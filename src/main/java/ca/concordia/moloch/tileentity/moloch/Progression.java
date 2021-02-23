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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class Progression implements INBTSerializable<CompoundNBT>, IMarkDirty {
    private List<State> states;
    private int index;
    private IMarkDirty parent;

    public Progression(IMarkDirty parent) {
        this.parent = parent;
        this.states = new ArrayList<State>();
        this.index = 0;
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

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("list")) {
            ListNBT stateNBTs = nbt.getList("list", 0);

            stateNBTs.stream().forEach(progressionNBT -> {
                State state = new State();

                state.deserializeNBT((CompoundNBT) progressionNBT);

                this.add(state);
            });
        }

        if(nbt.contains("index")) {
            this.index = nbt.getInt("index");
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

    private <T extends Action> void perform(Vector3d position, ServerWorld world, Function<State, Optional<T>> callback) {
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

    public void performReward(Vector3d position, ServerWorld world) {
        this.perform(position, world, state -> state.getRandomReward());
    }

    public void performPunishment(Vector3d position, ServerWorld world) {
        this.perform(position, world, state -> state.getRandomPunishement());
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
        this.index++;
    }

    /**
     * Sets the previous progression state.
     */
    public void previous() {
        this.index = Math.max(this.index, 0);
    }

    private Optional<State> getCurrentState() {
        if(this.index < 0 || this.index >= this.states.size()) {
            return Optional.empty();
        }

        return Optional.of(this.states.get(this.index));
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
        this.parent.markDirty();
    }
}
