package ca.concordia.moloch.tileentity.moloch.action;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Actions must run at least once, but changing doInitial affects whether the first time is immediate, 
 * or if it is run after an amount of time interval (with variance).
 **/
public abstract class Action implements INBTSerializable<CompoundNBT> {
	private long id;
	private boolean doInitial;
	private int doCountTotal;
	private int doCountRemaining;
	private int interval;
	private long variance;
	private long lastRun;
	private boolean active;

	protected Action() {
		this(
			0,
			true,
			1,
			1,
			10000,
			0,
			0,
			false
		);
	}

	protected Action(long id, boolean doInitial, int doCountTotal, int doCountRemaining, int interval, long variance,
			long lastRun, boolean active) {
		super();
		this.id = id;
		this.doInitial = doInitial;
		this.doCountTotal = Math.max(0,doCountTotal);
		this.doCountRemaining = Math.max(0, doCountRemaining);
		this.interval = interval;
		this.variance = variance;
		this.lastRun = lastRun;
		this.active = active;
	}

	public abstract Actions getType();
	public abstract void run(Vector3d position, String sourceName, ServerWorld world);
	public abstract ITextComponent getGUITitle();
	public abstract ITextComponent getGUIDescription();

	public boolean isDoInitial() {
		return doInitial;
	}

	public void setDoInitial(boolean doInitial) {
		this.doInitial = doInitial;
	}

	public int getDoCountTotal() {
		return doCountTotal;
	}

	public void setDoCountTotal(int doCountTotal) {
		this.doCountTotal = doCountTotal;
	}

	public int getDoCountRemaining() {
		return doCountRemaining;
	}

	public void setDoCountRemaining(int doCountRemaining) {
		this.doCountRemaining = doCountRemaining;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getVariance() {
		return variance;
	}

	public void setVariance(long variance) {
		this.variance = variance;
	}

	public long getLastRun() {
		return lastRun;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getId() {
		return id;
	}

	public void run(BlockPos blockPos, String sourceName, ServerWorld world) {
		run(new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), sourceName, world);
		doCountRemaining--;
		if(getDoCountRemaining()<1)setActive(false);
	}
	
	public boolean shouldRunNow() {
		return active && ((System.currentTimeMillis()-lastRun) > (interval+ (new Random().nextGaussian())*Math.sqrt(variance)));
	}

	public static class NBT {
		public static final String ID = "id";
		public static final String TYPE = "type";
		public static final String DO_INITIAL = "doInitial";
		public static final String DO_COUNT_TOTAL = "doCountTotal";
		public static final String DO_COUNT_REMAINING = "doCountRemaining";
		public static final String INTERVAL = "interval";
		public static final String VARIANCE = "variance";
		public static final String LAST_RUN = "lastRun";
		public static final String ACTIVE = "active";
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
        
		nbt.putLong(NBT.ID, this.getId());
		nbt.putInt(NBT.TYPE, this.getType().ordinal());
		nbt.putBoolean(NBT.DO_INITIAL, this.isDoInitial());
		nbt.putInt(NBT.DO_COUNT_TOTAL, this.getDoCountTotal());
		nbt.putInt(NBT.DO_COUNT_REMAINING, this.getDoCountRemaining());
		nbt.putInt(NBT.INTERVAL, this.getInterval());
		nbt.putLong(NBT.VARIANCE, this.getVariance());
		nbt.putLong(NBT.LAST_RUN, this.getLastRun());
		nbt.putBoolean(NBT.ACTIVE, this.isActive());
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if(nbt.contains(NBT.ID)) this.id = nbt.getLong(NBT.ID);
		if(nbt.contains(NBT.DO_INITIAL)) this.doInitial = nbt.getBoolean(NBT.DO_INITIAL);
		if(nbt.contains(NBT.DO_COUNT_TOTAL)) this.doCountTotal = nbt.getInt(NBT.DO_COUNT_TOTAL);
		this.doCountRemaining = nbt.contains(NBT.DO_COUNT_REMAINING) ? nbt.getInt(NBT.DO_COUNT_REMAINING) : this.doCountTotal;
		if(nbt.contains(NBT.INTERVAL)) this.interval = nbt.getInt(NBT.INTERVAL);
		if(nbt.contains(NBT.VARIANCE)) this.variance = nbt.getLong(NBT.VARIANCE);
		if(nbt.contains(NBT.LAST_RUN)) this.lastRun = nbt.getLong(NBT.LAST_RUN);
		if(nbt.contains(NBT.ACTIVE)) this.active = nbt.getBoolean(NBT.ACTIVE);
	}
}
