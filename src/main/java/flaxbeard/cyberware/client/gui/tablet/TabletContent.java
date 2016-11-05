package flaxbeard.cyberware.client.gui.tablet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import flaxbeard.cyberware.client.gui.tablet.TabletMainMenu.MenuElement;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.integration.tan.ToughAsNailsIntegration;


public class TabletContent
{
	public static TabletMainMenu mainMenu;
	public static TabletCatalog catalog;

	public static TabletCatalogItem eyes;
	public static TabletCatalogItem heart;

	public static void init()
	{
		mainMenu = new TabletMainMenu();
		mainMenu.addItem(new MenuElement("cyberware.gui.tablet.test0", 0));
		mainMenu.addItem(new MenuElement("cyberware.gui.tablet.test1", 1));
		
		catalog = new TabletCatalog();
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.eyeUpgrades), "cyberware.gui.tablet.cybereyes").setDefaultVisible();
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyes), "cyberware.gui.tablet.cybereyes").setDefaultVisible();
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 2), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 3), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 4), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 2), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 3), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 4), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.brainUpgrades, 1, 5), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.cyberheart), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.heartUpgrades, 1, 3), "").setDefaultVisible();
		new TabletCatalogItem(new ItemStack(CyberwareContent.heartUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.heartUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.heartUpgrades, 1, 2), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.lungsUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.lungsUpgrades, 1, 1), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.lowerOrgansUpgrades, 1, 2), "").setDefaultVisible();
		new TabletCatalogItem(new ItemStack(CyberwareContent.lowerOrgansUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.lowerOrgansUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.lowerOrgansUpgrades, 1, 3), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.denseBattery), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.skinUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.skinUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.skinUpgrades, 1, 2), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.skinUpgrades, 1, 3), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.muscleUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.muscleUpgrades, 1, 1), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.boneUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.boneUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.boneUpgrades, 1, 2), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.cyberlimbs, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cyberlimbs, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cyberlimbs, 1, 2), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.cyberlimbs, 1, 3), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.armUpgrades, 1, 0), "");
		
		new TabletCatalogItem(new ItemStack(CyberwareContent.handUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.handUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.handUpgrades, 1, 2), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.legUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.legUpgrades, 1, 1), "");

		new TabletCatalogItem(new ItemStack(CyberwareContent.footUpgrades, 1, 0), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.footUpgrades, 1, 1), "");
		new TabletCatalogItem(new ItemStack(CyberwareContent.footUpgrades, 1, 2), "");
		
		if (Loader.isModLoaded("ToughAsNails"))
		{
			new TabletCatalogItem(new ItemStack(ToughAsNailsIntegration.sweat, 1, 0), "");
			new TabletCatalogItem(new ItemStack(ToughAsNailsIntegration.sweat, 1, 1), "");
		}
	}
}
