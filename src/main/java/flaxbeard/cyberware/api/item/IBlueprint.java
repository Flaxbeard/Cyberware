package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;

public interface IBlueprint
{
	public ItemStack getResult(ItemStack[] items);
}
