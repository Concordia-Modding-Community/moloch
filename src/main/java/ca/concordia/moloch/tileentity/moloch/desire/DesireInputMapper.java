package ca.concordia.moloch.tileentity.moloch.desire;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class DesireInputMapper {
	public static List<Desire> find(CompoundNBT nbt) {
		List<Desire> desires = new ArrayList<Desire>();
		ListNBT list = nbt.getList("desires", Constants.NBT.TAG_COMPOUND);

		for(INBT eNBT: list) {
			desires.add(get((CompoundNBT) eNBT));
		}

		return desires;
	}

	private static Desire get(CompoundNBT nbt) {
		int amountTotal = nbt.contains("amountTotal")?nbt.getInt("amountTotal"):1;
		
		return new Desire(
			nbt.getLong("id"), 
			nbt.getString("item"), 
			amountTotal, 
			nbt.contains("amountRemaining")?nbt.getInt("amountRemaining"):amountTotal
		);
	}
}