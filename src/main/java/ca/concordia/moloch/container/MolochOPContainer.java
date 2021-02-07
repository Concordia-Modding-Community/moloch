package ca.concordia.moloch.container;

import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.tileentity.MolochInventory;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class MolochOPContainer extends AbstractInventoryContainer<MolochTileEntity> {
    public MolochOPContainer(int windowId, PlayerInventory playerInventory, MolochTileEntity tileEntity) {
        super(ModContainers.MOLOCH_OP.get(), playerInventory, tileEntity, windowId);

        System.out.println("Health: " + tileEntity.getHealth());
    }

    public MolochOPContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, (MolochTileEntity) getTileEntity(playerInventory, data));
    }

    public MolochTileEntity getTitleEntity() {
        return this.tileEntity;
    }

    @Override
    protected void addMainInventory(MolochTileEntity tileEntity) {
        MolochInventory molochInventory = tileEntity.getMolochInventory();

        this.addSlot(new Slot(molochInventory, 0, 62, 34));
        this.addSlot(new Slot(molochInventory, 1, 80, 34));
        this.addSlot(new Slot(molochInventory, 2, 98, 34));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.canInteractWithCallable, playerIn, ModBlocks.MOLOCH.get());
    }
}
