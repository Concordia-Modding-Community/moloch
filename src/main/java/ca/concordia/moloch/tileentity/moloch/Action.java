package ca.concordia.moloch.tileentity.moloch;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Action implements INBTSerializable<CompoundNBT> {
    private String actionName;
    private String command;

    public Action() {
        this(
            "Moloch Action",
            ""
        );
    }

    public Action(String actionName, String command) {
        this.actionName = actionName;
        this.command = command;
    }

    public static <T extends Action> Optional<T> getRandomFromList(List<T> actions) {
        if(actions.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(actions.get(new Random().nextInt(actions.size())));
    }

    public void perform(Vector3d position, ServerWorld world) {
        CommandSource source = new CommandSource(
            ICommandSource.DUMMY, 
            position, 
            Vector2f.ZERO, 
            world, 
            2, 
            this.actionName, 
            new StringTextComponent(this.actionName), 
            world.getServer(), 
            (Entity)null
        );

        world.getServer().getCommandManager().handleCommand(source, this.command);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("command", this.command);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("command")) {
            this.command = nbt.getString("command");
        }
    }
}
