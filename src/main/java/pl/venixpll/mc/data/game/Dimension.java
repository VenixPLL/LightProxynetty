package pl.venixpll.mc.data.game;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Dimension {

    NETHER(-1), OVERWORLD(0), END(1);

    private int id;

    Dimension(int id) {

        this.id = id;
    }

    public static Dimension getById(int id){
        return Arrays.asList(Dimension.values()).stream().filter(gm -> gm.id == id).findFirst().orElse(Dimension.OVERWORLD);
    }
}
