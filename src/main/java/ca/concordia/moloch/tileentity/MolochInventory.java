package ca.concordia.moloch.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;

public class MolochInventory implements IInventory, INBTSerializable<CompoundNBT> {
    private static final int REQUEST_PAGE_SIZE = 3;

    private MolochTileEntity molochTileEntity;
    private NonNullList<ItemStack> requests;

    public MolochInventory(MolochTileEntity molochTileEntity, int inventorySize) {
        this(molochTileEntity, NonNullList.withSize(inventorySize, ItemStack.EMPTY));
    }

    public MolochInventory(MolochTileEntity molochTileEntity, NonNullList<ItemStack> requests) {
        this.requests = requests;
        this.molochTileEntity = molochTileEntity;
    }

    public NonNullList<ItemStack> getStack() {
        return this.requests;
    }

    public void deltaOffset(int delta) {
        NonNullList<ItemStack> shiftedList = NonNullList.withSize(this.requests.size(), ItemStack.EMPTY);

        int listSize = this.requests.size();

        if(delta > 0) {
            for(int i = 0; i < listSize; i++) {
                shiftedList.set(i, this.requests.get((i + REQUEST_PAGE_SIZE) % listSize));
            }
        } else if (delta < 0) {
            for(int i = 0; i < listSize; i++) {
                shiftedList.set((i + REQUEST_PAGE_SIZE) % listSize, this.requests.get(i));
            }
        }

        this.requests = shiftedList;

        this.markDirty();
    }

    @Override
    public void clear() { 
        this.requests = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public int getSizeInventory() {
        return requests.size();
    }

    @Override
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return requests.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.requests.get(index).split(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.requests.get(index);

        this.requests.set(index, ItemStack.EMPTY);

        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.requests.set(index, stack);
    }

    @Override
    public void markDirty() {
        this.molochTileEntity.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ItemStackHelper.saveAllItems(nbt, this.requests);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.requests.clear();

        ItemStackHelper.loadAllItems(nbt, this.requests);
    }
}
