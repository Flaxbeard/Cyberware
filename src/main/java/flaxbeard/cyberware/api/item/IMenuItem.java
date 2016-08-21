package flaxbeard.cyberware.api.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IMenuItem
{
	public boolean hasMenu(ItemStack stack);
	public void use(Entity e, ItemStack stack);
	public String getUnlocalizedLabel(ItemStack stack);
	public float[] getColor(ItemStack stack);
}
