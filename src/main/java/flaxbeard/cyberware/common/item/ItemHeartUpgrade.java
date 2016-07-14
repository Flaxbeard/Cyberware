package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemHeartUpgrade extends ItemCyberware
{

	public ItemHeartUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == CyberwareContent.cyberheart && stack.getItemDamage() == 0;
	}
	
	@SubscribeEvent
	public void handleDeath(LivingDeathEvent event)
	{

		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)) && !event.isCanceled())
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			ItemStack[] items = cyberware.getInstalledCyberware(EnumSlot.HEART);
			ItemStack[] itemsNew = items.clone();
			for (int i = 0; i < items.length; i++)
			{
				ItemStack item = items[i];
				if (item != null && item.getItem() == this && item.getItemDamage() == 0)
				{
					itemsNew[i] = null;
					break;
				}
			}
			cyberware.setInstalledCyberware(EnumSlot.HEART, itemsNew);
			if (!e.worldObj.isRemote)
			{
				CyberwareAPI.updateData(e);
			}
			e.setHealth(e.getMaxHealth() / 3F);
			event.setCanceled(true);
		}
	}
	
	private static Map<EntityLivingBase, Integer> timesPlatelets = new HashMap<EntityLivingBase, Integer>();

	@SubscribeEvent
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{
			if (e.getHealth() >= e.getMaxHealth() * .8F && e.getHealth() != e.getMaxHealth())
			{
				int t = getPlateletTime(e);
				if (t >= 40)
				{
					timesPlatelets.put(e, e.ticksExisted);
					e.heal(1);
				}
			}
			else
			{
				timesPlatelets.put(e, e.ticksExisted);
			}
		}
		else
		{
			if (timesPlatelets.containsKey(e))
			{
				timesPlatelets.remove(e);
			}
		}
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{

			int t = getMedkitTime(e);
			if (t >= 100 && damageMedkit.get(e) > 0F)
			{
				e.heal(damageMedkit.get(e));
				damageMedkit.put(e, 0F);
			}

		}
		else
		{
			if (timesMedkit.containsKey(e))
			{
				timesMedkit.remove(e);
				damageMedkit.remove(e);
			}
		}
	}
	
	private static Map<EntityLivingBase, Integer> timesMedkit = new HashMap<EntityLivingBase, Integer>();
	private static Map<EntityLivingBase, Float> damageMedkit = new HashMap<EntityLivingBase, Float>();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleHurt(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{
			float damageAmount = event.getAmount();
			DamageSource damageSrc = event.getSource();

			damageAmount = applyArmorCalculations(e, damageSrc, damageAmount);
			damageAmount = applyPotionDamageCalculations(e, damageSrc, damageAmount);
			damageAmount = Math.max(damageAmount - e.getAbsorptionAmount(), 0.0F);
			
			damageMedkit.put(e, damageAmount);
			timesMedkit.put(e, e.ticksExisted);
		}
	}
	
	// Stolen from EntityLivingBase
    protected float applyArmorCalculations(EntityLivingBase e, DamageSource source, float damage)
    {
        if (!source.isUnblockable())
        {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)e.getTotalArmorValue(), (float)e.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }

        return damage;
    }
	
	// Stolen from EntityLivingBase
	protected float applyPotionDamageCalculations(EntityLivingBase e, DamageSource source, float damage)
	{
		if (source.isDamageAbsolute())
		{
			return damage;
		}
		else
		{
			if (e.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.outOfWorld)
			{
				int i = (e.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = damage * (float)j;
				damage = f / 25.0F;
			}

			if (damage <= 0.0F)
			{
				return 0.0F;
			}
			else
			{
				int k = EnchantmentHelper.getEnchantmentModifierDamage(e.getArmorInventoryList(), source);

				if (k > 0)
				{
					damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
				}

				return damage;
			}
		}
	}
	
	private int getPlateletTime(EntityLivingBase e)
	{
		if (!timesPlatelets.containsKey(e))
		{
			timesPlatelets.put(e, e.ticksExisted);
		}
		return e.ticksExisted - timesPlatelets.get(e);
	}
	
	private int getMedkitTime(EntityLivingBase e)
	{
		if (!timesMedkit.containsKey(e))
		{
			timesMedkit.put(e, e.ticksExisted);
			damageMedkit.put(e, 0F);
		}
		return e.ticksExisted - timesMedkit.get(e);
	}
}
