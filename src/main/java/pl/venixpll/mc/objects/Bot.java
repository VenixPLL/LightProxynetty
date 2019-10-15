package pl.venixpll.mc.objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.mc.packet.Packet;

@RequiredArgsConstructor
@Data
public class Bot {

    private final String username;
    private Channel channel;

    public void sendPacket(final Packet packet){
        channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
