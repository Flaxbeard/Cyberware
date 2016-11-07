package flaxbeard.cyberware.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import flaxbeard.cyberware.client.gui.ContainerBlueprintArchive;
import flaxbeard.cyberware.client.gui.ContainerComponentBox;
import flaxbeard.cyberware.client.gui.ContainerEngineeringTable;
import flaxbeard.cyberware.client.gui.ContainerPlayerExpandedCrafting;
import flaxbeard.cyberware.client.gui.ContainerRFIDContainer;
import flaxbeard.cyberware.client.gui.ContainerScanner;
import flaxbeard.cyberware.client.gui.ContainerSurgery;
import flaxbeard.cyberware.client.gui.GuiBlueprintArchive;
import flaxbeard.cyberware.client.gui.GuiComponentBox;
import flaxbeard.cyberware.client.gui.GuiEngineeringTable;
import flaxbeard.cyberware.client.gui.GuiInventoryExpandedCrafting;
import flaxbeard.cyberware.client.gui.GuiRFIDContainer;
import flaxbeard.cyberware.client.gui.GuiScanner;
import flaxbeard.cyberware.client.gui.GuiSurgery;
import flaxbeard.cyberware.common.block.tile.TileEntityBlueprintArchive;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.block.tile.TileEntityRFIDContainer;
import flaxbeard.cyberware.common.block.tile.TileEntityScanner;
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
			case 1:
				return new ContainerPlayerExpandedCrafting(player.inventory, false, player);
			case 2:
				return new ContainerEngineeringTable(player.getCachedUniqueIdString(), player.inventory, (TileEntityEngineeringTable) world.getTileEntity(new BlockPos(x, y, z)));
			case 3:
				return new ContainerScanner(player.inventory, (TileEntityScanner) world.getTileEntity(new BlockPos(x, y, z)));
			case 4:
				return new ContainerBlueprintArchive(player.inventory, (TileEntityBlueprintArchive) world.getTileEntity(new BlockPos(x, y, z)));
			case 5:
				return new ContainerComponentBox(player.inventory, (TileEntityComponentBox) world.getTileEntity(new BlockPos(x, y, z)));
			case 6:
				return new ContainerComponentBox(player.inventory, player.inventory.mainInventory[player.inventory.currentItem]);
			default:
			case 7:
				return new ContainerRFIDContainer(player.inventory, (TileEntityRFIDContainer) world.getTileEntity(new BlockPos(x, y, z)));
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case 0:
				return new GuiSurgery(player.inventory, (TileEntitySurgery) world.getTileEntity(new BlockPos(x, y, z)));
			case 1:
				return new GuiInventoryExpandedCrafting(player);
			case 2:
				return new GuiEngineeringTable(player.inventory, (TileEntityEngineeringTable) world.getTileEntity(new BlockPos(x, y, z)));
			case 3:
				return new GuiScanner(player.inventory, (TileEntityScanner) world.getTileEntity(new BlockPos(x, y, z)));
			case 4:
				return new GuiBlueprintArchive(player.inventory, (TileEntityBlueprintArchive) world.getTileEntity(new BlockPos(x, y, z)));
			case 5:
				return new GuiComponentBox(player.inventory, (TileEntityComponentBox) world.getTileEntity(new BlockPos(x, y, z)));
			case 6:
				return new GuiComponentBox(player.inventory, player.inventory.mainInventory[player.inventory.currentItem]);
			default:
			case 7:
				return new GuiRFIDContainer(player.inventory, (TileEntityRFIDContainer) world.getTileEntity(new BlockPos(x, y, z)));

		}
	}

}
