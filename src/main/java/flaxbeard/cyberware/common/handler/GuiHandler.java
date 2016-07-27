package flaxbeard.cyberware.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import flaxbeard.cyberware.client.gui.ContainerPlayerExpandedCrafting;
import flaxbeard.cyberware.client.gui.ContainerSurgery;
import flaxbeard.cyberware.client.gui.GuiInventoryExpandedCrafting;
import flaxbeard.cyberware.client.gui.GuiSurgery;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case 0:
				return new ContainerSurgery(player.inventory, (TileEntitySurgery) world.getTileEntity(new BlockPos(x, y, z)));
			default:
				return new ContainerPlayerExpandedCrafting(player.inventory, false, player);
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case 0:
				return new GuiSurgery(player.inventory, (TileEntitySurgery) world.getTileEntity(new BlockPos(x, y, z)));
			default:
				return new GuiInventoryExpandedCrafting(player);
		}
	}

}
