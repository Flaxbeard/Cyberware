package flaxbeard.cyberware.common.integration.roots;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class RootsIntegration
{
	public static ItemCyberware rootsUpgrade;

	public static void preInit()
	{
		Item component = CyberwareContent.component;
		
		rootsUpgrade = new ItemRootsUpgrade("rootsUpgrades",
				new EnumSlot[] { EnumSlot.HEART },
				new String[] { "extractor", "overclock" });
		rootsUpgrade.setEssenceCost(10, 10);
		rootsUpgrade.setWeights(CyberwareContent.RARE, CyberwareContent.RARE);
		rootsUpgrade.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 6), new ItemStack(component, 2, 8), new ItemStack(component, 3, 11) });
		
		//blasterUpgrade = new ItemBlasterUpgrade("blasterUpgrade", EnumSlot.HAND, new String[] { "fast", "strong" });
	}
}
