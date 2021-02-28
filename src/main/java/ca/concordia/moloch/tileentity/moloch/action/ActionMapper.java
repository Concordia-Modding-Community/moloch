package ca.concordia.moloch.tileentity.moloch.action;

import java.util.List;

import ca.concordia.moloch.utils.NBTMapper;
import net.minecraft.nbt.CompoundNBT;

public class ActionMapper extends NBTMapper<Action> {
    /**
     * By default - I set this to reward for the find method not to crash.
     * 
     * This said - definitely should force usage of specific find methods.
     */ 
    private String listTag = "rewards";

    @Override
    public String getListTag() {
        return this.listTag;
    }

    public List<Action> findRewards(CompoundNBT nbt) {
        this.listTag = "rewards";

		return find(nbt);
    }
    
    public void insertRewards(CompoundNBT nbt, List<Action> actions) {
        this.listTag = "rewards";

		insert(nbt, actions);
	}
	
	public List<Action> findPunishments(CompoundNBT nbt) {
        this.listTag = "punishments";

		return find(nbt);
    }
	
	public void insertPunishments(CompoundNBT nbt, List<Action> actions) {
        this.listTag = "punishments";

		insert(nbt, actions);
	}

    @Override
    public Action getInstance(CompoundNBT nbt) {
		switch(Actions.values()[nbt.getInt("type")]) {
			case COMMAND:
				return getInstance(new Command(), nbt);
			default:
				return null;
        }
    }
}