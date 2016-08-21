package flaxbeard.cyberware.common.integration.tan;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class ToughAsNailsIntegration
{
	public static ItemCyberware sweat;

	public static void preInit()
	{
		sweat = new ItemToughAsNailsUpgrade("toughAsNailsUpgrades",
				new EnumSlot[] { EnumSlot.SKIN, EnumSlot.SKIN },
				new String[] { "sweat", "blubber" });
		sweat.setEssenceCost(7, 14);
		sweat.setWeights(CyberwareContent.UNCOMMON, CyberwareContent.UNCOMMON);
		sweat.setComponents(
				new ItemStack[] { new ItemStack(CyberwareContent.component, 1, 8), new ItemStack(CyberwareContent.component, 2, 7), new ItemStack(CyberwareContent.component, 1, 1) },
				new ItemStack[] { new ItemStack(CyberwareContent.component, 2, 6), new ItemStack(CyberwareContent.component, 1, 7), new ItemStack(CyberwareContent.component, 3, 1) }
				);
	}
}
