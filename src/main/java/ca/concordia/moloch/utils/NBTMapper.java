package ca.concordia.moloch.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class NBTMapper<T extends INBTSerializable<CompoundNBT>> {
    public NBTMapper() {}

    public abstract String getListTag();

    public List<T> find(CompoundNBT nbt) {
        List<T> instances = new ArrayList<T>();

        if(!nbt.contains(getListTag())) return instances;

		ListNBT list = nbt.getList(getListTag(), Constants.NBT.TAG_COMPOUND);

		for(INBT eNBT : list) {
			instances.add(getInstance((CompoundNBT) eNBT));
		}

		return instances;
    }

    public void insert(CompoundNBT nbt, List<T> instances) {
        ListNBT nbts = new ListNBT();

        for(T instance : instances) {
            nbts.add(getNBT(instance));
        }
        
        nbt.put(getListTag(), nbts);
    }

    public abstract T getInstance(CompoundNBT nbt);

    public T getInstance(T emptyInstance, CompoundNBT nbt) {
        emptyInstance.deserializeNBT(nbt);

        return emptyInstance;
    }

    public CompoundNBT getNBT(T instance) {
        return instance.serializeNBT();
    }
}