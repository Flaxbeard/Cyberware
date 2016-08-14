package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;

public class HotkeyHelper
{
	public static void assignHotkey(ICyberwareUserData data, ItemStack stack, int key)
	{
		removeHotkey(data, stack);
		
		data.addHotkey(key, stack);
		CyberwareAPI.getCyberwareNBT(stack).setInteger("hotkey", key);
	}
	
	public static void removeHotkey(ICyberwareUserData data, int key)
	{
		ItemStack stack = data.getHotkey(key);
		removeHotkey(data, stack);
	}
	
	public static void removeHotkey(ICyberwareUserData data, ItemStack stack)
	{
		int hotkey = getHotkey(stack);
		
		if (hotkey != -1)
		{
			data.removeHotkey(hotkey);
			CyberwareAPI.getCyberwareNBT(stack).removeTag("hotkey");
		}
	}
	
	public static int getHotkey(ItemStack stack)
	{
		if (stack == null) return -1;
		
		NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
		if (!comp.hasKey("hotkey"))
		{
			return -1;
		}
		
		return comp.getInteger("hotkey");
	}
}
