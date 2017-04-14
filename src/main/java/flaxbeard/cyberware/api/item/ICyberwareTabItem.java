package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.item.ICyberwareTabItem.EnumCategory;

public interface ICyberwareTabItem
{	
	public static enum EnumCategory
	{
		BLOCKS,
		BODYPARTS,
		MISCAUGS,
		EYES,
		CRANIUM,
		HEART,
		LUNGS,
		LOWER_ORGANS,
		SKIN,
		MUSCLE,
		BONE,
		ARM,
		HAND,
		LEG,
		FOOT;
	}

	public EnumCategory getCategory(ItemStack stack);
}
