package ca.concordia.moloch.tileentity.moloch.action;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

/**
 * Actions must run at least once, but changing doInitial affects whether the first time is immediate, or if it is run after an amount of time interval (with variance) 
 **/
public abstract class Action {
	private long id;
	private boolean doInitial;
	private int doCountTotal;
	private int doCountRemaining;
	private int interval;
	private long variance;
	private long lastRun;
	private boolean active;

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

	abstract public Actions getType();
	abstract public void run(Vector3d position, String sourceName, ServerWorld world);

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
}
