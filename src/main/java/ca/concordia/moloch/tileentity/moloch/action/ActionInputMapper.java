package ca.concordia.moloch.tileentity.moloch.action;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class ActionInputMapper {

	public static List<Action> findRewards(CompoundNBT nbt) {
		return find(nbt, "rewards");
	}
	
	public static List<Action> findPunishments(CompoundNBT nbt) {
		return find(nbt, "punishments");
	}
	
	public static List<Action> find(CompoundNBT nbt, String flavor) {
		List<Action> acts = new LinkedList<Action>();
		ListNBT list = nbt.getList(flavor, Constants.NBT.TAG_COMPOUND);
		for(INBT a: list) {
			acts.add(get((CompoundNBT) a));
		}
		return acts;
	}

	private static Action get(CompoundNBT nbt) {		
		switch(Actions.values()[nbt.getInt("type")]) {
			case COMMAND:
				return new Command(
					nbt.getLong("id"), 
					nbt.getBoolean("doInitial"), 
					nbt.getInt("doCountTotal"), 
					nbt.getInt("doCountRemaining"), 
					nbt.getInt("interval"), 
					nbt.getInt("variance"), 
					nbt.getLong("lastRun"), 
					nbt.getBoolean("active"), 
					nbt.getString("command")
				);
		}
		
		return null;
	}


	
}
