package ca.concordia.moloch.tileentity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;

import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.init.ModTileEntities;
import ca.concordia.moloch.tileentity.moloch.action.Action;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;
import ca.concordia.moloch.tileentity.moloch.progression.ProgressionMapper;
import ca.concordia.moloch.tileentity.moloch.progression.Progression;
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
import net.minecraft.util.datafix.fixes.PlayerUUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MolochTileEntity extends LockableLootTileEntity implements ITickableTileEntity {
    private static final int INVENTORY_SIZE = 1;
    private static final int CONSUME_TIME = 100;

    private NonNullList<ItemStack> contents = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    private Queue<Action> actionQueue = new LinkedList<Action>();
    private List<Progression> progressions = new ArrayList<Progression>();
    private Progression currentProgression = null;

    //We haven't done this yet...
    private List<PlayerUUID> team = new ArrayList<PlayerUUID>();
    private String molochName = this.getDefaultName().getString();
    long lastConsumption = System.currentTimeMillis();
    
    public MolochTileEntity() {
        this(ModTileEntities.MOLOCH.get());
    }

    public MolochTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public String getMolochName() {
		return molochName;
	}

	public void setMolochName(String molochName) {
		this.molochName = molochName;
	}

    public Progression getCurrentProgression() {
		return currentProgression;
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
    	return new MolochContainer(id, playerInventory, this);
    }

    private static class NBT {
		public static final String MOLOCH_NAME = "molochName";
	}

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        System.out.println("Writing NBT");

        if(!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.contents);
        }

        nbt.putString(NBT.MOLOCH_NAME, this.molochName);
        
        new ProgressionMapper().insert(nbt, progressions);

        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.read(nbt);
    }
    
    public void read(CompoundNBT nbt) {
    	System.out.println("Reading NBT");
        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.contents);
        }

        if(nbt.contains(NBT.MOLOCH_NAME)) {
        	this.molochName = nbt.getString(NBT.MOLOCH_NAME);
        }

        this.progressions = new ProgressionMapper().find(nbt);
        
        this.actionQueue = buildActionQueue();

        this.currentProgression = findCurrentProgression();
    }

    private Queue<Action> buildActionQueue() {
        System.out.println("Building Action Queue");
        
        Queue<Action> newQueue = new LinkedList<Action>();
        
		for(Progression progression : this.progressions) {
			if(!progression.isActive() && System.currentTimeMillis() > progression.getStart()) {
				for(Action action : progression.getRewards()) {
					if(action.isActive()) newQueue.add(action);
                }
                
				for(Action action : progression.getPunishments()) {
					if(action.isActive()) newQueue.add(action);
				}
			}
        }
        
		return newQueue;
	}

	@Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();

        this.write(tag);

        return new SUpdateTileEntityPacket(getPos(), -1, tag);
    }

    @Override
	public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket packet) {
        super.onDataPacket(networkManager, packet);
        
        CompoundNBT tag = packet.getNbtCompound();
        
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

    public void markDirtyServer() {
    	//We don't need to send a special packet for this, the built in NBT synch crap (getUpdatePacket/onDataPacket) does this
        this.getWorld().notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
        this.markDirty();
    }

    /**
     * Checks if all desires have no remaining amount of items.
     */
    private boolean desiresAreComplete() {
        if(currentProgression == null) return false;

        for(Desire desire : currentProgression.getDesires()) {
            if(desire.getAmountRemaining() != 0) return false;
        }

        return true;
    }

    /**
     * Checks if the current progression (if any) has ran out of time.
     */
    private boolean ranOutOfTime() {
        if(currentProgression == null) return false;

        return currentProgression.getEnd() < System.currentTimeMillis();
    }

    /**
     * Attempts to consume the current item in Moloch's inventory at CONSUME_TIME rate.
     */
    private void tickConsumption() {
        if(currentProgression == null) return;
        if(System.currentTimeMillis() - lastConsumption < CONSUME_TIME) return;

        lastConsumption = System.currentTimeMillis();

        for(Desire desire : currentProgression.getDesires()) {
            if(desire.getAmountRemaining() == 0) continue;

            if(!contents.get(0).getItem().equals(desire.getItem())) continue;

            desire.decrementAmountRemaining();

            contents.get(0).setCount(contents.get(0).getCount()-1);

            this.markDirtyServer();

            return;
        }
    }

    /**
     * Attempts to run actions in queue.
     *
     * Just polling the whole queue and reinserting if they action is still active.
     * Method used to be O(n^2) because of removeAll but now is O(n).
     */
    private void tickActions() {
        for(int i = 0; i < actionQueue.size(); i++) {
            Action action = actionQueue.poll();

            if(action.shouldRunNow()) action.run(this.pos, molochName, (ServerWorld) this.getWorld());
            if(action.isActive()) actionQueue.add(action);
        }
    }

	public static void activateActions(ServerWorld actionWorld, BlockPos actionSource, String actionName, Queue<Action> targetQueue,
			List<Action> actions) {

		for(Action action: actions) {
            action.setActive(true);
            
			if(action.isDoInitial()) action.run(actionSource, actionName, actionWorld);
            else action.setLastRun(System.currentTimeMillis());
            
			if(action.isActive()) targetQueue.add(action);
		}
    }
    
    private void activateActions(List<Action> actionList) {
        activateActions((ServerWorld) this.getWorld(), this.pos, molochName, actionQueue, actionList);
        currentProgression.setActive(false);
        this.currentProgression = findCurrentProgression();
        this.markDirtyServer();
    }

    private void activateRewards() {
        activateActions(currentProgression.getRewards());
    }

    private void activatePunishments() {
        activateActions(currentProgression.getPunishments());
    }

    private Progression findCurrentProgression() {
        System.out.println("Finding current Progression:");
        
		for(Progression progression : this.progressions) {
			if(progression.isActive()) {
				System.out.println("\tfound " + progression);
				return progression;
			}
        }
        
		return null;
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

    private void tickClient() {
        if(currentProgression == null) return;
        if(!currentProgression.isActive()) return;

        World world = this.getWorld();

        if (this.world.rand.nextInt(100) < 10) {
            BlockPos blockPos = this.getPos();
            world.addParticle(ParticleTypes.LAVA, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0,
                    0);
        }
    }

    private void tickServer() {
        tickConsumption();

        if(desiresAreComplete()) activateRewards();

        if(ranOutOfTime()) activatePunishments();
        
        //TODO: Do we want to check this every tic? I think not.
        tickActions();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tileentity.moloch.moloch");
    }
}
