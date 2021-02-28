package pl.venixpll.mc.data.game;

import java.util.Arrays;

public enum WindowAction {
    CLICK_ITEM(0),
    SHIFT_CLICK_ITEM(1),
    MOVE_TO_HOTBAR_SLOT(2),
    CREATIVE_GRAB_MAX_STACK(3),
    DROP_ITEM(4),
    SPREAD_ITEM(5),
    FILL_STACK(6);

    private final int id;

    private WindowAction(final int id) {
        this.id = id;
    }

    public static WindowAction getActionById(final int id) {
        return Arrays.stream(values()).filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public int getId() {
        return this.id;
    }
}

