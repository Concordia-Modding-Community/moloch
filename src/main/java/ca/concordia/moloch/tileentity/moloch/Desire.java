package ca.concordia.moloch.tileentity.moloch;

import com.mojang.brigadier.StringReader;

import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Desire implements INBTSerializable<CompoundNBT> {
    private String item;
    private int count;

    public Desire() {
        this("", 0);
    }

    public Desire(String item, int count) {
        this.item = item;
        this.count = count;
    }

    public Desire(Item item, int count) {
        this(item.getRegistryName().toString(), count);
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemString() {
        return this.item;
    }

    public Item getItem() {
        ItemParser itemParser = new ItemParser(new StringReader(this.item), true);

        try {
            itemParser.readItem();

            Item item = itemParser.getItem();
        
            return item;
        } catch(Exception e) {
            return Items.AIR;
        }
    }

    public ItemStack getItemStack() {
        if(this.count == 0) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(() -> this.getItem(), 1);
    }

    public boolean isComplete() {
        return this.count == 0;
    }

    public boolean consume(ItemStack itemStack) {
        if(count <= 0 || itemStack.getCount() <= 0) {
            return false;
        }

        if(!this.getItem().equals(itemStack.getItem())) {
            return false;
        }

        if(this.getItem().equals(Items.AIR)) {
            return false;
        }

        itemStack.setCount(itemStack.getCount() - 1);
        this.count--;

        return true;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        
        nbt.putString("item", this.item);
        nbt.putInt("count", this.count);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("item")) {
            this.item = nbt.getString("item");
        }

        if(nbt.contains("count")) {
            this.count = nbt.getInt("count");
        }
    }
}
