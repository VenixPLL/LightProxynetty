/*
 * LightProxy
 * Copyright (C) 2021.  VenixPLL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
public class ServerPinger implements IConnector {

    private final Player owner;
    private final boolean showResult;
    private final IConnector otherConnection;
    private final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENT_LOOP_PING = new LazyLoadBase<NioEventLoopGroup>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Pinger IO #%d").setDaemon(true).build());
        }
    };
    private Channel channel;

    @Override
    public void connect(String host, int port, Proxy proxy) {
        final Bootstrap bootstrap = new Bootstrap()
                .group(CLIENT_NIO_EVENT_LOOP_PING.getValue())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        if (proxy != Proxy.NO_PROXY) {
                            pipeline.addFirst(new Socks4ProxyHandler(proxy.address()));
                        }
                        pipeline.addLast("timer", new ReadTimeoutHandler(20));
                        pipeline.addLast("frameCodec", new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new NettyPacketCodec(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                if (showResult) {
                                    owner.sendChatMessage("&aPinging...");
                                }
                                TimeUnit.MILLISECONDS.sleep(150);
                                sendPacket(new HandshakePacket(47, host, port, 1));
                                sendPacket(new ClientStatusRequestPacket());
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if (packet instanceof ServerStatusResponsePacket) {
                                    if (showResult) {
                                        final ServerStatusInfo info = ((ServerStatusResponsePacket) packet).getStatusInfo();
                                        owner.sendChatMessage("&6Online&8: &7%s&8/&7%s", info.getPlayers().getOnlinePlayers(), info.getPlayers().getMaxPlayers());
                                        owner.sendChatMessage("&6Motd&8: &f" + info.getDescription().getFullText());
                                        owner.sendChatMessage("&6Version&8: &6%s &8(&f%s&8)", info.getVersion().getProtocol(), info.getVersion().getName());
                                    }
                                    channel.close();
                                    if (otherConnection != null) {
                                        otherConnection.connect(host, port, proxy);
                                    }
                                } else if (packet instanceof ServerStatusPongPacket) {
                                    channel.close();
                                }
                            }
                        });
                    }
                });
        this.channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
        this.channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        this.channel.config().setOption(ChannelOption.IP_TOS, 0x18);
    }

    public void sendPacket(final Packet packet) {
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
