package ca.concordia.moloch.tileentity.moloch.desire;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class DesireOutputMapper {
	public static void insert(CompoundNBT nbt, List<Desire> desires) {
		ListNBT desireNBTs = new ListNBT();

		for(Desire desire : desires) {
			desireNBTs.add(getNBT(desire));
		}

		nbt.put("desires", desireNBTs);
	}

	private static INBT getNBT(Desire d) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putLong("id", d.getId());
		nbt.putString("item", d.getItemName());
		nbt.putInt("amountTotal", d.getAmountTotal());
		nbt.putInt("amountRemaining", d.getAmountRemaining());
		return nbt;
	}
}