package flaxbeard.cyberware.common.integration.tan;

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
		/*manaLens = new ItemManaLens("manaseerLens", EnumSlot.EYES,
				new String[] { "lens", "link" });*/
	}
}
