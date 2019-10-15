package pl.venixpll.mc.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class GameProfile {

    private String username;
    private UUID uuid;

}
