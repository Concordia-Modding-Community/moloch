package ca.concordia.moloch.tileentity.moloch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ca.concordia.moloch.tileentity.IMarkDirty;
import ca.concordia.moloch.tileentity.moloch.State.StateInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Progression implements INBTSerializable<CompoundNBT>, IMarkDirty {
    private List<State> states;
    private int index;
    private boolean isActive;
    private Optional<IMarkDirty> parent;

    public Progression() {
        this.parent = Optional.empty();
        this.states = new ArrayList<State>();
        this.index = 0;
        this.isActive = false;
    }

    public void setParent(IMarkDirty parent) {
        this.parent = Optional.ofNullable(parent);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT stateNBTs = new ListNBT();
        for(State state : this.states) {
            stateNBTs.add(state.serializeNBT());
        }

        nbt.put("list", stateNBTs);
        nbt.putInt("index", this.index);
        nbt.putBoolean("active", this.isActive);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("list")) {
            ListNBT stateNBTs = nbt.getList("list", Constants.NBT.TAG_COMPOUND);

            stateNBTs.stream().forEach(progressionNBT -> {
                State state = new State();

                state.deserializeNBT((CompoundNBT) progressionNBT);

                this.add(state);
            });
        }

        if(nbt.contains("index")) {
            this.index = nbt.getInt("index");
        }

        if(nbt.contains("active")) {
            this.isActive = nbt.getBoolean("active");
        }
    }

    public boolean consume(ItemStack itemStack) {
        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return false;
        }

        return state.get().consume(itemStack);
    }

    public boolean isComplete() {
        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return false;
        }

        return state.get().isComplete();
    }

    private <T extends Action> void perform(BlockPos blockPos, ServerWorld world, Function<State, Optional<T>> callback) {
        Vector3d position = new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return;
        }

        Optional<T> action = callback.apply(state.get());

        if(!action.isPresent()) {
            return;
        }

        action.get().perform(position, world);
    }

    public void performReward(BlockPos blockPos, ServerWorld world) {
        this.perform(blockPos, world, state -> state.getRandomReward());
    }

    public void performPunishment(BlockPos blockPos, ServerWorld world) {
        this.perform(blockPos, world, state -> state.getRandomPunishement());
    }

    public boolean isPunishing() {
        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return false;
        }

        return state.get().isPunishing();
    }

    public float getCompletionFraction() {
        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return 0;
        }

        return state.get().getTimeLeftInSeconds() / state.get().getTotalTimeDifferenceInSeconds();
    }

    /**
     * Insert a new state into the progression.
     */
    public Progression add(State state) {
        this.states.add(state);

        state.setParent(this);

        return this;
    }

    /**
     * Sets the next progression state.
     */
    public void next() {
        this.index = Math.min(this.index + 1, this.getNumberOfStates());
    }

    /**
     * Sets the previous progression state.
     */
    public void previous() {
        this.index = Math.max(this.index - 1, 0);
    }

    public int getIndex() {
        return this.index;
    }

    public int getNumberOfStates() {
        return this.states.size();
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getActive() {
        return this.isActive;
    }

    public Optional<State> getCurrentState() {
        if(this.index < 0 || this.index >= this.states.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.states.get(this.index));
    }

    public Optional<StateInventory> getInventory() {
        Optional<State> state = this.getCurrentState();

        if(!state.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(state.get().getInventory());
    }

    @Override
    public void markDirty() {
        if(!this.parent.isPresent()) {
            return;
        }

        this.parent.get().markDirty();
    }
}
