package pl.venixpll.mc.data.status;

import lombok.Data;

@Data
public class VersionInfo {
    private String name;
    private int protocol;

    public VersionInfo(final String name, final int protocol) {
        this.name = name;
        this.protocol = protocol;
    }
}
