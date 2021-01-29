package ca.concordia.moloch.tileentity;

import ca.concordia.moloch.init.ModTileEntities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MolochTileEntity extends LockableLootTileEntity {
    public MolochTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public MolochTileEntity() {
        super(ModTileEntities.MOLOCH.get());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tileentity.moloch.moloch");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }
}
