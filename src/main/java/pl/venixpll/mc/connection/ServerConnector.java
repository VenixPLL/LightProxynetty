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
import pl.venixpll.mc.netty.NettyCompressionCodec;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.netty.NettyVarInt21FrameCodec;
import pl.venixpll.mc.objects.Player;
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
public class ServerConnector implements IConnector {

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
    private Channel channel;
    private EnumConnectionState connectionState = EnumConnectionState.LOGIN;
    private boolean connected;
    private double lastTps;
    private long lastPacketTime = 0L;

    @Override
    public void connect(String host, int port, Proxy proxy) {
        final Bootstrap bootstrap = new Bootstrap()
                .group(CLIENT_NIO_EVENT_LOOP_PING.getValue())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 0x18)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        if (proxy != Proxy.NO_PROXY) {
                            pipeline.addFirst(new Socks4ProxyHandler(proxy.address()));
                        }
                        pipeline.addLast("timer", new ReadTimeoutHandler(20));
                        pipeline.addLast("frameCodec", new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new NettyPacketCodec(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                owner.sendChatMessage("&aConnecting...");
                                TimeUnit.MILLISECONDS.sleep(150);
                                sendPacket(new HandshakePacket(47, host, port, 2));
                                sendPacket(new ClientLoginStartPacket(username));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                if (connected) {
                                    owner.sendChatMessage("&cServer disconnected directly!");
                                }
                                connected = false;
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                owner.setLastPacket(packet.getClass().getSimpleName());
                                owner.setPacketID(packet.getPacketID());
                                if (packet instanceof ServerLoginSetCompressionPacket) {
                                    setCompressionThreshold(((ServerLoginSetCompressionPacket) packet).getThreshold());
                                } else if (packet instanceof ServerLoginEncryptionRequestPacket) {
                                    owner.sendChatMessage("&cProxy does not support Minecraft Premium!");
                                    close();
                                } else if (packet instanceof ServerLoginSuccessPacket) {
                                    setConnectionState(EnumConnectionState.PLAY);
                                    owner.sendChatMessage("&aLogged in!");
                                } else if (packet instanceof ServerJoinGamePacket) {
                                    WorldUtils.dimSwitch(owner, (ServerJoinGamePacket) packet);
                                    connected = true;
                                    owner.sendChatMessage("&aConnected!");
                                } else if (packet instanceof ServerDisconnectPacket) {
                                    connected = false;
                                    WorldUtils.emptyWorld(owner);
                                    owner.sendChatMessage("&cDisconnected!");
                                    owner.sendChatMessage("&f" + ((ServerDisconnectPacket) packet).getReason().getFullText());
                                } else if (packet instanceof ServerLoginDisconnectPacket) {
                                    connected = false;
                                    owner.sendChatMessage("&cDisconnected during login!");
                                    owner.sendChatMessage("&f" + ((ServerLoginDisconnectPacket) packet).getReason().getFullText());
                                } else if (packet instanceof ServerKeepAlivePacket) {
                                    sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) packet).getKeepaliveId()));
                                } else if (connected && connectionState == EnumConnectionState.PLAY) {
                                    if (packet instanceof ServerTimeUpdatePacket) {
                                        tpstimes.add(Math.max(1000, tpsTimer.getTime()));
                                        long timesAdded = 0;
                                        if (tpstimes.size() > 5) {
                                            tpstimes.remove(0);
                                        }
                                        for (long l : tpstimes) {
                                            timesAdded += l;
                                        }
                                        long roundedTps = timesAdded / tpstimes.size();
                                        lastTps = (20.0 / roundedTps) * 1000.0;
                                        tpsTimer.reset();
                                    }
                                    lastPacketTime = System.currentTimeMillis();
                                    owner.sendPacket(packet);
                                }
                                if (owner.isListenChunks()){
                                    if (packet.getPacketID() == 0x26){
                                        byte[] data = packet.getCustomData();
                                        owner.getBytes().add(data);
                                    }
                                }
                            }
                        });
                    }
                });
        this.channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
    }

    public void setConnectionState(final EnumConnectionState state) {
        ((NettyPacketCodec) channel.pipeline().get("packetCodec")).setConnectionState(state);
        connectionState = state;
    }

    public void setCompressionThreshold(final int threshold) {
        if (connectionState == EnumConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
            } else {
                ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void close() {
        connected = false;
        this.channel.close();
    }

    public void sendPacket(final Packet packet) {
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
