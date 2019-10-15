package pl.venixpll.mc.data.game;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gamemode {

    SURVIVAL(0),CREATIVE(1),ADVENTURE(2),SPECTATOR(3),HARDCORE(0x8);

    private int id;

    Gamemode(int id){
        this.id = id;
    }

    public static Gamemode getById(int id){
        return Arrays.asList(Gamemode.values()).stream().filter(gm -> gm.id == id).findFirst().orElse(Gamemode.SURVIVAL);
    }
}
