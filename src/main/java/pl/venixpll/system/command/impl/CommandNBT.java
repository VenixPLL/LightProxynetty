package pl.venixpll.system.command.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.client.play.ClientCreativeInventoryAction;
import pl.venixpll.system.command.Command;

public class CommandNBT extends Command {

    public CommandNBT() {
        super(",nbt", "test", "");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final NBTTagList nbtTagList = new NBTTagList();
        nbtTagList.appendTag(new NBTTagString("TestNBT!"));
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("pages",nbtTagList);
        final ItemStack stack = new ItemStack(386,1,0,compound);
        sender.getConnector().sendPacket(new ClientCreativeInventoryAction(36,stack));
        sender.sendChatMessage("&cSent NBT!");
    }
}
