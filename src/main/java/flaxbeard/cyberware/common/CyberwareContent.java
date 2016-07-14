package flaxbeard.cyberware.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.block.BlockSurgery;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.integration.botania.BotaniaIntegration;
import flaxbeard.cyberware.common.item.ItemBodyPart;
import flaxbeard.cyberware.common.item.ItemBrainUpgrade;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import flaxbeard.cyberware.common.item.ItemCybereyes;
import flaxbeard.cyberware.common.item.ItemCyberheart;
import flaxbeard.cyberware.common.item.ItemExpCapsule;
import flaxbeard.cyberware.common.item.ItemHeartUpgrade;
import flaxbeard.cyberware.common.item.ItemLungsUpgrades;
import flaxbeard.cyberware.common.item.VanillaWares.SpiderEyeWare;

public class CyberwareContent
{
	public static Block surgeryApparatus;
	public static Block surgeryChamber;
	public static Item bodyPart;
	public static Item cybereyes;
	public static Item cybereyeUpgrades;
	public static Item brainUpgrades;
	public static Item expCapsule;
	public static Item heartUpgrades;
	public static Item cyberheart;
	public static Item lungsUpgrades;

	public static List<Item> items;
	public static List<Block> blocks;

	public static void preInit()
	{
		items = new ArrayList<Item>();
		blocks = new ArrayList<Block>();
		surgeryApparatus = new BlockSurgery();
		surgeryChamber = new BlockSurgeryChamber();
		bodyPart = new ItemBodyPart("bodyPart", 
				new EnumSlot[] { EnumSlot.EYES, EnumSlot.CRANIUM, EnumSlot.HEART, EnumSlot.LUNGS },
				new String[] { "eyes", "brain", "heart", "lungs" });
		
		
		cybereyes = new ItemCybereyes("cybereyes", EnumSlot.EYES);
		cybereyeUpgrades = new ItemCybereyeUpgrade("cybereyeUpgrades", EnumSlot.EYES,
				new String[] { "nightVision", "underwaterVision", "hudjack", "targeting", "zoom" });
		CyberwareAPI.linkCyberware(Items.SPIDER_EYE, new SpiderEyeWare());
		
		brainUpgrades = new ItemBrainUpgrade("brainUpgrades", EnumSlot.CRANIUM,
				new String[] { "corticalStack", "enderJammer", "consciousnessTransmitter", "neuralContextualizer" });
		expCapsule = new ItemExpCapsule("expCapsule");
		
		cyberheart = new ItemCyberheart("cyberheart", EnumSlot.HEART);
		heartUpgrades = new ItemHeartUpgrade("heartUpgrades", EnumSlot.HEART,
				new String[] { "defibrillator", "platelets", "medkit" });
		
		lungsUpgrades = new ItemLungsUpgrades("lungsUpgrades", EnumSlot.LUNGS,
				new String[] { "oxygen", "hyperoxygenation" });
		
		if (Loader.isModLoaded("Botania"))
		{
			BotaniaIntegration.preInit();
		}
	}
}
