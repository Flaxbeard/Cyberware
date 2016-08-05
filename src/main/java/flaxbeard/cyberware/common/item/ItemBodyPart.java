package flaxbeard.cyberware.common.item;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;

public class ItemBodyPart extends ItemCyberware implements ISidedLimb
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
	public int getEssenceCost(ItemStack stack)
	{
		return 0;
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		if (stack.getItemDamage() <= 7)
		{
			return CyberwareAPI.getCyberware(other).isEssential(other);
		}
		
		ICyberware ware = CyberwareAPI.getCyberware(other);
		
		if (ware instanceof ISidedLimb)
		{
			return ware.isEssential(other) && ((ISidedLimb) ware).getSide(other) == this.getSide(stack);
		}
		return false;
	}
	
	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.BODYPARTS;
	}

	@Override
	public EnumSide getSide(ItemStack stack)
	{
		return stack.getItemDamage() % 2 == 0 ? EnumSide.LEFT : EnumSide.RIGHT;
	}
	
	@Override
	public Quality getQuality(ItemStack stack)
	{
		return null;
	}
	
	@Override
	public boolean canHoldQuality(ItemStack stack, Quality quality)
	{
		return false;
	}
}
