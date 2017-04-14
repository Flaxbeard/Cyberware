package flaxbeard.cyberware.common.integration.am2;

import am2.defs.ItemDefs;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class ArsMagicaIntegration
{
	public static void preInit()
	{
		CyberwareAPI.linkCyberware(ItemDefs.woodenLeg, new PegLeg());

	}
}
