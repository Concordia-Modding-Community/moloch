package ca.concordia.moloch.container;

import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MolochContainer extends AbstractInventoryContainer<MolochTileEntity> {
    public MolochContainer(int windowId, PlayerInventory playerInventory, MolochTileEntity tileEntity) {
        super(ModContainers.MOLOCH.get(), playerInventory, tileEntity, windowId);
    }

    public MolochContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, (MolochTileEntity) getTileEntity(playerInventory, data));
    }

    public MolochTileEntity getTitleEntity() {
        return this.tileEntity;
    }

    @Override
    protected void addMainInventory(MolochTileEntity tileEntity) {
        this.addSlot(new Slot(tileEntity, 0, 50, 34));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.canInteractWithCallable, playerIn, ModBlocks.MOLOCH.get());
    }

    public static class DisplaySlot extends Slot {
        public DisplaySlot(IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
        }
    
        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    
        @Override
        public boolean canTakeStack(PlayerEntity playerIn) {
            return false;
        }
    }
}
