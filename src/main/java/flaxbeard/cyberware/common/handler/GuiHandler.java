package flaxbeard.cyberware.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import flaxbeard.cyberware.client.gui.ContainerSurgery;
import flaxbeard.cyberware.client.gui.GuiSurgery;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.sprockets.gui.GuiBook;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new ContainerSurgery(player.inventory, (TileEntitySurgery) world.getTileEntity(new BlockPos(x, y, z)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new GuiSurgery(player.inventory, (TileEntitySurgery) world.getTileEntity(new BlockPos(x, y, z)));
	}

}
