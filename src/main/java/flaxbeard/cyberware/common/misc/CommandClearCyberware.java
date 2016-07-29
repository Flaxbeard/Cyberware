package flaxbeard.cyberware.common.misc;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import flaxbeard.cyberware.api.CyberwareAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandClearCyberware extends CommandBase
{
	/**
	 * Gets the name of the command
	 */
	public String getCommandName()
	{
		return "clearcyberware";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender)
	{
		return "cyberware.commands.clearCyberware.usage";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	/**
	 * Callback for when the command is executed
	 */
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP entityplayermp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(server, sender, args[0]);
		NBTTagCompound nbttagcompound = null;

		CyberwareAPI.getCapability(entityplayermp).resetWare();
		CyberwareAPI.updateData(entityplayermp);
		
		notifyCommandListener(sender, this, "cyberware.commands.clearCyberware.success", new Object[] {entityplayermp.getName()});
	}

	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : Collections.<String>emptyList();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}
}