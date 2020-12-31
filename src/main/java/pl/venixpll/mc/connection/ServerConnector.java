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
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.objects.Session;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientKeepAlivePacket;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginDisconnectPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginEncryptionRequestPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerDisconnectPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerJoinGamePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerKeepAlivePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerTimeUpdatePacket;
import pl.venixpll.utils.LazyLoadBase;
import pl.venixpll.utils.WaitTimer;
import pl.venixpll.utils.WorldUtils;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Data
public class ServerConnector {

    private final Player owner;
    private final String username;

    private final ArrayList<Long> tpstimes = new ArrayList<>();
    private final WaitTimer tpsTimer = new WaitTimer();

    private final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENT_LOOP_PING = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
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
                        pipeline.addLast("packetCodec",new NettyPacketCodec(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                owner.sendChatMessage("&aConnecting...");
                                TimeUnit.MILLISECONDS.sleep(150);
                                owner.getRemoteSession().sendPacket(new HandshakePacket(owner.getSession().getProtocolID(),host,port,2));
                                owner.getRemoteSession().sendPacket(new ClientLoginStartPacket(username));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                if(owner.isConnected()){
                                    owner.sendChatMessage("&cServer disconnected directly!");
                                }
                                owner.setConnected(false);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if(packet instanceof ServerLoginSetCompressionPacket) {
                                    owner.getRemoteSession().setCompressionThreshold(((ServerLoginSetCompressionPacket) packet).getThreshold());
                                }else if(packet instanceof ServerLoginEncryptionRequestPacket){
                                    owner.sendChatMessage("&cProxy does not support Minecraft Premium!");
                                    owner.setConnected(false);
                                    owner.getRemoteSession().getChannel().close();
                                }else if(packet instanceof ServerLoginSuccessPacket){
                                    owner.getRemoteSession().setConnectionState(EnumConnectionState.PLAY);
                                    owner.sendChatMessage("&aLogged in!");
                                }else if(packet instanceof ServerJoinGamePacket) {
                                    owner.setConnected(true);
                                    WorldUtils.dimSwitch(owner, (ServerJoinGamePacket) packet);
                                    owner.sendChatMessage("&aConnected!");
                                }else if(packet instanceof ServerDisconnectPacket) {
                                    owner.setConnected(false);
                                    WorldUtils.emptyWorld(owner);
                                    owner.sendChatMessage("&cDisconnected!");
                                    owner.sendChatMessage("&f" + ((ServerDisconnectPacket) packet).getReason().getFullText());
                                }else if(packet instanceof ServerLoginDisconnectPacket){
                                    owner.setConnected(false);
                                    owner.sendChatMessage("&cDisconnected during login!");
                                    owner.sendChatMessage("&f" + ((ServerLoginDisconnectPacket) packet).getReason().getFullText());
                                }else if(packet instanceof ServerKeepAlivePacket){
                                    owner.getRemoteSession(). sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) packet).getKeepaliveId()));
                                }else if(owner.isConnected() && owner.getRemoteSession().getConnectionState() == EnumConnectionState.PLAY){
                                    if(packet instanceof ServerTimeUpdatePacket) {
                                        tpstimes.add(Math.max(1000, tpsTimer.getTime()));
                                        long timesAdded = 0;
                                        if (tpstimes.size() > 5) {
                                            tpstimes.remove(0);
                                        }
                                        for (long l : tpstimes) {
                                            timesAdded += l;
                                        }
                                        long roundedTps = timesAdded / tpstimes.size();
                                        owner.setLastTps((20.0 / roundedTps) * 1000.0);
                                        tpsTimer.reset();
                                    }
                                    owner.getSession().sendPacket(packet);
                                }
                            }
                        });
                    }
                });
        owner.setRemoteSession(new Session(bootstrap.connect(host, port).syncUninterruptibly().channel()));
        owner.getRemoteSession().setProtocolID(owner.getSession().getProtocolID());
        owner.getRemoteSession().setConnectionState(EnumConnectionState.LOGIN);
        owner.getRemoteSession().getChannel().config().setOption(ChannelOption.TCP_NODELAY, true);
        owner.getRemoteSession().getChannel().config().setOption(ChannelOption.IP_TOS, 0x18);
        owner.getRemoteSession().setUsername(username);
    }
}
