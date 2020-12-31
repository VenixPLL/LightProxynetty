package pl.venixpll.mc.packet.impl.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.game.TitleAction;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Getter
public class ServerTitlePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x45, 47));
        this.getProtocolList().add(new Protocol(0x45, 110));
        this.getProtocolList().add(new Protocol(0x48, 340));
    }

    private TitleAction titleAction;
    private Message title;
    private Message subTitle;

    private int fadeIn;
    private int fadeOut;
    private int stay;

    public ServerTitlePacket(TitleAction action, String message){
        this.titleAction = action;
        if(action == TitleAction.TITLE){
            this.title = Message.fromString(LogUtil.fixColor(message));
        }else if(action == TitleAction.SUBTITLE){
            this.subTitle = Message.fromString(LogUtil.fixColor(message));
        }else{
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket!");
        }
    }

    public ServerTitlePacket(TitleAction action,int fadeIn,int stay,int fadeOut){
        this.titleAction = action;
        if(titleAction == TitleAction.TIMES){
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }else{
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket");
        }
    }

    public ServerTitlePacket(TitleAction action){
        this.titleAction = action;
    }



    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if(titleAction == TitleAction.TIMES && protocol == 340) {
            out.writeVarIntToBuffer(3);
        } else {
            out.writeVarIntToBuffer(titleAction.getId());
        }
        if(titleAction == TitleAction.TITLE) {
            out.writeString(this.title.toJsonString());
        } else if (titleAction == TitleAction.SUBTITLE) {
            out.writeString(this.subTitle.toJsonString());
        }
        if(titleAction == TitleAction.TIMES) {
            out.writeInt(this.fadeIn);
            out.writeInt(this.stay);
            out.writeInt(this.fadeOut);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        int t = in.readVarIntFromBuffer();

        if(t == 3 && protocol == 340) {
            this.titleAction = TitleAction.TIMES;
        } else {
            this.titleAction = TitleAction.getById(t);
        }

        if(titleAction == TitleAction.TITLE) {
            this.title = Message.fromString(in.readStringFromBuffer(32767));
        } else if (titleAction == TitleAction.SUBTITLE) {
            this.subTitle = Message.fromString(in.readStringFromBuffer(32767));
        } else if (titleAction == TitleAction.TIMES) {
            this.fadeIn = in.readInt();
            this.stay = in.readInt();
            this.fadeOut = in.readInt();
        }
    }
}
