package pl.venixpll.mc.objects;

import com.mojang.authlib.properties.PropertyMap;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class GameProfile {

    private final PropertyMap properties = new PropertyMap();
    private String username;
    private UUID uuid;

}
