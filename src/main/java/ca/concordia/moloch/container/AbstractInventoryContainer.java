package ca.concordia.moloch.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractInventoryContainer<U extends TileEntity> extends Container {
    public static int SLOT_SIZE = 18;
    protected final U tileEntity;
    protected final IWorldPosCallable canInteractWithCallable;

    public AbstractInventoryContainer(ContainerType<?> containerType, PlayerInventory playerInventory, U tileEntity, int windowId) {
        super(containerType, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        addMainInventory(tileEntity);
        addPlayerInventory(playerInventory, 8, 84);
        addInventoryHotbar(playerInventory, 8, 142);
    }

    protected abstract void addMainInventory(U tileEntity);

    protected void addSlotTable(IInventory inventory, int x, int y, int width, int height, int inventoryOffset) {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                this.addSlot(new Slot(inventory, inventoryOffset + (i * width) + j, x + (j * SLOT_SIZE), y + (i * SLOT_SIZE)));
            }
        }
    }

    protected void addPlayerInventory(PlayerInventory playerInventory, int x, int y) {
        this.addSlotTable(playerInventory, x, y, 9, 3, 9);
    }

    protected void addInventoryHotbar(PlayerInventory playerInventory, int x, int y) {
        this.addSlotTable(playerInventory, x, y, 9, 1, 0);
    }

    protected static TileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        if (playerInventory == null)
            throw new RuntimeException("Player inventory cannot be null.");
        
        if (data == null)
            throw new RuntimeException("Data cannot be null.");

        PlayerEntity playerEntity = playerInventory.player;

        if(playerEntity == null)
            throw new RuntimeException("No player attached to inventory.");

        World world = playerEntity.world;

        if(world == null)
            throw new RuntimeException("No world attached to player.");
    

        BlockPos blockPos = data.readBlockPos();

        if(blockPos == null)
            throw new RuntimeException("No blockpos in data.");

        TileEntity tileEntity = world.getTileEntity(blockPos);

        return tileEntity;
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 1) {
                if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
