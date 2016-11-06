package flaxbeard.cyberware.common.integration.baubles;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import baubles.client.gui.GuiBaublesButton;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import flaxbeard.cyberware.client.gui.GuiInventoryExpandedCrafting;

public class BaublesIntegration
{

	public static void handleAction(GuiInventoryExpandedCrafting gui, GuiButton button)
	{
		if (button.id == 55)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory(gui.mc.thePlayer));
		}
	}

	public static void addButton(GuiInventoryExpandedCrafting gui)
	{
		gui.addButton(new GuiBaublesButton(55, gui.getLeft(), gui.getTop(), 64, 9, 10, 10,
				I18n.format("button.baubles", new Object[0])));
	}

}
