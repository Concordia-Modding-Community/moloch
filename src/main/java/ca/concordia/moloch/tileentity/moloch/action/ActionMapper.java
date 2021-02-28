package ca.concordia.moloch.tileentity.moloch.action;

import java.util.List;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class ActionMapper extends NBTMapper<Action> {
    private static class NBT {
        public static final String REWARDS = "rewards";
        public static final String PUNISHMENTS = "punishments";
    }

    /**
     * By default - I set this to reward for the find method not to crash.
     * 
     * This said - definitely should force usage of specific find methods.
     */ 
    private String listTag = NBT.REWARDS;

    @Override
    public String getListTag() {
        return this.listTag;
    }

    public List<Action> findRewards(CompoundNBT nbt) {
        this.listTag = NBT.REWARDS;

		return find(nbt);
    }
    
    public void insertRewards(CompoundNBT nbt, List<Action> actions) {
        this.listTag = NBT.REWARDS;

		insert(nbt, actions);
	}
	
	public List<Action> findPunishments(CompoundNBT nbt) {
        this.listTag = NBT.PUNISHMENTS;

		return find(nbt);
    }
	
	public void insertPunishments(CompoundNBT nbt, List<Action> actions) {
        this.listTag = NBT.PUNISHMENTS;

		insert(nbt, actions);
	}

    @Override
    public Action getInstance(CompoundNBT nbt) {
        int actionID = nbt.getInt(Action.NBT.TYPE);

        if(actionID >= Actions.values().length) return null;

		switch(Actions.values()[actionID]) {
			case COMMAND:
				return getInstance(new Command(), nbt);
			default:
				return null;
        }
    }
}