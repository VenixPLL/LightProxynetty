package pl.venixpll.mc.connection;

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
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.data.status.ServerStatusInfo;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.objects.Session;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.status.ClientStatusRequestPacket;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusPongPacket;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusResponsePacket;
import pl.venixpll.utils.LazyLoadBase;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Data
public class ServerPinger {

    private final Player owner;
    private final boolean showResult;
    private Session session;

    private final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENT_LOOP_PING = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Pinger IO #%d").setDaemon(true).build());
        }
    };

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
                        pipeline.addLast("packetCodec",new NettyPacketCodec(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                if(showResult) {
                                    owner.sendChatMessage("&aPinging...");
                                }
                                TimeUnit.MILLISECONDS.sleep(150);
                                session.sendPacket(new HandshakePacket(session.getProtocolID(),host,port,1));
                                session.sendPacket(new ClientStatusRequestPacket());
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if(packet instanceof ServerStatusResponsePacket){
                                    if(showResult) {
                                        final ServerStatusInfo info = ((ServerStatusResponsePacket) packet).getStatusInfo();
                                        owner.sendChatMessage("&6Online&8: &7%s&8/&7%s", info.getPlayers().getOnlinePlayers(), info.getPlayers().getMaxPlayers());
                                        owner.sendChatMessage("&6Motd&8: &f" + info.getDescription().getFullText());
                                        owner.sendChatMessage("&6Version&8: &6%s &8(&f%s&8)", info.getVersion().getProtocol(), info.getVersion().getName());
                                    }
                                    session.getChannel().close();
                                }else if(packet instanceof ServerStatusPongPacket){
                                    session.getChannel().close();
                                }
                            }
                        });
                    }
                });
        session = new Session(bootstrap.connect(host, port).syncUninterruptibly().channel());
        session.setProtocolID(owner.getSession().getProtocolID());
        session.getChannel().config().setOption(ChannelOption.TCP_NODELAY, true);
        session.getChannel().config().setOption(ChannelOption.IP_TOS, 0x18);
    }
}
