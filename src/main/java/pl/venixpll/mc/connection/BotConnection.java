package pl.venixpll.mc.connection;

import com.darkmagician6.eventapi.EventManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.events.BotConnectedEvent;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.netty.NettyCompressionCodec;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Bot;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientKeepAlivePacket;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerKeepAlivePacket;
import pl.venixpll.utils.LazyLoadBase;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Data
@RequiredArgsConstructor
public class BotConnection implements IConnector {

    private Channel channel;
    private EnumConnectionState connectionState = EnumConnectionState.LOGIN;
    private boolean connected;
    private final Bot owner;

    private final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENT_LOOP_PING = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };

    @Override
    public void connect(String host, int port, Proxy proxy) {
        final Bootstrap bootstrap  = new Bootstrap()
                .group(CLIENT_NIO_EVENT_LOOP_PING.getValue())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        if(proxy != Proxy.NO_PROXY) {
                            pipeline.addFirst(new Socks4ProxyHandler(proxy.address()));
                        }
                        pipeline.addLast("timer",new ReadTimeoutHandler(20));
                        pipeline.addLast("frameCodec",new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec",new NettyPacketCodec(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                final BotConnectedEvent event = new BotConnectedEvent(owner);
                                EventManager.call(event);
                                if(!event.isCancelled()) {
                                    TimeUnit.MILLISECONDS.sleep(150);
                                    sendPacket(new HandshakePacket(47, "", port, 2));
                                    sendPacket(new ClientLoginStartPacket(owner.getUsername()));
                                }
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                connected = false;
                                owner.disconnected();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if(packet instanceof ServerLoginSetCompressionPacket){
                                    setCompressionThreshold(((ServerLoginSetCompressionPacket) packet).getThreshold());
                                }else if(packet instanceof ServerLoginSuccessPacket){
                                    setConnectionState(EnumConnectionState.PLAY);
                                    connected = true;
                                }else if(packet instanceof ServerKeepAlivePacket){
                                    sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) packet).getKeepaliveId()));
                                }
                                owner.packetReceived(packet);
                            }
                        });
                    }
                });
        this.channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
        this.channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        this.channel.config().setOption(ChannelOption.IP_TOS, 0x18);
    }

    public void setConnectionState(final EnumConnectionState state){
        ((NettyPacketCodec)channel.pipeline().get("packetCodec")).setConnectionState(state);
        connectionState = state;
    }

    public void setCompressionThreshold(final int threshold){
        if(connectionState == EnumConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
            } else {
                ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        }else{
            throw new UnsupportedOperationException();
        }
    }

    public void close(){
        connected = false;
        this.channel.close();
    }

    public void sendPacket(final Packet packet){
        channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

}
