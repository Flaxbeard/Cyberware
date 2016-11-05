package flaxbeard.cyberware.common;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.UpdateConfigPacket;

public class CyberwareConfig
{
	public static CyberwareConfig INSTANCE = new CyberwareConfig();
	
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
	
	public static float HUDLENS_FLOAT = 0.1F;
	public static float HUDJACK_FLOAT = 0.05F;
	
	public static boolean SURGERY_CRAFTING = false;
	
	private static String[][] defaultStartingItems;
	private static String[][] startingItems;
	private static ItemStack[][] startingStacks;
	
	public static boolean DEFAULT_DROP = false;
	public static boolean DEFAULT_KEEP = false;

	public static boolean KATANA = true;
	public static boolean CLOTHES = true;
	public static boolean RENDER = true;
	
	public static int TESLA_PER_POWER = 1;
	
	public static Configuration config;
	
	public static File configDirectory;
	
	private static final String C_MOBS = "Mobs";
	private static final String C_OTHER = "Other";
	private static final String C_HUD = "HUD";
	private static final String C_MACHINES = "Machines";
	private static final String C_ESSENCE = "Essence";
	private static final String C_GAMERULES = "Gamerules";

	public static void preInit(FMLPreInitializationEvent event)
	{
		configDirectory = event.getModConfigurationDirectory();
		config = new Configuration(new File(event.getModConfigurationDirectory(), Cyberware.MODID + ".cfg"));
		loadConfig();
	}
	
	public static void loadConfig()
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
		
		config.load();
		
		for (int index = 0; index < EnumSlot.values().length; index++)
		{
			EnumSlot slot = EnumSlot.values()[index];
			startingItems[index] = config.getStringList("Default augments for " + slot.getBaseName() + " slot",
					"Defaults", defaultStartingItems[index], "Use format 'id amount metadata'");
		}
		
		NO_ZOMBIES = config.getBoolean("Disable cyberzombies", C_MOBS, NO_ZOMBIES, "");
		ZOMBIE_WEIGHT = config.getInt("Spawning weight of Cyberzombies", C_MOBS, ZOMBIE_WEIGHT, 0, Integer.MAX_VALUE, "Vanilla Zombie = 100, Enderman = 10, Witch = 5");
		ZOMBIE_MIN_PACK = config.getInt("Minimum Cyberzombie pack size", C_MOBS, ZOMBIE_MIN_PACK, 0, Integer.MAX_VALUE, "Vanilla Zombie = 4, Enderman = 1, Witch = 1");
		ZOMBIE_MAX_PACK = config.getInt("Maximum Cyberzombie pack size", C_MOBS, ZOMBIE_MAX_PACK, 0, Integer.MAX_VALUE, "Vanilla Zombie = 4, Enderman = 4, Witch = 1");

		DROP_RARITY = config.getFloat("Percent chance a Cyberzombie drops an item", C_MOBS, DROP_RARITY, 0F, 100F, "");
		
		SURGERY_CRAFTING = config.getBoolean("Enable crafting recipe for Robosurgeon", C_OTHER, SURGERY_CRAFTING, "Normally only found in Nether fortresses");
		TESLA_PER_POWER = config.getInt("RF/Tesla per internal power unit", C_OTHER, TESLA_PER_POWER, 0, Integer.MAX_VALUE, "");
		
		ESSENCE = config.getInt("Maximum Essence", C_ESSENCE, ESSENCE, 0, Integer.MAX_VALUE, "");
		CRITICAL_ESSENCE = config.getInt("Critical Essence value, where rejection begins", C_ESSENCE, CRITICAL_ESSENCE, 0, Integer.MAX_VALUE, "");
		
		DEFAULT_DROP = config.getBoolean("Default for gamerule cyberware_dropCyberware", C_GAMERULES, DEFAULT_DROP, "Determines if players drop their Cyberware on death. Does not change settings on existing worlds, use /gamerule for that. Overridden if cyberware_keepCyberware is true");
		DEFAULT_KEEP = config.getBoolean("Default for gamerule cyberware_keepCyberware", C_GAMERULES, DEFAULT_KEEP, "Determines if players keep their Cyberware between lives. Does not change settings on existing worlds, use /gamerule for that.");

		ENGINEERING_CHANCE = config.getFloat("Chance of blueprint from Engineering Table", C_MACHINES, ENGINEERING_CHANCE, 0, 100F, "");
		SCANNER_CHANCE = config.getFloat("Chance of blueprint from Scanner", C_MACHINES, SCANNER_CHANCE, 0, 100F, "");
		SCANNER_CHANCE_ADDL = config.getFloat("Additive chance for Scanner per extra item", C_MACHINES, SCANNER_CHANCE_ADDL, 0, 100F, "");
		SCANNER_TIME = config.getInt("Ticks taken per Scanner operation", C_MACHINES, SCANNER_TIME, 0, Integer.MAX_VALUE, "24000 is one Minecraft day, 1200 is one real-life minute");
		
		KATANA = config.getBoolean("Enable Katana", C_OTHER, KATANA, "");
		CLOTHES = config.getBoolean("Enable Trenchcoat, Mirrorshades, and Biker Jacket", C_OTHER, CLOTHES, "");
		
		RENDER = config.getBoolean("Enable changes to player model (missing skin, missing limbs, Cybernetic limbs)", C_OTHER, RENDER, "");
		HUDJACK_FLOAT = config.getFloat("Amount hudjack HUD will 'float' with movement. Set to 0 for no float.", C_HUD, HUDJACK_FLOAT, 0F, 100F, "");
		HUDLENS_FLOAT = config.getFloat("Amount hudlens HUD will 'float' with movement. Set to 0 for no float.", C_HUD, HUDLENS_FLOAT, 0F, 100F, "");

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
				throw new RuntimeException("Cyberware configuration error! Too many items for slot " + slot.getBaseName());
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
								+ slot.getBaseName() + " slot has invalid metadata: '" + params[2] + "'");
					}
					try
					{
						number = Integer.parseInt(params[1]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException("Cyberware configuration error! Item " + (i + 1) + " for "
								+ slot.getBaseName() + " slot has invalid number: '" + params[1] + "'");
					}
				}
				else
				{
					throw new RuntimeException("Cyberware configuration error! Item " + (i + 1) + " for "
							+ slot.getBaseName() + " slot has too many arguments!");
				}
				
				Item item;
				try
				{
					item = CommandBase.getItemByText(null, itemName);
				}
				catch (NumberInvalidException e)
				{
					throw new RuntimeException("Cyberware configuration error! Item '" + (i + 1) + "' for "
							+ slot.getBaseName() + " slot has a nonexistant item: " + itemName);
				}
				
				ItemStack stack = new ItemStack(item, number, meta);
				
				if (!CyberwareAPI.isCyberware(stack))
				{
					throw new RuntimeException("Cyberware configuration error! " + itemName + " is not a valid piece of cyberware!");
				}
				if ((CyberwareAPI.getCyberware(stack)).getSlot(stack) != slot)
				{
					throw new RuntimeException("Cyberware configuration error! " + itemEncoded + " will not fit in slot " + slot.getBaseName());
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		if (event.getWorld().isRemote && !Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen())
		{
			loadConfig();
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.worldObj;
		
		if (!world.isRemote)
		{
			CyberwarePacketHandler.INSTANCE.sendTo(new UpdateConfigPacket(), (EntityPlayerMP) player);  
		}
	}
	
	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equalsIgnoreCase(Cyberware.MODID))
		{
			loadConfig();
		}
	}

}
