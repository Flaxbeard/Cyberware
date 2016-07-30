package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
	
	private Map<EntityLivingBase, Boolean> lastAqua = new HashMap<EntityLivingBase, Boolean>();

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test) && e.isInWater())
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
					e.moveRelative(0F, numLegs * 0.5F, 0.075F);
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
		return stack.getItemDamage() == 0 ? LibConstants.JUMPBOOST_CONSUMPTION : LibConstants.AQUA_CONSUMPTION;
	}
}
