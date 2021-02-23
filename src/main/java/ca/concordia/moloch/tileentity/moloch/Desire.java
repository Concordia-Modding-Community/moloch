package ca.concordia.moloch.tileentity.moloch;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Desire implements INBTSerializable<CompoundNBT> {
    private ItemStack item;
    private int count;

    public Desire() {
        this(ItemStack.EMPTY, 0);
    }

    public Desire(ItemStack item, int count) {
        this.item = item;
        this.count = count;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getItemCount() {
        return this.count;
    }

    public boolean isComplete() {
        return this.count == 0;
    }

    public boolean consume(ItemStack itemStack) {
        if(count <= 0 || itemStack.getCount() <= 0) {
            return false;
        }

        if(!itemStack.isItemEqual(this.item)) {
            return false;
        }

        itemStack.setCount(itemStack.getCount() - 1);
        this.count--;

        return true;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        
        nbt.put("item", this.item.serializeNBT());
        nbt.putInt("count", this.count);

        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("item")) {
            this.item = ItemStack.EMPTY;
            this.item.deserializeNBT(nbt.getCompound("item"));
        }

        if(nbt.contains("count")) {
            this.count = nbt.getInt("count");
        }
    }
}
