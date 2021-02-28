package ca.concordia.moloch.tileentity.moloch.action;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class Command extends Action {
	private String command;

	public Command() {
		super();

		this.command = "/say Nobody told me what to do...";
	}

	public Command(long id, boolean doInitial, int doCountTotal, int doCountRemaining, int interval, long variance,
			long lastRun, boolean active, String command) {
		super(id, doInitial, doCountTotal, doCountRemaining, interval, variance, lastRun, active);

		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public Actions getType() {
		return Actions.COMMAND;
	}

	@Override
	public void run(Vector3d position, String sourceName, ServerWorld world) {
		setLastRun(System.currentTimeMillis());

		CommandSource commandSource = new CommandSource(ICommandSource.DUMMY, position, Vector2f.ZERO, world,
				// PermissionLevelIn
				2, sourceName, new StringTextComponent(sourceName), world.getServer(), (Entity) null);

		world.getServer().getCommandManager().handleCommand(commandSource, this.command);
	}

	private static class NBT {
		public static final String COMMAND = "command";
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);

		if (nbt.contains(NBT.COMMAND))
			this.command = nbt.getString(NBT.COMMAND);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();

		nbt.putString(NBT.COMMAND, this.getCommand());

		return nbt;
	}

	@Override
	public ITextComponent getGUITitle() {
		return new StringTextComponent("Command").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GOLD)));
	}

	@Override
	public ITextComponent getGUIDescription() {
		return new StringTextComponent(this.command).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GRAY)));
	}
}