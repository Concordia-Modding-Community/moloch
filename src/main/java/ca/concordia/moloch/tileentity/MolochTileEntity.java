package ca.concordia.moloch.tileentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.init.ModTileEntities;
import ca.concordia.moloch.tileentity.moloch.action.Action;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;
import ca.concordia.moloch.tileentity.moloch.progreesion.mapper.ProgressionInputMapper;
import ca.concordia.moloch.tileentity.moloch.progreesion.mapper.ProgressionOutputMapper;
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

    private List<Action> actionQueue = new ArrayList<Action>();
    private List<Progression> progressions = new ArrayList<Progression>();
    private Progression currentProgression = null;
    public Progression getCurrentProgression() {
		return currentProgression;
	}

	private String molochName = this.getDefaultName().getString();
    public String getMolochName() {
		return molochName;
	}

	public void setMolochName(String molochName) {
		this.molochName = molochName;
	}

	private boolean active = false;
    
    //We haven't done this yet...
    private List<PlayerUUID> team = new ArrayList<PlayerUUID>();
    long lastConsumption = System.currentTimeMillis();
    

    public MolochTileEntity() {
        this(ModTileEntities.MOLOCH.get());
    }

    public MolochTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        this.setCustomName(this.getDefaultName());
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

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        System.out.println("Writing NBT");

        if(!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.contents);
        }
        nbt.putString("molochName", this.molochName);
        nbt.putBoolean("active", this.active);
        
        ProgressionOutputMapper.insert(nbt, progressions);

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

        if(nbt.contains("molochName")) {
        	this.molochName = nbt.getString("molochName");
        }

        if(nbt.contains("active")) {
        	this.active = nbt.getBoolean("active");
        }

        if(nbt.contains("progressions")) {
        	this.progressions = ProgressionInputMapper.find(nbt);
        }
        
        this.actionQueue = buildActionQueue();
        
        this.currentProgression = findCurrentProgression();
        
        
    }

    private List<Action> buildActionQueue() {
    	System.out.println("Building Action Queue");
    	List<Action> newQueue = new ArrayList<Action>();
		for(Progression p: this.progressions) {
			if(!p.isActive() && System.currentTimeMillis() > p.getStart()) {
				for(Action a: p.getRewards()) {
					if(a.isActive()) newQueue.add(a);
				}
				for(Action a: p.getPunishments()) {
					if(a.isActive()) newQueue.add(a);
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

    public void markDirtyServer() {
    	//We don't need to send a special packet for this, the built in NBT synch crap (getUpdatePacket/onDataPacket) does thsi
        this.getWorld().notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
        this.markDirty();
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
        if(currentProgression != null) { //There's a current progression, which means there's a current demand
	        if(System.currentTimeMillis()-lastConsumption > CONSUME_TIME) {
	        	lastConsumption = System.currentTimeMillis();
	        	
	    		for(Desire d: currentProgression.getDesires()) {
	    			if(d.getAmountRemaining() > 0) {
	    				if(contents.get(0).getItem().equals(d.getItem())) {
	    					d.decrementAmountRemaining();
	    					contents.get(0).setCount(contents.get(0).getCount()-1);
	    					this.markDirtyServer();
	    					break;
	    				}
	    			}
	    		}
	    		boolean complete = true;
	    		for(Desire d: currentProgression.getDesires()) {
	    			complete = complete && (d.getAmountRemaining() == 0);
	    		}
	    		if(complete) {
	    			System.out.println("We have completed the current progression!");
	    			activateActions((ServerWorld) this.getWorld(), this.pos, molochName, actionQueue, currentProgression.getRewards());
	    			currentProgression.setActive(false);
	    			this.currentProgression = findCurrentProgression();
	    			this.markDirtyServer();
	    		}
	        }

        }
        
        if(currentProgression != null && currentProgression.getEnd() < System.currentTimeMillis()) {
			activateActions((ServerWorld) this.getWorld(), this.pos, molochName, actionQueue, currentProgression.getPunishments());
        	currentProgression.setActive(false);
        	this.currentProgression = findCurrentProgression();
        	this.markDirtyServer();
        }
        
        //TODO: Do we want to check this every tic? I think not.
    	ArrayList<Action> finishedQueue = new ArrayList<Action>();
    	for(Action a: actionQueue) {
    		if(a.shouldRunNow()) a.run(this.pos, molochName, (ServerWorld) this.getWorld());
    		if(!a.isActive()) finishedQueue.add(a);
    	}
    	actionQueue.removeAll(finishedQueue);
        
        
    }

	public static void activateActions(ServerWorld actionWorld, BlockPos actionSource, String actionName, List<Action> targetQueue,
			List<Action> actions) {
		for(Action a: actions) {
			a.setActive(true);
			if(a.isDoInitial()) a.run(actionSource, actionName, actionWorld);
			else a.setLastRun(System.currentTimeMillis());
			if(a.isActive()) targetQueue.add(a);
		}
	}

    private Progression findCurrentProgression() {
    	System.out.println("Finding current Progression:");
		for(Progression p: this.progressions) {
			if(p.isActive()) {
				System.out.println("\tfound " + p);
				return p;
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

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tileentity.moloch.moloch");
    }
}
