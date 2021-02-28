package ca.concordia.moloch.tileentity.moloch.progression;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class ProgressionMapper extends NBTMapper<Progression> {
    @Override
    public String getListTag() {
        return "progressions";
    }

    @Override
    public Progression getInstance(CompoundNBT nbt) {
        return getInstance(new Progression(), nbt);
    }
}
