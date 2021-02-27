package ca.concordia.moloch.tileentity.moloch.action;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class Command extends Action {

	private String command;
	
	
	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	protected Command(long id, boolean doInitial, int doCountTotal, int doCountRemaining, int interval, int variance,
			long lastRun, boolean active, String command) {
		super(id, doInitial, doCountTotal, doCountRemaining, interval, variance, lastRun, active);
		this.command = command;
	}


	@Override
	public Actions getType() {
		return Actions.COMMAND;
	}


	@Override
	public void run(Vector3d position, String sourceName, ServerWorld world) {
		setLastRun(System.currentTimeMillis());
        CommandSource s = new CommandSource(
                ICommandSource.DUMMY, 
                position, 
                Vector2f.ZERO, 
                world, 
                2, //PermissionLevelIn
                sourceName, 
                new StringTextComponent(sourceName), 
                world.getServer(), 
                (Entity)null
            );

            world.getServer().getCommandManager().handleCommand(s, this.command);
	}
	 
}
