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
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Bot;
import pl.venixpll.mc.objects.Session;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientKeepAlivePacket;
import pl.venixpll.mc.packet.impl.client.play.ClientPlayerPosLookPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientPluginMessagePacket;
import pl.venixpll.mc.packet.impl.client.play.ClientSettingsPacket;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerJoinGamePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerKeepAlivePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerPlayerPositionRotationPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerSpawnPositionPacket;
import pl.venixpll.utils.LazyLoadBase;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Data
@RequiredArgsConstructor
public class BotConnection {

    private final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENT_LOOP_PING = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };

    public void connect(String host, int port, Proxy proxy, Bot bot) {
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
                                final BotConnectedEvent event = new BotConnectedEvent(bot);
                                EventManager.call(event);
                                if(!event.isCancelled()) {
                                    TimeUnit.MILLISECONDS.sleep(150);
                                    bot.getSession().sendPacket(new HandshakePacket(bot.getSession().getProtocolID(), "", port, 2));
                                    bot.getSession().sendPacket(new ClientLoginStartPacket(bot.getUsername()));
                                }
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                bot.setConnected(false);
                                bot.disconnected();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if(packet instanceof ServerLoginSetCompressionPacket){
                                    bot.getSession().setCompressionThreshold(((ServerLoginSetCompressionPacket) packet).getThreshold());
                                } else if(packet instanceof ServerLoginSuccessPacket){
                                    bot.getSession().setConnectionState(EnumConnectionState.PLAY);
                                    bot.setConnected(true);
                                } else if(packet instanceof ServerKeepAlivePacket){
                                    bot.getSession().sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) packet).getKeepaliveId()));
                                }

                                final PacketReceivedEvent event = new PacketReceivedEvent(packet,this);
                                EventManager.call(event);
                                if(!event.isCancelled()){
                                    if(packet instanceof ServerJoinGamePacket){
                                        bot.setEntityId(((ServerJoinGamePacket) packet).getEntityId());
                                        bot.getOwner().sendChatMessage("&a%s Connected!",bot.getUsername());
                                        bot.getOwner().getBots().add(bot);
                                        bot.getSession().sendPacket(new ClientPluginMessagePacket("MC|Brand","vanilla".getBytes()));
                                        bot.getSession().sendPacket(new ClientSettingsPacket("pl_PL",(byte)32,(byte)0,true,(byte)1));
                                    }else if(packet instanceof ServerSpawnPositionPacket){
                                        //Helps in future movement of bot.
                                        bot.getSession().sendPacket(new ClientPlayerPosLookPacket(((ServerSpawnPositionPacket) packet).getPosition(),160,90,true));
                                    }else if(packet instanceof ServerPlayerPositionRotationPacket){
                                        //Helps in future movement of bot.
                                        final ServerPlayerPositionRotationPacket p = (ServerPlayerPositionRotationPacket) packet;
                                        bot.getSession().sendPacket(new ClientPlayerPosLookPacket(p.getPos(),p.getYaw(),p.getPitch(),p.isOnGround()));
                                    }
                                }
                            }
                        });
                    }
                });
        bot.setSession(new Session(bootstrap.connect(host, port).syncUninterruptibly().channel()));
        bot.getSession().setProtocolID(bot.getOwner().getSession().getProtocolID());
        bot.getSession().setConnectionState(EnumConnectionState.LOGIN);
        bot.getSession().getChannel().config().setOption(ChannelOption.TCP_NODELAY, true);
        bot.getSession().getChannel().config().setOption(ChannelOption.IP_TOS, 0x18);
    }
}
