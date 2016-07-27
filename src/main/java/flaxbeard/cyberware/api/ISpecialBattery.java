package flaxbeard.cyberware.api;

import net.minecraft.item.ItemStack;

public interface ISpecialBattery
{
	public int add(ItemStack battery, ItemStack power, int amount, boolean simulate);
	
	public int extract(ItemStack battery, int amount, boolean simulate);
	
	public int getStoredEnergy(ItemStack battery);
	
	public int getCapacity(ItemStack battery);

}
