package pl.venixpll.mc.data.chat;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MessagePosition {

    CHATBOX(0),SYSTEM(1),HOTBAR(2);

    private int id;

    MessagePosition(int id){
        this.id = id;
    }

    public static MessagePosition getById(int id){
        Optional<MessagePosition> positionOptional = Arrays.stream(MessagePosition.values()).filter(pos -> pos.getId() == id).findFirst();
        if(positionOptional.isPresent()){
            return positionOptional.get();
        }else{
            return CHATBOX;
        }
    }
}
