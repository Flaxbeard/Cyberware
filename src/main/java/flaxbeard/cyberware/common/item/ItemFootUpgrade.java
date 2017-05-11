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
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemFootUpgrade extends ItemCyberware implements IMenuItem
{

	public ItemFootUpgrade(String name, EnumSlot[] slot, String[] subnames)
	{
		super(name, new EnumSlot[][] { slot }, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		if (stack.getItemDamage() != 1) return new ItemStack[0][0];
		
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

	
	private Map<Integer, Boolean> lastAqua = new HashMap<Integer, Boolean>();
	private Map<Integer, Integer> lastWheels = new HashMap<Integer, Integer>();
	private Map<Integer, Float> stepAssist = new HashMap<Integer, Float>();

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test) && e.isInWater() && !e.onGround)
		{
			int numLegs = 0;
			if (CyberwareAPI.isCyberwareInstalledInSlot(e, test, EnumSlot.FOOT))
			{
				numLegs++;
			}
			if (CyberwareAPI.isCyberwareInstalledInSlot(e, test, EnumSlot.FOOTLEFT))
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
			
			lastAqua.put(e.getEntityId(), powerUsed);
		}
		else
		{
			lastAqua.put(e.getEntityId(), true);
		}
		
		test = new ItemStack(this, 1, 2);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
		{
			boolean last = getLastWheels(e) > 0;

			boolean powerUsed = EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)) && (e.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)) : last);
			if (powerUsed)
			{
				if (!stepAssist.containsKey(e.getEntityId()))
				{
					stepAssist.put(e.getEntityId(), Math.max(e.stepHeight, .6F));
				}
				e.stepHeight = 1F;
				
				lastWheels.put(e.getEntityId(), 10);


			}
			else if (stepAssist.containsKey(e.getEntityId()) && last)
			{

				e.stepHeight = stepAssist.get(e.getEntityId());
				
				lastWheels.put(e.getEntityId(), getLastWheels(e) - 1);
			}
			else
			{
				lastWheels.put(e.getEntityId(), 0);
			}
		
			
		}
		else if (stepAssist.containsKey(e.getEntityId()))
		{

			e.stepHeight = stepAssist.get(e.getEntityId());
			
			int glw = getLastWheels(e) - 1;
			
			if (glw == 0)
			{
				stepAssist.remove(e.getEntityId());
			}

			lastWheels.put(e.getEntityId(), glw);

		}
	}
	
	private boolean getLastAqua(EntityLivingBase e)
	{
		if (!lastAqua.containsKey(e.getEntityId()))
		{
			lastAqua.put(e.getEntityId(), true);
		}
		return lastAqua.get(e.getEntityId());
	}
	
	private int getLastWheels(EntityLivingBase e)
	{
		if (!lastWheels.containsKey(e.getEntityId()))
		{
			lastWheels.put(e.getEntityId(), 10);
		}
		return lastWheels.get(e.getEntityId());
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 1 ? LibConstants.AQUA_CONSUMPTION :
				stack.getItemDamage() == 2 ? LibConstants.WHEEL_CONSUMPTION : 0;
	}
	
	@Override
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 2;
	}

	@Override
	public void use(Entity e, ItemStack stack)
	{
		EnableDisableHelper.toggle(stack);
	}

	@Override
	public String getUnlocalizedLabel(ItemStack stack)
	{
		return EnableDisableHelper.getUnlocalizedLabel(stack);
	}
	

	private static final float[] f = new float[] { 1F, 0F, 0F };
	
	@Override
	public float[] getColor(ItemStack stack)
	{
		return EnableDisableHelper.isEnabled(stack) ? f : null;
	}
}
