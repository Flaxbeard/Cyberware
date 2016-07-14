package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemCyberheart extends ItemCyberware
{

	public ItemCyberheart(String name, EnumSlot slot)
	{
		super(name, slot);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isEssential(ItemStack stack)
	{
		return true;		
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return CyberwareAPI.getCyberware(other).isEssential(other);
	}
	
	@SubscribeEvent
	public void handleBlindnessImmunity(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this)))
		{
			e.removePotionEffect(MobEffects.WEAKNESS);
		}
		
	}

}
