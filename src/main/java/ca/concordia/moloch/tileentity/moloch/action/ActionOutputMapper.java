package ca.concordia.moloch.tileentity.moloch.action;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class ActionOutputMapper {

	public static void insertRewards(CompoundNBT nbt, List<Action> actions) {
		insert(nbt, "rewards", actions);
	}
	
	public static void insertPunishments(CompoundNBT nbt, List<Action> actions) {
		insert(nbt, "punishments", actions);
	}
	
	public static void insert(CompoundNBT nbt, String flavor, List<Action> actions) {
		ListNBT acts = new ListNBT();
		try {
			for(Action a: actions) {
				acts.add(getNBT(a));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		nbt.put(flavor, acts);
	}

	private static INBT getNBT(Action a) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putLong("id", a.getId());

		nbt.putBoolean("doInitial", a.isDoInitial());
		nbt.putInt("doCountTotal", a.getDoCountTotal());
		nbt.putInt("doCountRemaining", a.getDoCountRemaining());
		nbt.putInt("interval", a.getInterval());
		nbt.putInt("variance", a.getVariance());
		nbt.putLong("lastRun", a.getLastRun());
		nbt.putBoolean("active", a.isActive());
		
		switch(a.getType()) {
			case COMMAND:
				nbt.putInt("type", Actions.COMMAND.ordinal());
				nbt.putString("command", ((Command)a).getCommand());
		}
		
		return nbt;
	}


	
}
