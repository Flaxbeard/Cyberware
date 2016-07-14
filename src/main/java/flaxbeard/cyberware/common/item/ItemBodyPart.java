package flaxbeard.cyberware.common.item;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareTabItem.EnumCategory;

public class ItemBodyPart extends ItemCyberware
{
	
	public ItemBodyPart(String name, EnumSlot[] slots, String[] subnames)
	{
		super(name, slots, subnames);
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
	
	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.BODYPARTS;
	}
	
}
