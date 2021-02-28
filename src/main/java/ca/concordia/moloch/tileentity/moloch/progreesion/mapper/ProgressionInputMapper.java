package ca.concordia.moloch.tileentity.moloch.progreesion.mapper;

import java.util.LinkedList;
import java.util.List;

import ca.concordia.moloch.tileentity.moloch.action.ActionInputMapper;
import ca.concordia.moloch.tileentity.moloch.desire.DesireInputMapper;
import ca.concordia.moloch.tileentity.moloch.progression.Progression;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class ProgressionInputMapper {

	public static List<Progression> find(CompoundNBT nbt) {
		List<Progression> progressions = new LinkedList<Progression>();
		ListNBT list = nbt.getList("progressions", Constants.NBT.TAG_COMPOUND);
		for(INBT a: list) {
			progressions.add(get((CompoundNBT) a));
		}
		return progressions;
	}

	private static Progression get(CompoundNBT nbt) {		
		long start = nbt.contains("start")?nbt.getLong("start"):System.currentTimeMillis();
		return new Progression(
			nbt.getLong("id"), 
			start, 
			nbt.contains("end")?nbt.getLong("end"):start+(1000*60*60*24*7), 
			nbt.getBoolean("active"),
			DesireInputMapper.find(nbt),
			ActionInputMapper.findRewards(nbt),
			ActionInputMapper.findPunishments(nbt)
		);
	}
	
}
