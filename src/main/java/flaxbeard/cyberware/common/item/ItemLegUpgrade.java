package flaxbeard.cyberware.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemLegUpgrade extends ItemCyberware
{

	public ItemLegUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{		
		return new ItemStack[][] { 
				new ItemStack[] { new ItemStack(CyberwareContent.cyberlimbs, 1, 2), new ItemStack(CyberwareContent.cyberlimbs, 1, 3) }};
	}
	
	
	@SubscribeEvent
	public void playerJumps(LivingEvent.LivingJumpEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 0);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
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
			ICyberwareUserData ware = CyberwareAPI.getCapability(e);
			if (ware.usePower(test, this.getPowerConsumption(test)))
			{
				if (e.isSneaking())
				{
					Vec3d vector = e.getLook(0.5F);
					double total = Math.abs(vector.zCoord + vector.xCoord);
					double jump = 0;
					if (jump >= 1)
					{
						jump = (jump + 2D) / 4D;
					}

					double y = vector.yCoord < total ? total : vector.yCoord;

					e.motionY += (numLegs * ((jump + 1) * y)) / 3F;
					e.motionZ += (jump + 1) * vector.zCoord * numLegs;
					e.motionX += (jump + 1) * vector.xCoord * numLegs;
				}
				else
				{
					e.motionY += numLegs * (0.2750000059604645D / 2D);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onFallDamage(LivingAttackEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (event.getSource() == DamageSource.fall && event.getAmount() <= 6F && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			event.setCanceled(true);
		}
	}

	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? LibConstants.JUMPBOOST_CONSUMPTION : 0;
	}
}
