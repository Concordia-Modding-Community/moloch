package ca.concordia.moloch.tileentity.moloch.desire;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class DesireMapper extends NBTMapper<Desire> {
    @Override
    public String getListTag() {
        return "desires";
    }

    @Override
    public Desire getInstance(CompoundNBT nbt) {
        return getInstance(new Desire(), nbt);
    }
}
