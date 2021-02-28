package ca.concordia.moloch.tileentity.moloch.progression;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.moloch.tileentity.moloch.action.Action;
import ca.concordia.moloch.tileentity.moloch.action.ActionMapper;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;
import ca.concordia.moloch.tileentity.moloch.desire.DesireMapper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Progression implements INBTSerializable<CompoundNBT> {
	private long id;
	private long start;
	private long end;
	private boolean active;

	private List<Desire> desires;
	private List<Action> rewards;
	private List<Action> punishments;

	public Progression() {
		this(
			0, 
			System.currentTimeMillis(), 
			System.currentTimeMillis()+(1000*60*60*24*7), 
			false,
			new ArrayList<Desire>(),
			new ArrayList<Action>(),
			new ArrayList<Action>()
		);
	}

	public Progression(long id, long start, long end, boolean active, List<Desire> desires, List<Action> rewards,
			List<Action> punishments) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.active = active;
		this.desires = desires;
		this.rewards = rewards;
		this.punishments = punishments;
	}
	
	public long getStart() {
		return start;
	}
	
	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Desire> getDesires() {
		return desires;
	}

	public void setDesires(List<Desire> desires) {
		this.desires = desires;
	}

	public List<Action> getRewards() {
		return rewards;
	}

	public void setRewards(List<Action> rewards) {
		this.rewards = rewards;
	}

	public List<Action> getPunishments() {
		return punishments;
	}

	public void setPunishments(List<Action> punishments) {
		this.punishments = punishments;
	}

	public long getId() {
		return id;
	}

	private static class NBT {
		public static final String ID = "id";
		public static final String START = "start";
		public static final String END = "end";
		public static final String ACTIVE = "active";
	}

	@Override
	public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        
		nbt.putLong(NBT.ID, this.getId());
		nbt.putLong(NBT.START,this.getStart());
		nbt.putLong(NBT.END, this.getEnd());
        nbt.putBoolean(NBT.ACTIVE, this.isActive());
        
		new DesireMapper().insert(nbt, this.getDesires());
		new ActionMapper().insertRewards(nbt, this.getRewards());
        new ActionMapper().insertPunishments(nbt, this.getPunishments());
        
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if(nbt.contains(NBT.ID)) this.id = nbt.getLong(NBT.ID);
		if(nbt.contains(NBT.START)) this.start = nbt.getLong(NBT.START);
		if(nbt.contains(NBT.END)) this.end = nbt.getLong(NBT.END);
		if(nbt.contains(NBT.ACTIVE)) this.active = nbt.getBoolean(NBT.ACTIVE);

		this.desires = new DesireMapper().find(nbt);
		this.rewards = new ActionMapper().findRewards(nbt);
		this.punishments = new ActionMapper().findPunishments(nbt);
	}
}