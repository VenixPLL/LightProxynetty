package pl.venixpll.mc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.managers.PlayerManager;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.data.status.ServerStatusInfo;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.objects.Session;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.server.play.ServerKeepAlivePacket;
import pl.venixpll.utils.LazyLoadBase;
import pl.venixpll.utils.LogUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Data
public class MinecraftServer {

    private final int port;

    private final LazyLoadBase<NioEventLoopGroup> NETTY_CLIENT_IO = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };

    public final List<Player> playerList = new CopyOnWriteArrayList<>();
    public ServerStatusInfo statusInfo;

    public MinecraftServer bind(final String successMessage){
        new ServerBootstrap()
                .group(NETTY_CLIENT_IO.getValue())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast("timer",new ReadTimeoutHandler(10));
                        pipeline.addLast("varintCodec",new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec",new NettyPacketCodec(EnumConnectionState.HANDSHAKE, EnumPacketDirection.SERVERBOUND));

                        pipeline.addLast(new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                PlayerManager.createPlayer(new Session(ctx.channel()));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                PlayerManager.getPlayer(ctx.channel()).disconnected();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                PlayerManager.getPlayer(channelHandlerContext.channel()).packetReceived(packet);
                            }
                        });
                    }
                }).bind(port).addListener((ChannelFutureListener) channelFuture -> LogUtil.printMessage(successMessage,port));
        final ScheduledExecutorService keepAliveTask = Executors.newSingleThreadScheduledExecutor();
        keepAliveTask.scheduleAtFixedRate(() -> PlayerManager.getPlayers().stream().filter(p -> p.getSession().getConnectionState() == EnumConnectionState.PLAY).forEach(p -> {
            p.getSession().sendPacket(new ServerKeepAlivePacket(System.currentTimeMillis()));
        }),3,3, TimeUnit.SECONDS);
        return this;
    }

}
