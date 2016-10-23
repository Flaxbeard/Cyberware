package flaxbeard.cyberware.client.gui.tablet;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.client.gui.tablet.TabletMainMenu.MenuElement;
import flaxbeard.cyberware.common.CyberwareContent;


public class TabletContent
{
	public static TabletMainMenu mainMenu;
	public static TabletCatalogItem eyes;
	
	public static void init()
	{
		mainMenu = new TabletMainMenu();
		mainMenu.addItem(new MenuElement("cyberware.gui.tablet.test0", 0));
		mainMenu.addItem(new MenuElement("cyberware.gui.tablet.test1", 1));
		
		eyes = new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyes), "cyberware.gui.tablet.cybereyes").setDefaultVisible();
	}
}
