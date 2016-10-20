package flaxbeard.cyberware.common.integration.botania;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class BotaniaIntegration
{
	public static ItemCyberware botaniaUpgrade;

	public static void preInit()
	{
		/*manaLens = new ItemManaLens("manaseerLens", EnumSlot.EYES,
				new String[] { "lens", "link" });*/
		
		Item component = CyberwareContent.component;
		
		botaniaUpgrade = new ItemBotaniaUpgrade("botaniaUpgrades",
				new EnumSlot[] { EnumSlot.BONE, EnumSlot.HAND },
				new String[] { "manaBones", "blaster", "fluxfield" });
		botaniaUpgrade.setEssenceCost(10, 2, 8);
		botaniaUpgrade.setWeights(CyberwareContent.RARE, CyberwareContent.UNCOMMON, CyberwareContent.RARE);
		botaniaUpgrade.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 6), new ItemStack(component, 2, 8), new ItemStack(component, 3, 10) },
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 6), new ItemStack(component, 2, 8), new ItemStack(component, 3, 10) },
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 6), new ItemStack(component, 2, 8), new ItemStack(component, 3, 10) });
		
		//blasterUpgrade = new ItemBlasterUpgrade("blasterUpgrade", EnumSlot.HAND, new String[] { "fast", "strong" });
	}
}
