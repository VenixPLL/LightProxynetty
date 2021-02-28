package pl.venixpll.mc.data.item;

import net.minecraft.nbt.NBTTagCompound;

public class ItemStack {
    private int id;
    private int amount;
    private int data;
    private NBTTagCompound nbt;

    public ItemStack(final int id) {
        this.id = id;
        this.amount = 1;
    }

    public ItemStack(final int id, final int amount) {
        this.id = id;
        this.amount = amount;
        this.data = 0;
    }

    public ItemStack(final int id, final int amount, final int data) {
        this.id = id;
        this.amount = amount;
        this.data = data;
    }

    public ItemStack(final int id, final int amount, final int data, final NBTTagCompound nbt) {
        this.id = id;
        this.amount = amount;
        this.data = data;
        this.nbt = nbt;
    }

    public int getId() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getData() {
        return this.data;
    }

    public NBTTagCompound getNBT() {
        return this.nbt;
    }
}
