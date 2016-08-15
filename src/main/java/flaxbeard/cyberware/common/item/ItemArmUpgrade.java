package flaxbeard.cyberware.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemArmUpgrade extends ItemCyberware
{

	public ItemArmUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{		
		return new ItemStack[][] { 
				new ItemStack[] { new ItemStack(CyberwareContent.cyberlimbs, 1, 0), new ItemStack(CyberwareContent.cyberlimbs, 1, 1) }};
	}
	
	
	@SubscribeEvent
	public void useBow(LivingEntityUseItemEvent.Tick event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 0);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
		{
			ItemStack item = event.getItem();
			
			if (item != null && item.getItem() instanceof ItemBow)
			{
				event.setDuration(event.getDuration() - 1);
			}
		}
	}

}
