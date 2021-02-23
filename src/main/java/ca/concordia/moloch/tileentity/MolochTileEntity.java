package ca.concordia.moloch.tileentity;

import javax.annotation.Nonnull;

import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.container.MolochOPContainer;
import ca.concordia.moloch.init.ModItems;
import ca.concordia.moloch.init.ModTileEntities;
import ca.concordia.moloch.init.ModPacketHandler;
import ca.concordia.moloch.network.UpdateMoloch;
import ca.concordia.moloch.tileentity.moloch.Progression;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MolochTileEntity extends LockableLootTileEntity implements ITickableTileEntity, IMarkDirty {
    private static final int INVENTORY_SIZE = 1;

    private NonNullList<ItemStack> contents = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    private Progression progression;

    public MolochTileEntity() {
        this(ModTileEntities.MOLOCH.get());
    }

    public MolochTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        this.progression = new Progression(this);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }

    @Override
    public int getSizeInventory() {
        return this.contents.size();
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
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        if(playerInventory.getCurrentItem().isItemEqual(new ItemStack(ModItems.OPERATOR_KEY.get(), 1))) {
            return new MolochOPContainer(id, playerInventory, this);
        } else {
            return new MolochContainer(id, playerInventory, this);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);

        if(!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.contents);
        }

        nbt.put("progression", this.progression.serializeNBT());

        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.read(nbt);
    }

    public void read(CompoundNBT nbt) {
        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.contents);
        }

        if(nbt.contains("progression")) {
            this.progression.deserializeNBT(nbt.getCompound("progression"));
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();

        this.write(tag);

        return new SUpdateTileEntityPacket(getPos(), -1, tag);
    }

    @Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        
        CompoundNBT tag = pkt.getNbtCompound();
        
		this.read(tag);
	}

	@Nonnull
	public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        
        this.write(tag);
        
		return tag;
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

        if (itemHandler != null) {
            itemHandler.invalidate();
        }
    }

    public void markDirtyNetwork() {
        this.markDirty();
        
        ModPacketHandler.HANDLER.sendToServer(new UpdateMoloch(this.pos, this.getCustomName()));
    }

    public Progression getProgression() {
        return progression;
    }

    private void tickClient() {
        World world = this.getWorld();

        if (this.world.rand.nextInt(100) < 10) {
            BlockPos blockPos = this.getPos();

            world.addParticle(ParticleTypes.LAVA, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0,
                    0);
        }
    }

    private void tickServer() {
        ServerWorld world = (ServerWorld) this.getWorld();
        Vector3d position = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        this.progression.consume(contents.get(0));

        if(this.progression.isComplete()) {
            this.progression.performReward(position, world);
            this.progression.next();
        }

        if(this.progression.isPunishing()) {
            if(world.rand.nextInt(1000) < 1) {
                this.progression.performPunishment(position, world);
            }
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        
        if(world.isRemote) {
            this.tickClient();
        } else {
            this.tickServer();
        }
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tileentity.moloch.moloch");
    }
}
