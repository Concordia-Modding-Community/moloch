package ca.concordia.moloch.container;

import java.util.Optional;

import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.tileentity.MolochInventory;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.State.StateInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class MolochOPContainer extends AbstractInventoryContainer<MolochTileEntity> {
    public MolochOPContainer(int windowId, PlayerInventory playerInventory, MolochTileEntity tileEntity) {
        super(ModContainers.MOLOCH_OP.get(), playerInventory, tileEntity, windowId);
    }

    public MolochOPContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, (MolochTileEntity) getTileEntity(playerInventory, data));
    }

    public MolochTileEntity getTitleEntity() {
        return this.tileEntity;
    }

    @Override
    protected void addMainInventory(MolochTileEntity tileEntity) {
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.canInteractWithCallable, playerIn, ModBlocks.MOLOCH.get());
    }

    @Override
    protected void addPlayerInventory(PlayerInventory playerInventory, int x, int y) {
    }

    @Override
    protected void addInventoryHotbar(PlayerInventory playerInventory, int x, int y) {
    }
}
