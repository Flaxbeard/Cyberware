package flaxbeard.cyberware.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;

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


}
