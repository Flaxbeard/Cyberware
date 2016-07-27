package flaxbeard.cyberware.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware;
import flaxbeard.cyberware.api.ICyberware.ISidedLimb;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemCyberlimb extends ItemCyberware implements ISidedLimb
{
	
	public ItemCyberlimb(String name, EnumSlot[] slots, String[] subnames)
	{
		super(name, slots, subnames);
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
		ICyberware ware = CyberwareAPI.getCyberware(other);
		
		if (ware instanceof ISidedLimb)
		{
			return ware.isEssential(other) && ((ISidedLimb) ware).getSide(other) == this.getSide(stack);
		}
		return false;
	}
	
	@Override
	public EnumSide getSide(ItemStack stack)
	{
		return stack.getItemDamage() % 2 == 0 ? EnumSide.LEFT : EnumSide.RIGHT;
	}
	
	public static boolean isPowered(ItemStack stack)
	{
		if (!stack.hasTagCompound())
		{
			NBTTagCompound comp = new NBTTagCompound();
			stack.setTagCompound(comp);
		}
		if (!stack.getTagCompound().hasKey("active"))
		{
			stack.getTagCompound().setBoolean("active", true);
		}
		return stack.getTagCompound().getBoolean("active");
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void power(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		for (int i = 0; i < 4; i++)
		{
			ItemStack test = new ItemStack(this, 1, i);
			ItemStack installed = CyberwareAPI.getCyberware(e, test);
			if (e.ticksExisted % 20 == 0 && installed != null)
			{
				boolean used = CyberwareAPI.getCapability(e).usePower(installed, getPowerConsumption(installed));
				if (!installed.hasTagCompound())
				{
					NBTTagCompound comp = new NBTTagCompound();
					installed.setTagCompound(comp);
				}
				installed.getTagCompound().setBoolean("active", used);
			}
		}

	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return LibConstants.LIMB_CONSUMPTION;
	}
}
