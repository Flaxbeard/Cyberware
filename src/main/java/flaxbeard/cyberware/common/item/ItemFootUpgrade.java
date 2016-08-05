package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemFootUpgrade extends ItemCyberware
{

	public ItemFootUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		if (stack.getItemDamage() == 0) return new ItemStack[0][0];
		
		return new ItemStack[][] { 
				new ItemStack[] { new ItemStack(CyberwareContent.cyberlimbs, 1, 2), new ItemStack(CyberwareContent.cyberlimbs, 1, 3) }};
	}
	
	@SubscribeEvent
	public void handleHorseMove(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (e instanceof EntityHorse)
		{
			EntityHorse horse = (EntityHorse) e;
			for (Entity pass : horse.getPassengers())
			{
				if (pass instanceof EntityLivingBase && CyberwareAPI.isCyberwareInstalled(pass, new ItemStack(this, 1, 0)))
				{
					horse.addPotionEffect(new PotionEffect(MobEffects.SPEED, 1, 5, true, false));
					break;
				}
			}
		}
	}

	
	private Map<EntityLivingBase, Boolean> lastAqua = new HashMap<EntityLivingBase, Boolean>();

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test) && e.isInWater() && !e.onGround)
		{
			int numLegs = 0;
			if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 2)))
			{
				numLegs++;
			}
			if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 3)))
			{
				numLegs++;
			}
			boolean last = getLastAqua(e);

			boolean powerUsed = e.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)) : last;
			if (powerUsed)
			{
				if (e.moveForward > 0)
				{
					e.moveRelative(0F, numLegs * 0.4F, 0.075F);
				}
			}
			
			lastAqua.put(e, powerUsed);
		}
		else
		{
			lastAqua.put(e, true);
		}
	}
	
	private boolean getLastAqua(EntityLivingBase e)
	{
		if (!lastAqua.containsKey(e))
		{
			lastAqua.put(e, true);
		}
		return lastAqua.get(e);
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? 0 : LibConstants.AQUA_CONSUMPTION;
	}
}
