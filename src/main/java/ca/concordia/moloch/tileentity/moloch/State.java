package ca.concordia.moloch.tileentity.moloch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import ca.concordia.moloch.tileentity.IMarkDirty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class State implements INBTSerializable<CompoundNBT>, IMarkDirty {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 

    private List<Reward> rewards;
    private List<Punishment> punishments;
    private List<Desire> desires;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Optional<Progression> progression;

    public State() {
        this.progression = Optional.empty();
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now().plusWeeks(1);

        this.rewards = new ArrayList<Reward>();
        this.punishments = new ArrayList<Punishment>();
        this.desires = new ArrayList<Desire>();
    }

    public void setParent(Progression progression) {
        this.progression = Optional.ofNullable(progression);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("startTime", getStartTimeString());
        nbt.putString("endTime", getEndTimeString());

        ListNBT rewardNBTs = new ListNBT();
        for (Action reward : this.rewards) {
            rewardNBTs.add(reward.serializeNBT());
        }
        nbt.put("rewards", rewardNBTs);

        ListNBT punishmentNBTs = new ListNBT();
        for (Action punishement : this.punishments) {
            punishmentNBTs.add(punishement.serializeNBT());
        }
        nbt.put("punishments", punishmentNBTs);

        ListNBT desireNBTs = new ListNBT();
        for (Desire desire : this.desires) {
            desireNBTs.add(desire.serializeNBT());
        }
        nbt.put("desires", desireNBTs);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("startTime")) {
            this.setStartTime(nbt.getString("startTime"));
        }

        if (nbt.contains("endTime")) {
            this.setEndTime(nbt.getString("endTime"));
        }

        if (nbt.contains("rewards")) {
            ListNBT rewardNBTs = nbt.getList("rewards", Constants.NBT.TAG_COMPOUND);

            rewardNBTs.stream().forEach(rewardNBT -> {
                Reward reward = new Reward();

                reward.deserializeNBT((CompoundNBT) rewardNBT);

                this.rewards.add(reward);
            });
        }

        if (nbt.contains("punishments")) {
            ListNBT punishmentNBTs = nbt.getList("punishments", Constants.NBT.TAG_COMPOUND);

            punishmentNBTs.stream().forEach(punishmentNBT -> {
                Punishment punishment = new Punishment();

                punishment.deserializeNBT((CompoundNBT) punishmentNBT);

                this.punishments.add(punishment);
            });
        }

        if(nbt.contains("desires")) {
            ListNBT desireNBTs = nbt.getList("desires", Constants.NBT.TAG_COMPOUND);

            desireNBTs.stream().forEach(desireNBT -> {
                Desire desire = new Desire();

                desire.deserializeNBT((CompoundNBT) desireNBT);

                this.desires.add(desire);
            });
        }
    }

    /**
     * This returns the time difference between the start and end time.
     * 
     * This currently only considers up to days (don't think we need more).
     */
    private long getTimeDifferenceInSeconds(LocalDateTime start, LocalDateTime end) {
        return start.until(end, ChronoUnit.DAYS) * 24 * 60 * 365 + start.until(end, ChronoUnit.HOURS) * 24 * 60
                + start.until(end, ChronoUnit.MINUTES) * 60 + start.until(end, ChronoUnit.SECONDS);
    }

    /**
     * Returns the time difference between the start and end time of the
     * progression.
     */
    public long getTotalTimeDifferenceInSeconds() {
        return getTimeDifferenceInSeconds(this.startTime, this.endTime);
    }

    /**
     * Gets the time left in seconds.
     */
    public long getTimeLeftInSeconds() {
        return getTimeDifferenceInSeconds(LocalDateTime.now(), this.endTime);
    }

    private static String getTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(TIME_FORMATTER);
    }

    public String getStartTimeString() {
        return getTimeString(this.startTime);
    }

    public String getEndTimeString() {
        return getTimeString(this.endTime);
    }

    private void setTime(String localString, Consumer<LocalDateTime> consumer) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(localString, TIME_FORMATTER);

            consumer.accept(localDateTime);
        } catch(Exception e) {}
    }

    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
    }

    public void setStartTime(String localString) {
        this.setTime(localString, this::setStartTime);
    }

    public void setEndTime(LocalDateTime localDateTime) {
        this.endTime = localDateTime;
    }

    public void setEndTime(String localString) {
        this.setTime(localString, this::setEndTime);
    }

    public boolean consume(ItemStack itemStack) {
        for (Desire desire : this.desires) {
            if (desire.consume(itemStack)) {
                return true;
            }
        }

        return false;
    }

    public boolean isComplete() {
        for (Desire desire : this.desires) {
            if (!desire.isComplete()) {
                return false;
            }
        }

        return true;
    }

    public boolean isPunishing() {
        return LocalDateTime.now().isAfter(this.endTime);
    }

    public State add(Punishment punishment) {
        this.punishments.add(punishment);

        return this;
    }

    public State add(Reward reward) {
        this.rewards.add(reward);

        return this;
    }

    public State add(Desire desire) {
        this.desires.add(desire);

        return this;
    }

    public List<Desire> getDesires() {
        return desires;
    }

    public List<Reward> getRewards() {
        return this.rewards;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public Optional<Punishment> getRandomPunishement() {
        return Action.getRandomFromList(this.punishments);
    }

    public Optional<Reward> getRandomReward() {
        return Action.getRandomFromList(this.rewards);
    }

    @Override
    public void markDirty() {
        if(!this.progression.isPresent()) {
            return;
        }

        this.progression.get().markDirty();
    }

    public State.StateInventory getInventory() {
        return new StateInventory(this);
    }

    public class StateInventory implements IInventory {
        private State state;

        public StateInventory(State state) {
            this.state = state;
        }

        @Override
        public void clear() {
        }

        @Override
        public int getSizeInventory() {
            return this.state.desires.size();
        }

        @Override
        public boolean isEmpty() {
            return getSizeInventory() == 0;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return this.state.desires.get(index).getItemStack();
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
        }

        @Override
        public void markDirty() {
            if(this.state == null) {
                return;
            }

            this.state.markDirty();
        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity player) {
            return true;
        }
    }
}
