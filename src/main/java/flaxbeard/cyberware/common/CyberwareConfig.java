package flaxbeard.cyberware.common;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.lib.LibConstants;

public class CyberwareConfig
{
	public static float DROP_RARITY = 50F;
	public static float ZOMBIE_RARITY = 15F;
	public static boolean NO_ZOMBIES = false;
	public static boolean SURGERY_CRAFTING = false;
	private static String[][] defaultStartingItems;
	private static String[][] startingItems;
	private static ItemStack[][] startingStacks;
	public static int TESLA_PER_POWER = 1;

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
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), Cyberware.MODID + ".cfg"));
		config.load();
		
		for (int index = 0; index < EnumSlot.values().length; index++)
		{
			EnumSlot slot = EnumSlot.values()[index];
			startingItems[index] = config.getStringList("Default augments for " + slot.getName() + " slot",
					"Defaults", defaultStartingItems[index], "Use format 'id amount metadata'");
		}
		
		NO_ZOMBIES = config.getBoolean("Disable cyberzombies", "Mobs", NO_ZOMBIES, "");
		ZOMBIE_RARITY = config.getFloat("Percent of zombies that are Cyberzombies", "Mobs", ZOMBIE_RARITY, 0F, 100F, "");
		DROP_RARITY = config.getFloat("Percent chance a Cyberzombie drops an item", "Mobs", DROP_RARITY, 0F, 100F, "");
		SURGERY_CRAFTING = config.getBoolean("Enable crafting recipe for Robosurgeon", "Other", SURGERY_CRAFTING, "Normally only found in Nether fortresses");
		TESLA_PER_POWER = config.getInt("RF/Tesla per internal power unit", "Other", TESLA_PER_POWER, 0, Integer.MAX_VALUE, "");
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
