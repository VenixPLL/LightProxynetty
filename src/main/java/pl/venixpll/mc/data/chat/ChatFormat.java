package pl.venixpll.mc.data.chat;

public enum ChatFormat {
    BOLD,
    UNDERLINED,
    STRIKETHROUGH,
    ITALIC,
    OBFUSCATED;

    public static ChatFormat byName(String name) {
        name = name.toLowerCase();
        for (ChatFormat format : values()) {
            if (format.toString().equals(name)) {
                return format;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}