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
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.NumItems;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;

public class CyberwareDataHandler
{
	public static final CyberwareDataHandler INSTANCE = new CyberwareDataHandler();

	@SubscribeEvent
	public void attachCyberwareData(AttachCapabilitiesEvent.Entity event)
	{
		if (event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityCyberZombie)
		{
			event.addCapability(CyberwareUserDataImpl.Provider.NAME, new CyberwareUserDataImpl.Provider());
		}
	}
	
	@SubscribeEvent
	public void playerDeathEvent(PlayerEvent.Clone event)
	{
	}
	
	@SubscribeEvent
	public void handleCyberzombieDrops(LivingDropsEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (e instanceof EntityCyberZombie && CyberwareAPI.hasCapability(e))
		{
			if (e.worldObj.rand.nextFloat() < (CyberwareConfig.DROP_RARITY / 100F))
			{
				ICyberwareUserData data = CyberwareAPI.getCapability(e);
				List<ItemStack> allWares = new ArrayList<ItemStack>();
				for (EnumSlot slot : EnumSlot.values())
				{
					ItemStack[] stuff = data.getInstalledCyberware(slot);
					
					allWares.addAll(Arrays.asList(stuff));
				}
				
				allWares.removeAll(Collections.singleton(null));
				
				ItemStack drop = null;
				int count = 0;
				while (count < 50 && (drop == null || drop.getItem() == CyberwareContent.creativeBattery || drop.getItem() == CyberwareContent.bodyPart))
				{
					int random = e.worldObj.rand.nextInt(allWares.size());
					drop = ItemStack.copyItemStack(allWares.get(random));
					drop.stackSize = 1;
					count++;
				}
				if (count < 50)
				{
					EntityItem ei = new EntityItem(e.worldObj, e.posX, e.posY, e.posZ, drop);
					e.worldObj.spawnEntityInWorld(ei);
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleSpawn(SpecialSpawn event)
	{
		if (event.getEntityLiving() instanceof EntityZombie && !(event.getEntityLiving() instanceof EntityCyberZombie) && !(event.getEntityLiving() instanceof EntityPigZombie))
		{
			if (CyberwareConfig.NO_ZOMBIES || !(event.getWorld().rand.nextFloat() < (CyberwareConfig.ZOMBIE_RARITY / 100F))) return;
			
			EntityZombie zombie = (EntityZombie) event.getEntityLiving();
			EntityCyberZombie cyberZombie = new EntityCyberZombie(event.getWorld());
			cyberZombie.setLocationAndAngles(zombie.posX, zombie.posY, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);

			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
			{
				cyberZombie.setItemStackToSlot(slot, zombie.getItemStackFromSlot(slot));
			}
			event.getWorld().spawnEntityInWorld(cyberZombie);
			event.setCanceled(true);
			zombie.deathTime = 19;
			zombie.setHealth(0F);
			addRandomCyberware(cyberZombie);
		}
	}
	
	private void addRandomCyberware(EntityCyberZombie cyberZombie)
	{
		ICyberwareUserData data = CyberwareAPI.getCapability(cyberZombie);
		
		List<List<ItemStack>> wares = new ArrayList<List<ItemStack>>();
		
		for (EnumSlot slot : EnumSlot.values())
		{
			wares.add(new ArrayList(Arrays.asList(data.getInstalledCyberware(slot))));
		}
		
		
		// Cyberzombies get all the power
		ItemStack battery = new ItemStack(CyberwareContent.creativeBattery);
		wares.get(CyberwareContent.creativeBattery.getSlot(battery).ordinal()).add(battery);
		
		int numberOfItemsToInstall = ((NumItems) WeightedRandom.getRandomItem(cyberZombie.worldObj.rand, CyberwareContent.numItems)).num;
		
		List<ItemStack> installed = new ArrayList<ItemStack>();

		//ItemStack re = new ItemStack(CyberwareContent.heartUpgrades, 1, 2);
		//wares.get(((ICyberware) re.getItem()).getSlot(re).ordinal()).add(re);
		//installed.add(re);
		
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
		
		
		/*System.out.println("_____LIST_____ " + numberOfItemsToInstall);
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
	}
	
	public boolean contains(List<ItemStack> items, ItemStack item)
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
