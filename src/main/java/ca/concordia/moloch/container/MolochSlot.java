package ca.concordia.moloch.container;

import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class MolochSlot extends Slot {
    public MolochSlot(MolochTileEntity tileEntity, int index, int xPosition, int yPosition) {
        super(tileEntity.getMolochInventory(), index, xPosition, yPosition);
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
