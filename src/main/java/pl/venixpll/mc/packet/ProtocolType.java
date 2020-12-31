package pl.venixpll.mc.packet;

import java.util.Arrays;

public enum ProtocolType {
    PROTOCOL_NONE(0, "NONE"),
    PROTOCOL_1_8_X(47, "1.8"),
    PROTOCOL_1_9_4(110, "1.9.4"),
    PROTOCOL_1_12_2(340, "1.12.2");

    private final int protocol;
    private final String prefix;

    ProtocolType(int protocol, String prefix) {
        this.protocol = protocol;
        this.prefix = prefix;
    }

    public String getPrefix() { return prefix; }

    public int getProtocol() { return protocol; }

    public static ProtocolType getByProtocolID(int protocol) {
        return Arrays.stream(values()).filter(p -> p.protocol == protocol).findFirst().orElse(PROTOCOL_NONE);
    }
}