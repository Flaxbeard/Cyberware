package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.CyberwareAPI;

public class EnableDisableHelper
{
	public static final String ENABLED_STR = "~enabled";
	
	public static boolean isEnabled(ItemStack stack)
	{
		if (stack == null) return false;

		NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
		if (!comp.hasKey(ENABLED_STR))
		{
			return true;
		}
		
		return comp.getBoolean(ENABLED_STR);
	}
	
	public static void toggle(ItemStack stack)
	{
		if (isEnabled(stack))
		{
			NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
			comp.setBoolean(ENABLED_STR, false);
		}
		else
		{
			NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
			comp.removeTag(ENABLED_STR);
		}
	}
	
	public static String getUnlocalizedLabel(ItemStack stack)
	{
		if (isEnabled(stack))
		{
			return "cyberware.gui.active.disable";
		}
		else
		{
			return "cyberware.gui.active.enable";
		}
	}
}
