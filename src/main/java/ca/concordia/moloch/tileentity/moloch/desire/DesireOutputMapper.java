package ca.concordia.moloch.tileentity.moloch.desire;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class DesireOutputMapper {

	public static void insert(CompoundNBT nbt, List<Desire> desires) {
		ListNBT des = new ListNBT();
		for(Desire d: desires) {
			des.add(getNBT(d));
		}
		nbt.put("desires", des);
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
