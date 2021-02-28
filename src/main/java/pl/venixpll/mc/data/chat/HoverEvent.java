package pl.venixpll.mc.data.chat;


import java.util.Objects;

public class HoverEvent implements Cloneable {
    private HoverAction action;
    private Message value;

    public HoverEvent(HoverAction action, Message value) {
        this.action = action;
        this.value = value;
    }

    public HoverAction getAction() {
        return this.action;
    }

    public Message getValue() {
        return this.value;
    }

    @Override
    public HoverEvent clone() {
        return new HoverEvent(this.action, this.value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoverEvent)) return false;

        HoverEvent that = (HoverEvent) o;
        return this.action == that.action &&
                Objects.equals(this.value, that.value);
    }

}
