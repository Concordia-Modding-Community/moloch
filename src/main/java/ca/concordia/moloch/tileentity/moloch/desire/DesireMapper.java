package ca.concordia.moloch.tileentity.moloch.desire;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class DesireMapper extends NBTMapper<Desire> {
    private static class NBT {
        public static final String DESIRES = "desires";
    }

    @Override
    public String getListTag() {
        return NBT.DESIRES;
    }

    @Override
    public Desire getInstance(CompoundNBT nbt) {
        return getInstance(new Desire(), nbt);
    }
}
