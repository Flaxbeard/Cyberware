package flaxbeard.cyberware.common.integration.tan;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.config.GameplayOption;
import toughasnails.config.SyncedConfigHandler;
import toughasnails.temperature.TemperatureHandler;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemToughAsNailsUpgrade extends ItemCyberware
{

	public ItemToughAsNailsUpgrade(String name, EnumSlot[] slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 0);
		if (e instanceof EntityPlayer && CyberwareAPI.isCyberwareInstalled(e, test) && !e.worldObj.isRemote)
		{
			
			EntityPlayer p = (EntityPlayer) e;
			IThirst thirstData = ThirstHelper.getThirstData(p);
			ITemperature tempData = TemperatureHelper.getTemperatureData(p);

			if (tempData.getTemperature().getRawValue() >= LibConstants.SWEAT_TEMP && thirstData.getHydration() > 0F)
			{
				addExhaustion(thirstData, 0.008F);
			
				boolean cooling = false;
				if (tempData instanceof TemperatureHandler)
				{
					// If the player is cooling, sweat speeds the change, otherwise it slows it
					cooling = ((TemperatureHandler) tempData).debugger.targetTemperature < tempData.getTemperature().getRawValue();
				}
				
				if (cooling)
				{
					tempData.applyModifier("cyberwareCoolSweat", -3, +100, 1);
				}
				else
				{
					tempData.applyModifier("cyberwareHeatSweat", -3, -500, 1);
				}
			}

		}
		
		test = new ItemStack(this, 1, 1);
		if (e instanceof EntityPlayer && CyberwareAPI.isCyberwareInstalled(e, test) && !e.worldObj.isRemote)
		{
			
			EntityPlayer p = (EntityPlayer) e;
			ITemperature tempData = TemperatureHelper.getTemperatureData(p);

			boolean heating = false;
			if (tempData instanceof TemperatureHandler)
			{
				// If the player is heating, blubber speeds the change, otherwise it slows it
				heating = !(((TemperatureHandler) tempData).debugger.targetTemperature < tempData.getTemperature().getRawValue());
			}
			
			if (heating)
			{
				tempData.applyModifier("cyberwareHeatBlubber", 2, +50, 1);
			}
			else
			{
				tempData.applyModifier("cyberwareCoolBlubber", 2, -100, 1);
			}
			
		}
	}
	
	public void addExhaustion(IThirst data, float amount)
	{
		if (SyncedConfigHandler.getBooleanValue(GameplayOption.ENABLE_THIRST))
		{
			data.setExhaustion(Math.min(data.getExhaustion() + amount, 40.0F));
		}
	}


}
