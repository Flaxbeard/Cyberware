package flaxbeard.cyberware.common;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.lib.LibConstants;

public class CyberwareConfig
{
	public static float ENGINEERING_CHANCE = 15F;
	public static float SCANNER_CHANCE = 10F;
	public static float SCANNER_CHANCE_ADDL = 10F;
	public static int SCANNER_TIME = 24000;
	
	public static int ESSENCE = 100;
	public static int CRITICAL_ESSENCE = 25;
	
	public static float DROP_RARITY = 50F;
	public static int ZOMBIE_WEIGHT = 15;
	public static int ZOMBIE_MIN_PACK = 1;
	public static int ZOMBIE_MAX_PACK = 1;
	public static boolean NO_ZOMBIES = false;
	
	public static int HUDR = 76;
	public static int HUDG = 255;
	public static int HUDB = 0;
	
	public static boolean SURGERY_CRAFTING = false;
	
	private static String[][] defaultStartingItems;
	private static String[][] startingItems;
	private static ItemStack[][] startingStacks;
	
	public static boolean DEFAULT_DROP = false;
	public static boolean DEFAULT_KEEP = false;

	public static boolean KATANA = true;
	public static boolean CLOTHES = true;
	
	public static int TESLA_PER_POWER = 1;
	
	public static Configuration config;
	
	public static File configDirectory;

	public static void loadConfig(FMLPreInitializationEvent event)
	{
		startingItems = defaultStartingItems = new String[EnumSlot.values().length][0];
		startingStacks = new ItemStack[EnumSlot.values().length][LibConstants.WARE_PER_SLOT];
		
		int j = 0;
		for (int i = 0; i < EnumSlot.values().length; i++)
		{
			if (EnumSlot.values()[i].hasEssential())
			{
				if (EnumSlot.values()[i].isSided())
				{
					defaultStartingItems[i] = new String[] { "cyberware:bodyPart 1 " + j, "cyberware:bodyPart 1 " + (j + 1)  };
					j += 2;
				}
				else
				{
					defaultStartingItems[i] = new String[] { "cyberware:bodyPart 1 " + j };
					j++;
				}
			}
			else
			{
				defaultStartingItems[i] = new String[0];
			}
		}
		
		configDirectory = event.getModConfigurationDirectory();
		config = new Configuration(new File(event.getModConfigurationDirectory(), Cyberware.MODID + ".cfg"));
		config.load();
		
		for (int index = 0; index < EnumSlot.values().length; index++)
		{
			EnumSlot slot = EnumSlot.values()[index];
			startingItems[index] = config.getStringList("Default augments for " + slot.getName() + " slot",
					"Defaults", defaultStartingItems[index], "Use format 'id amount metadata'");
		}
		
		NO_ZOMBIES = config.getBoolean("Disable cyberzombies", "Mobs", NO_ZOMBIES, "");
		ZOMBIE_WEIGHT = config.getInt("Spawning weight of Cyberzombies", "Mobs", ZOMBIE_WEIGHT, 0, Integer.MAX_VALUE, "Vanilla Zombie = 100, Enderman = 10, Witch = 5");
		ZOMBIE_MIN_PACK = config.getInt("Minimum Cyberzombie pack size", "Mobs", ZOMBIE_MIN_PACK, 0, Integer.MAX_VALUE, "Vanilla Zombie = 4, Enderman = 1, Witch = 1");
		ZOMBIE_MAX_PACK = config.getInt("Maximum Cyberzombie pack size", "Mobs", ZOMBIE_MAX_PACK, 0, Integer.MAX_VALUE, "Vanilla Zombie = 4, Enderman = 4, Witch = 1");

		DROP_RARITY = config.getFloat("Percent chance a Cyberzombie drops an item", "Mobs", DROP_RARITY, 0F, 100F, "");
		
		SURGERY_CRAFTING = config.getBoolean("Enable crafting recipe for Robosurgeon", "Other", SURGERY_CRAFTING, "Normally only found in Nether fortresses");
		TESLA_PER_POWER = config.getInt("RF/Tesla per internal power unit", "Other", TESLA_PER_POWER, 0, Integer.MAX_VALUE, "");
		
		ESSENCE = config.getInt("Maximum Essence", "Essence", ESSENCE, 0, Integer.MAX_VALUE, "");
		CRITICAL_ESSENCE = config.getInt("Critical Essence value, where rejection begins", "Essence", CRITICAL_ESSENCE, 0, Integer.MAX_VALUE, "");
		
		DEFAULT_DROP = config.getBoolean("Default for gamerule cyberware_dropCyberware", "Gamerules", DEFAULT_DROP, "Determines if players drop their Cyberware on death. Does not change settings on existing worlds, use /gamerule for that. Overridden if cyberware_keepCyberware is true");
		DEFAULT_KEEP = config.getBoolean("Default for gamerule cyberware_keepCyberware", "Gamerules", DEFAULT_KEEP, "Determines if players keep their Cyberware between lives. Does not change settings on existing worlds, use /gamerule for that.");

		ENGINEERING_CHANCE = config.getFloat("Chance of blueprint from Engineering Table", "Machines", ENGINEERING_CHANCE, 0, 100F, "");
		SCANNER_CHANCE = config.getFloat("Chance of blueprint from Scanner", "Machines", SCANNER_CHANCE, 0, 100F, "");
		SCANNER_CHANCE_ADDL = config.getFloat("Additive chance for Scanner per extra item", "Machines", SCANNER_CHANCE_ADDL, 0, 100F, "");
		SCANNER_TIME = config.getInt("Ticks taken per Scanner operation", "Machines", SCANNER_TIME, 0, Integer.MAX_VALUE, "24000 is one Minecraft day, 1200 is one real-life minute");
		
		KATANA = config.getBoolean("Enable Katana", "Other", KATANA, "");
		CLOTHES = config.getBoolean("Enable Trenchcoat, Mirrorshades, and Biker Jacket", "Other", CLOTHES, "");

		HUDR = config.getInt(REDKEY, "HUD Color", HUDR, 0, 255, "");
		HUDG = config.getInt(GREENKEY, "HUD Color", HUDG, 0, 255, "");
		HUDB = config.getInt(BLUEKEY, "HUD Color", HUDB, 0, 255, "");
		CyberwareAPI.setHUDColor(HUDR / 255F, HUDG / 255F, HUDB / 255F);
		config.save();
	}
	
	private static final String REDKEY = "Hudjack Color Red";
	private static final String GREENKEY = "Hudjack Color Green";
	private static final String BLUEKEY = "Hudjack Color Blue";

	public static void updateColors()
	{
		config.load();
		ConfigCategory category = config.getCategory("HUD Color");
		Property red = category.get(REDKEY);
		Property green = category.get(GREENKEY);
		Property blue = category.get(BLUEKEY);

		float[] color = CyberwareAPI.getHUDColor();
		red.set((int) (color[0] * 255F));
		green.set((int) (color[1] * 255F));
		blue.set((int) (color[2] * 255F));

		config.save();
	}
	
	public static void postInit()
	{
		int index = 0;
		for (String[] itemSet : startingItems)
		{
			EnumSlot slot = EnumSlot.values()[index];
			if (itemSet.length > LibConstants.WARE_PER_SLOT)
			{
				throw new RuntimeException("Cyberware configuration error! Too many items for slot " + slot.getName());
			}
			
			for (int i = 0; i < itemSet.length; i++)
			{
				String itemEncoded = itemSet[i];
				String[] params = itemEncoded.split("\\s+");
				
				String itemName;
				int meta;
				int number;
				
				if (params.length == 1)
				{
					itemName = params[0];
					meta = 0;
					number = 0;
				}
				else if (params.length == 3)
				{
					itemName = params[0];
					try
					{
						meta = Integer.parseInt(params[2]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException("Cyberware configuration error! Item " + (i + 1) + " for "
								+ slot.getName() + " slot has invalid metadata: '" + params[2] + "'");
					}
					try
					{
						number = Integer.parseInt(params[1]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException("Cyberware configuration error! Item " + (i + 1) + " for "
								+ slot.getName() + " slot has invalid number: '" + params[1] + "'");
					}
				}
				else
				{
					throw new RuntimeException("Cyberware configuration error! Item " + (i + 1) + " for "
							+ slot.getName() + " slot has too many arguments!");
				}
				
				Item item;
				try
				{
					item = CommandBase.getItemByText(null, itemName);
				}
				catch (NumberInvalidException e)
				{
					throw new RuntimeException("Cyberware configuration error! Item '" + (i + 1) + "' for "
							+ slot.getName() + " slot has a nonexistant item: " + itemName);
				}
				
				ItemStack stack = new ItemStack(item, number, meta);
				
				if (!CyberwareAPI.isCyberware(stack))
				{
					throw new RuntimeException("Cyberware configuration error! " + itemName + " is not a valid piece of cyberware!");
				}
				if ((CyberwareAPI.getCyberware(stack)).getSlot(stack) != slot)
				{
					throw new RuntimeException("Cyberware configuration error! " + itemEncoded + " will not fit in slot " + slot.getName());
				}
				
				startingStacks[index][i] = stack;
			}
			
			index++;
		}
	}
	
	public static ItemStack[] getStartingItems(EnumSlot slot)
	{
		return startingStacks[slot.ordinal()];
	}
	
}
