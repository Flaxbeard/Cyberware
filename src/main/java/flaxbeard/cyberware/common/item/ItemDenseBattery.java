package flaxbeard.cyberware.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.ISpecialBattery;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemDenseBattery extends ItemCyberware implements ISpecialBattery
{

	public ItemDenseBattery(String name, EnumSlot slot)
	{
		super(name, slot);

	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == CyberwareContent.lowerOrgansUpgrades && (stack.getItemDamage() == 2);
	}

	@Override
	public int add(ItemStack battery, ItemStack power, int amount, boolean simulate)
	{
		if (power == null)
		{
			int amountToAdd = Math.min(getCapacity(battery) - getStoredEnergy(battery), amount);
			if (!simulate)
			{
				battery.getTagCompound().setInteger("power", battery.getTagCompound().getInteger("power") + amountToAdd);
			}
			return amountToAdd;
		}
		return 0;
	}

	@Override
	public int extract(ItemStack battery, int amount, boolean simulate)
	{
		int amountToSub = Math.min(getStoredEnergy(battery), amount);
		if (!simulate)
		{
			battery.getTagCompound().setInteger("power", battery.getTagCompound().getInteger("power") - amountToSub);
		}
		return amountToSub;
	}

	@Override
	public int getStoredEnergy(ItemStack battery)
	{
		if (!battery.hasTagCompound())
		{
			battery.setTagCompound(new NBTTagCompound());
		}
		if (!battery.getTagCompound().hasKey("power"))
		{
			battery.getTagCompound().setInteger("power", 0);
		}
		return battery.getTagCompound().getInteger("power");
	}
	
	@Override
	public int getCapacity(ItemStack battery)
	{
		return LibConstants.DENSE_BATTERY_CAPACITY;
	}

}
