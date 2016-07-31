package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;

public interface IDeconstructable
{
	public boolean canDestroy(ItemStack stack);
	public ItemStack[] getComponents(ItemStack stack);
}
