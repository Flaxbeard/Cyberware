package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemLowerOrgansUpgrade extends ItemCyberware
{

	public ItemLowerOrgansUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	private static Map<EntityLivingBase, Collection<PotionEffect>> potions = new HashMap<EntityLivingBase, Collection<PotionEffect>>();

	@SubscribeEvent
	public void handleEatFoodTick(LivingEntityUseItemEvent.Tick event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack stack = event.getItem();
		
		if (e instanceof EntityPlayer && CyberwareAPI.hasCapability(e) && stack != null && (stack.getItem().getItemUseAction(stack) == EnumAction.EAT || stack.getItem().getItemUseAction(stack) == EnumAction.DRINK))
		{
			EntityPlayer p = (EntityPlayer) e;
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0)))
			{
				potions.put(p, new ArrayList<PotionEffect>(p.getActivePotionEffects()));

			}
		}
		else
		{
			//potions.remove(e);
		}
	}
	
	@SubscribeEvent
	public void handleEatFoodEnd(LivingEntityUseItemEvent.Finish event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack stack = event.getItem();
		
		if (e instanceof EntityPlayer && CyberwareAPI.hasCapability(e) && stack != null && (stack.getItem().getItemUseAction(stack) == EnumAction.EAT || stack.getItem().getItemUseAction(stack) == EnumAction.DRINK))
		{
			EntityPlayer p = (EntityPlayer) e;
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0)))
			{
				Collection<PotionEffect> toRemove = new ArrayList<PotionEffect>(p.getActivePotionEffects());
				for (PotionEffect pE : toRemove)
				{
					if (pE.getPotion().isBadEffect())
					{
						p.removePotionEffect(pE.getPotion());
					}
				}
				
				Collection<PotionEffect> toAdd = potions.keySet().contains(p) ? potions.get(p) : new ArrayList<PotionEffect>();

				for (PotionEffect add : toAdd)
				{
					for (PotionEffect removed : toRemove)
					{
						if (removed.getPotion() == add.getPotion())
						{
							p.addPotionEffect(add);
							break;
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void power(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
		{
			ItemStack stack = CyberwareAPI.getCyberware(e, test);
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (e.ticksExisted % 20 == 0 && !cyberware.isAtCapacity(test, getPowerProduction(stack)))
			{
				if (e instanceof EntityPlayer)
				{
					EntityPlayer p = (EntityPlayer) e;
					if (p.getFoodStats().getFoodLevel() > 0 || p.isCreative())
					{
						int toRemove = getTicksTilRemove(stack);
						if (!p.isCreative() && toRemove <= 0)
						{
							p.getFoodStats().setFoodLevel(p.getFoodStats().getFoodLevel() - 1);
							toRemove = LibConstants.METABOLIC_USES;
						}
						else if (toRemove > 0)
						{
							toRemove--;
						}
						CyberwareAPI.getCyberwareNBT(stack).setInteger("toRemove", toRemove);
						
						cyberware.addPower(getPowerProduction(test), test);
					}
				}
				else
				{
					cyberware.addPower(getPowerProduction(test) / 10, test);
				}
				
			}
		}
		
		ItemStack test2 = new ItemStack(this, 1, 3);
		if (CyberwareAPI.isCyberwareInstalled(e, test2))
		{
			ItemStack stack = CyberwareAPI.getCyberware(e, test2);
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);

			boolean wasBelow = wasBelow(stack);
			boolean isBelow = false;
			if (e.getMaxHealth() > 8 && e.getHealth() < 8)
			{
				isBelow = true;

				if (!wasBelow && cyberware.usePower(stack, this.getPowerConsumption(stack), false))
				{
					//System.out.println(wasBelow);
					e.addPotionEffect(new PotionEffect(MobEffects.SPEED, 600, 0, true, false));
					e.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 600, 0, true, false));
				}
				
			}
			
			CyberwareAPI.getCyberwareNBT(stack).setBoolean("wasBelow", isBelow);
		}
	}
	
	private int getTicksTilRemove(ItemStack stack)
	{
		NBTTagCompound data = CyberwareAPI.getCyberwareNBT(stack);
		if (!data.hasKey("toRemove"))
		{
			data.setInteger("toRemove", LibConstants.METABOLIC_USES);
		}
		return data.getInteger("toRemove");
	}
	
	private boolean wasBelow(ItemStack stack)
	{
		NBTTagCompound data = CyberwareAPI.getCyberwareNBT(stack);
		if (!data.hasKey("toRemove"))
		{
			data.setBoolean("wasBelow", false);
		}
		return data.getBoolean("wasBelow");
	}

	@Override
	public int getCapacity(ItemStack wareStack)
	{
		return wareStack.getItemDamage() == 1 ? LibConstants.METABOLIC_PRODUCTION : 
			wareStack.getItemDamage() == 2 ? LibConstants.BATTERY_CAPACITY * wareStack.stackSize : 0;
	}
	
	@Override
	public int installedStackSize(ItemStack stack)
	{
		return stack.getItemDamage() == 2 ? 4 : 1;
	}
	
	@Override
	public int getPowerProduction(ItemStack stack)
	{
		return stack.getItemDamage() == 1 ? LibConstants.METABOLIC_PRODUCTION : 0;
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 3 ? LibConstants.ADRENALINE_CONSUMPTION : 0;
	}
	
	@Override
	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		if (stack.getItemDamage() == 2)
		{
			switch (stack.stackSize) // 0.2.0 changed from 10 11 13 15
			{
				case 1:
					return 5;
				case 2:
					return 7;
				case 3:
					return 9;
				case 4:
					return 11;
			}
		}
		return super.getUnmodifiedEssenceCost(stack);
	}
}
