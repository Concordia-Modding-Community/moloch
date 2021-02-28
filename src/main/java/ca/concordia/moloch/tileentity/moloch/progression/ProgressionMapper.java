package ca.concordia.moloch.tileentity.moloch.progression;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class ProgressionMapper extends NBTMapper<Progression> {
	private static class NBT {
		public static final String PROGRESSIONS = "progressions";
	}
    
    @Override
    public String getListTag() {
        return NBT.PROGRESSIONS;
    }

    @Override
    public Progression getInstance(CompoundNBT nbt) {
        return getInstance(new Progression(), nbt);
    }
}
