package pl.venixpll.mc.data.chat;

public enum ClickAction {
    RUN_COMMAND,
    SUGGEST_COMMAND,
    OPEN_URL,
    OPEN_FILE;

    public static ClickAction byName(String name) {
        name = name.toLowerCase();
        for (ClickAction action : values()) {
            if (action.toString().equals(name)) {
                return action;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
