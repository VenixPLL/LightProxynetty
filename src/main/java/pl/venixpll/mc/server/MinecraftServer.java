package pl.venixpll.mc.server;

import com.darkmagician6.eventapi.EventManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.events.PlayerConnectedEvent;
import pl.venixpll.events.PlayerDisconnectedEvent;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.data.status.ServerStatusInfo;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.server.play.ServerKeepAlivePacket;
import pl.venixpll.utils.LazyLoadBase;
import pl.venixpll.utils.LogUtil;
import pl.venixpll.utils.WaitTimer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class MinecraftServer {

    public final List<Player> playerList = new CopyOnWriteArrayList<>();
    private final int port;
    private final LazyLoadBase<NioEventLoopGroup> NETTY_CLIENT_IO = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };
    public ServerStatusInfo statusInfo;

    public MinecraftServer bind(final String successMessage) {
        final ChannelFuture future = new ServerBootstrap()
                .group(NETTY_CLIENT_IO.getValue())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast("timer", new ReadTimeoutHandler(10));
                        pipeline.addLast("varintCodec", new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new NettyPacketCodec(EnumConnectionState.HANDSHAKE, EnumPacketDirection.SERVERBOUND));

                        Player player = new Player();

                        pipeline.addLast(new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                player.setUsername(ctx.channel().remoteAddress().toString());
                                player.setChannel(ctx.channel());
                                final PlayerConnectedEvent event = new PlayerConnectedEvent(player);
                                EventManager.call(event);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                final PlayerDisconnectedEvent event = new PlayerDisconnectedEvent(player);
                                EventManager.call(event);
                                player.disconnected();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                player.packetReceived(packet);
                            }
                        });
                    }
                }).bind(port).addListener((ChannelFutureListener) channelFuture -> LogUtil.printMessage(successMessage, port));
        final ScheduledExecutorService keepAliveTask = Executors.newSingleThreadScheduledExecutor();

        final WaitTimer keepAliveTimer = new WaitTimer();

        keepAliveTask.scheduleAtFixedRate(() -> {
            if (keepAliveTimer.hasTimeElapsed(3000, true)) {
                this.playerList.stream().filter(p -> p.getConnectionState() == EnumConnectionState.PLAY).collect(Collectors.toList()).forEach(p -> {
                    p.sendPacket(new ServerKeepAlivePacket((int) System.currentTimeMillis()));
                });
            }
            this.playerList.stream().filter(p -> p.getConnectionState() == EnumConnectionState.PLAY).collect(Collectors.toList()).forEach(p -> {
                p.tick();
            });
        }, 50, 50, TimeUnit.MILLISECONDS);
        return this;
    }

}
