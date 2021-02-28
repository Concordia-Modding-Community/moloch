package ca.concordia.moloch.tileentity.moloch.progreesion.mapper;

import java.util.List;

import ca.concordia.moloch.tileentity.moloch.action.ActionOutputMapper;
import ca.concordia.moloch.tileentity.moloch.desire.DesireOutputMapper;
import ca.concordia.moloch.tileentity.moloch.progression.Progression;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class ProgressionOutputMapper {

	public static void insert(CompoundNBT nbt, List<Progression> progressions) {
		ListNBT progs = new ListNBT();
		for(Progression p: progressions) {
			progs.add(getNBT(p));
		}
		nbt.put("progressions", progs);
	}

	private static INBT getNBT(Progression p) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putLong("id", p.getId());
		nbt.putLong("start", p.getStart());
		nbt.putLong("end", p.getEnd());
		nbt.putBoolean("active", p.isActive());
		DesireOutputMapper.insert(nbt, p.getDesires());
		ActionOutputMapper.insertRewards(nbt, p.getRewards());
		ActionOutputMapper.insertPunishments(nbt, p.getPunishments());
		return nbt;
	}

}
