package ca.concordia.moloch.container;

import java.util.Optional;

import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.Progression;
import ca.concordia.moloch.tileentity.moloch.State.StateInventory;
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

        Progression progression = tileEntity.getProgression();

        Optional<StateInventory> stateInventory = progression.getInventory();

        if(!stateInventory.isPresent()) {
            return;
        }

        IInventory inventory = stateInventory.get();

        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            this.addSlot(new Slot(inventory, i, 113, 17 + i * 18));
        }
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
