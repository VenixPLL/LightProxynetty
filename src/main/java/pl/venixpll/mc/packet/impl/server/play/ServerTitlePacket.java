package pl.venixpll.mc.packet.impl.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.game.TitleAction;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Getter
public class ServerTitlePacket extends Packet {

    {
        this.setPacketID(0x45);
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
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(titleAction.getId());
        switch(titleAction){
            case TITLE:
                out.writeString(this.title.toJsonString());
                break;
            case SUBTITLE:
                out.writeString(this.subTitle.toJsonString());
                break;
            case TIMES:
                out.writeInt(this.fadeIn);
                out.writeInt(this.stay);
                out.writeInt(this.fadeOut);
                break;
            default:
                break;
        }
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.titleAction = TitleAction.getById(in.readVarIntFromBuffer());
        switch(titleAction){
            case TITLE:
                this.title = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case SUBTITLE:
                this.subTitle = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case TIMES:
                this.fadeIn = in.readInt();
                this.stay = in.readInt();
                this.fadeOut = in.readInt();
                break;
            default:
                break;
        }
    }
}
