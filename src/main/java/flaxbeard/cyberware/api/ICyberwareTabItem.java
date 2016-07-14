package flaxbeard.cyberware.api;

import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.ICyberwareTabItem.EnumCategory;

public interface ICyberwareTabItem
{	
	public static enum EnumCategory
	{
		BLOCKS(),
		BODYPARTS(),
		EYES(),
		CRANIUM(),
		HEART(),
		LUNGS();
		
		private EnumCategory() {}
	}

	public EnumCategory getCategory(ItemStack stack);
}
