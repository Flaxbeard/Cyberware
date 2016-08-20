package flaxbeard.cyberware.common.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.NumItems;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;
import flaxbeard.cyberware.common.block.tile.TileEntityBeacon;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;

public class CyberwareDataHandler
{
	public static final CyberwareDataHandler INSTANCE = new CyberwareDataHandler();
	public static final String KEEP_WARE_GAMERULE = "cyberware_keepCyberware";
	public static final String DROP_WARE_GAMERULE = "cyberware_dropCyberware";

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event)
	{
		GameRules rules = event.getWorld().getGameRules();
		if(!rules.hasRule(KEEP_WARE_GAMERULE))
		{
			rules.addGameRule(KEEP_WARE_GAMERULE, Boolean.toString(CyberwareConfig.DEFAULT_KEEP), ValueType.BOOLEAN_VALUE);
		}
		if(!rules.hasRule(DROP_WARE_GAMERULE))
		{
			rules.addGameRule(DROP_WARE_GAMERULE, Boolean.toString(CyberwareConfig.DEFAULT_DROP), ValueType.BOOLEAN_VALUE);
		}
	}
	
	@SubscribeEvent
	public void attachCyberwareData(AttachCapabilitiesEvent.Entity event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			event.addCapability(CyberwareUserDataImpl.Provider.NAME, new CyberwareUserDataImpl.Provider());
		}
	}
	
	@SubscribeEvent
	public void playerDeathEvent(PlayerEvent.Clone event)
	{
		EntityPlayer p = event.getEntityPlayer();
		EntityPlayer o = event.getOriginal();
		if (event.isWasDeath())
		{
			if (p.worldObj.getWorldInfo().getGameRulesInstance().getBoolean(KEEP_WARE_GAMERULE))
			{
				if (CyberwareAPI.hasCapability(o) && CyberwareAPI.hasCapability(o))
				{
					CyberwareAPI.getCapability(p).deserializeNBT(CyberwareAPI.getCapability(o).serializeNBT());
				}
			}
		}
		else
		{
			if (CyberwareAPI.hasCapability(o) && CyberwareAPI.hasCapability(o))
			{
				CyberwareAPI.getCapability(p).deserializeNBT(CyberwareAPI.getCapability(o).serializeNBT());
			}
		}
	}
	
	@SubscribeEvent
	public void handleCyberzombieDrops(LivingDropsEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (e instanceof EntityPlayer && !e.worldObj.isRemote)
		{
			EntityPlayer p = (EntityPlayer) e;
			if ((p.worldObj.getWorldInfo().getGameRulesInstance().getBoolean(DROP_WARE_GAMERULE) && !p.worldObj.getWorldInfo().getGameRulesInstance().getBoolean(KEEP_WARE_GAMERULE)) || (p.worldObj.getWorldInfo().getGameRulesInstance().getBoolean(KEEP_WARE_GAMERULE) && event.getSource() == EssentialsMissingHandler.noessence))
			{
				if (CyberwareAPI.hasCapability(p))
				{
					ICyberwareUserData data = CyberwareAPI.getCapability(p);
					for (EnumSlot slot : EnumSlot.values())
					{
						ItemStack[] stacks = data.getInstalledCyberware(slot);
						ItemStack[] defaults = CyberwareConfig.getStartingItems(EnumSlot.values()[slot.ordinal()]).clone();
						for (ItemStack stack : stacks)
						{
							if (stack != null)
							{
								ItemStack toDrop = stack.copy();
								boolean found = false;
								for (ItemStack def : defaults)
								{
									if (CyberwareAPI.areCyberwareStacksEqual(def, toDrop))
									{
										if (toDrop.stackSize > def.stackSize)
										{
											toDrop.stackSize -= def.stackSize;
										}
										else
										{
											found = true;
										}
									}
								}
								
								if (!found)
								{
									EntityItem item = new EntityItem(p.worldObj, p.posX, p.posY, p.posZ, toDrop);
									p.worldObj.spawnEntityInWorld(item);
								}
							}
						}
					}
					data.resetWare(p);
				}
			}
		}
		if (!CyberwareConfig.NO_ZOMBIES && e instanceof EntityCyberZombie && ((EntityCyberZombie) e).hasWare && !e.worldObj.isRemote)
		{
			
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleCZSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		if (event.getEntityLiving() instanceof EntityZombie && !(event.getEntityLiving() instanceof EntityCyberZombie) && !(event.getEntityLiving() instanceof EntityPigZombie))
		{
			
			EntityZombie zombie = (EntityZombie) event.getEntityLiving();
			
			int tier = TileEntityBeacon.isInRange(zombie.worldObj, zombie.posX, zombie.posY, zombie.posZ);
			if (tier > 0)
			{
				float chance = (tier == 2 ? LibConstants.BEACON_CHANCE : (tier == 1 ? LibConstants.BEACON_CHANCE_INTERNAL :  LibConstants.LARGE_BEACON_CHANCE));
				if (CyberwareConfig.NO_ZOMBIES || !(event.getWorld().rand.nextFloat() < (chance / 100F))) return;

				
				EntityCyberZombie cyberZombie = new EntityCyberZombie(event.getWorld());
				if (event.getWorld().rand.nextFloat() < (LibConstants.BEACON_BRUTE_CHANCE / 100F))
				{
					boolean works = cyberZombie.setBrute();
				}
				cyberZombie.setLocationAndAngles(zombie.posX, zombie.posY, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);
	
				for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
				{
					cyberZombie.setItemStackToSlot(slot, zombie.getItemStackFromSlot(slot));
				}
				event.getWorld().spawnEntityInWorld(cyberZombie);
				zombie.deathTime = 19;
				zombie.setHealth(0F);
				return;
			}
		}
		if (event.getEntityLiving() instanceof EntityZombie && CyberwareConfig.CLOTHES && !(event.getEntityLiving() instanceof EntityPigZombie))
		{
			EntityZombie zom = (EntityZombie) event.getEntityLiving();

			if (!zom.worldObj.isRemote && zom.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null && zom.worldObj.rand.nextFloat() < LibConstants.ZOMBIE_SHADES_CHANCE / 100F)
			{
				if (zom.worldObj.rand.nextBoolean())
				{
					zom.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(CyberwareContent.shades));
				}
				else
				{
					zom.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(CyberwareContent.shades2));
				}
				
				zom.setDropChance(EntityEquipmentSlot.HEAD, .5F);
			}
			
			float chestRand = zom.worldObj.rand.nextFloat();
			
			if (!zom.worldObj.isRemote && zom.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null && chestRand < LibConstants.ZOMBIE_TRENCH_CHANCE / 100F)
			{
				
				ItemStack stack = new ItemStack(CyberwareContent.trenchcoat);
				int rand = zom.worldObj.rand.nextInt(3);
				if (rand == 0)
				{
					CyberwareContent.trenchcoat.setColor(stack, 0x664028);
				}
				else if (rand == 1)
				{
					CyberwareContent.trenchcoat.setColor(stack, 0xEAEAEA);
				}
				
				zom.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
				
				
				zom.setDropChance(EntityEquipmentSlot.CHEST, .5F);
			}
			else if (!zom.worldObj.isRemote && zom.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null && chestRand - (LibConstants.ZOMBIE_TRENCH_CHANCE / 100F) < LibConstants.ZOMBIE_BIKER_CHANCE / 100F)
			{
				
				ItemStack stack = new ItemStack(CyberwareContent.jacket);
				
				zom.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
				
				
				zom.setDropChance(EntityEquipmentSlot.CHEST, .5F);
			}
		}
	}
	
	public static void addRandomCyberware(EntityCyberZombie cyberZombie, boolean brute)
	{	
		ICyberwareUserData data = CyberwareAPI.getCapability(cyberZombie);
		List<List<ItemStack>> wares = new ArrayList<List<ItemStack>>();
		
		for (EnumSlot slot : EnumSlot.values())
		{
			List<ItemStack> toAdd = new ArrayList(Arrays.asList(data.getInstalledCyberware(slot)));
			toAdd.removeAll(Collections.singleton(null));
			wares.add(toAdd);
		}
		
		
		// Cyberzombies get all the power
		ItemStack battery = new ItemStack(CyberwareContent.creativeBattery);
		wares.get(CyberwareContent.creativeBattery.getSlot(battery).ordinal()).add(battery);
		
		int numberOfItemsToInstall = ((NumItems) WeightedRandom.getRandomItem(cyberZombie.worldObj.rand, CyberwareContent.numItems)).num;
		if (brute)
		{
			numberOfItemsToInstall += LibConstants.MORE_ITEMS_BRUTE;
		}
		
		List<ItemStack> installed = new ArrayList<ItemStack>();

		/*ItemStack re = new ItemStack(CyberwareContent.heartUpgrades, 1, 0);
		wares.get(((ICyberware) re.getItem()).getSlot(re).ordinal()).add(re);
		installed.add(re);*/
		
		List<ZombieItem> items = new ArrayList(CyberwareContent.zombieItems);
		for (int i = 0; i < numberOfItemsToInstall; i++)
		{
			int tries = 0;
			ItemStack randomItem = null;
			ICyberware randomWare = null;
			
			// Ensure we get a unique item
			do
			{
				randomItem = ItemStack.copyItemStack(((ZombieItem) WeightedRandom.getRandomItem(cyberZombie.worldObj.rand, items)).stack);
				randomWare = CyberwareAPI.getCyberware(randomItem);
				randomItem.stackSize = randomWare.installedStackSize(randomItem);
				tries++;
			}
			while (contains(wares.get(randomWare.getSlot(randomItem).ordinal()), randomItem) && tries < 10);
			
			if (tries < 10)
			{
				// Fulfill requirements
				ItemStack[][] required = randomWare.required(randomItem).clone();
				for (ItemStack[] requiredCategory : required)
				{
					boolean found = false;
					for (ItemStack option : requiredCategory)
					{
						ICyberware optionWare = CyberwareAPI.getCyberware(option);
						option.stackSize = optionWare.installedStackSize(option);
						if (contains(wares.get(optionWare.getSlot(option).ordinal()), option))
						{
							found = true;
							break;
						}
					}
					
					if (!found)
					{
						ItemStack req = requiredCategory[cyberZombie.worldObj.rand.nextInt(requiredCategory.length)].copy();
						ICyberware reqWare = CyberwareAPI.getCyberware(req);
						req.stackSize = reqWare.installedStackSize(req);
						wares.get(reqWare.getSlot(req).ordinal()).add(req);
						installed.add(req);
						i++;
					}
				}
				wares.get(randomWare.getSlot(randomItem).ordinal()).add(randomItem);
				installed.add(randomItem);
			}
		}
		
		/*
		System.out.println("_____LIST_____ " + numberOfItemsToInstall);
		for (ItemStack stack : installed)
		{
			System.out.println(stack.getUnlocalizedName() + " " + stack.stackSize);
		}*/
		
		for (EnumSlot slot : EnumSlot.values())
		{
			data.setInstalledCyberware(cyberZombie, slot, wares.get(slot.ordinal()));
		}
		data.updateCapacity();
		
		cyberZombie.setHealth(cyberZombie.getMaxHealth());
		cyberZombie.hasWare = true;
		
		CyberwareAPI.updateData(cyberZombie);
	}
	
	public static boolean contains(List<ItemStack> items, ItemStack item)
	{
		for (ItemStack check : items)
		{			
			if (check != null && item != null && check.getItem() == item.getItem() && check.getItemDamage() == item.getItemDamage())
			{
				return true;
			}
		}
		return false;
	}
	

	@SubscribeEvent
	public void syncCyberwareData(EntityJoinWorldEvent event)
	{
		if (!event.getWorld().isRemote)
		{
			Entity e = event.getEntity();
			if (CyberwareAPI.hasCapability(e))
			{
				if (e instanceof EntityPlayer)
				{
					//System.out.println("Sent data for player " + ((EntityPlayer) e).getName() + " to that player's client");
					NBTTagCompound nbt = CyberwareAPI.getCapability(e).serializeNBT();
					CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, e.getEntityId()), (EntityPlayerMP) e);
				}
			}
		}
	}

	@SubscribeEvent
	public void startTrackingEvent(StartTracking event)
	{			
		EntityPlayer tracker = event.getEntityPlayer();
		Entity target = event.getTarget();
		
		if (!target.worldObj.isRemote)
		{
			if (CyberwareAPI.hasCapability(target))
			{
				if (target instanceof EntityPlayer)
				{
					//System.out.println("Sent data for player " + ((EntityPlayer) target).getName() + " to player " + tracker.getName());
				}

				NBTTagCompound nbt = CyberwareAPI.getCapability(target).serializeNBT();
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, target.getEntityId()), (EntityPlayerMP) tracker);
			}
		}
		
	}

}
