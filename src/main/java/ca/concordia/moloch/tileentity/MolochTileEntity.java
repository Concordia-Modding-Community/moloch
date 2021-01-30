package ca.concordia.moloch.tileentity;

import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MolochTileEntity extends LockableLootTileEntity implements ITickableTileEntity {
    private static final int INVENTORY_SIZE = 4;
    public static final float MAX_HEALTH = 10000f;

    private NonNullList<ItemStack> contents = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
    private float health = MAX_HEALTH;

    public MolochTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public MolochTileEntity() {
        super(ModTileEntities.MOLOCH.get());
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }

    public float getItemProgress() {
        ItemStack playerItem = contents.get(0);
        float maxProgress = 0;

        for(int i = 1; i < INVENTORY_SIZE; i++) {
            ItemStack targetItem = contents.get(i);

            if(playerItem.isItemEqual(targetItem)) {
                maxProgress = Math.max(maxProgress, (float) playerItem.getCount() / (float) targetItem.getCount());
            }
        }

        return Math.min(1, maxProgress);
    }

    public float getHealth() {
        return this.health;
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.contents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.contents = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tileentity.moloch.moloch");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new MolochContainer(id, player, this);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);

        if(!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.contents);
        }

        nbt.putFloat("health", this.health);

        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.contents);
        }

        if(nbt.contains("health")) {
            this.health = nbt.getFloat("health");
        }
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();

        if (this.itemHandler == null)
            return;

        this.itemHandler.invalidate();
        this.itemHandler = null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return this.itemHandler.cast();
        return super.getCapability(cap);
    }

    @Override
    public void remove() {
        super.remove();

        if (itemHandler != null)
            itemHandler.invalidate();
    }

    @Override
    public void tick() {
        World world = this.getWorld();

        // Removes stack if objective complete.
        if (this.getItemProgress() >= 1) {
            ItemStack playerItem = contents.get(0);

            for(int i = 1; i < INVENTORY_SIZE; i++) {
                ItemStack targetItem = contents.get(i);
    
                if(playerItem.isItemEqual(targetItem) && playerItem.getCount() >= targetItem.getCount()) {
                    playerItem.setCount(playerItem.getCount() - targetItem.getCount());

                    targetItem.setCount(Math.min(targetItem.getMaxStackSize(), targetItem.getCount() + 1));

                    this.health = MAX_HEALTH;

                    break;
                }
            }
        } 

        // Reduces health every tick.
        this.health = Math.max(0, this.health - 1f);

        // TODO: Fix particle emission + add engineery/organic sounds.
        if (!world.isRemote) {
            BlockPos blockPos = this.getPos();

            world.addParticle(ParticleTypes.EXPLOSION, blockPos.getX(), blockPos.getY() + 3, blockPos.getZ(), 0, 0,
                    0);
        }

        // TODO: Add health related warnings + effects.
        // TODO: Switch textures with changing health.
    }
}
