package flaxbeard.cyberware.api.progression;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class ItemPool
{
	private static List<ItemStack> allItems = new ArrayList<ItemStack>();
	private static List<ItemPoolItem> zombieItems = new ArrayList<ItemPoolItem>();
	private static List<ItemPoolItem> containerItems = new ArrayList<ItemPoolItem>();

	public static class ItemPoolItem extends WeightedRandom.Item
	{
		public ItemStack stack;
		public ItemPoolItem(int weight, ItemStack stack)
		{
			super(weight);
			this.stack = stack;
		}

		@Override
		public boolean equals(Object target)
		{
			if (!(target instanceof ItemPoolItem)) return false;
			ItemStack stack2 = ((ItemPoolItem)target).stack;
			return (stack == stack2 || (stack != null && stack2 != null && stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage() && stack.stackSize == stack2.stackSize));
		}
	}
	
	public static void addCyberware(int weight, ItemStack ware, boolean addToZombiePool, boolean addToContainerPool)
	{
		ItemPoolItem item = new ItemPoolItem(weight, ware);
		allItems.add(ware);
		if (addToZombiePool)
		{
			zombieItems.add(item);
		}
		if (addToContainerPool)
		{
			containerItems.add(item);
		}
	}
	
	public static List<ItemPoolItem> getZombieItems()
	{
		return zombieItems;
	}
	
	public static List<ItemStack> getAllItems()
	{
		return allItems;
	}
	
	public static ItemStack generateRandomContainerItemForPlayer(EntityPlayer player)
	{
		List<ItemPoolItem> items = new ArrayList<ItemPoolItem>();
		ICyberwareProgressionData progCap = ProgressionHelper.getCapability(player);
		for (ItemPoolItem item : containerItems)
		{
			if (!progCap.hasSeen(item.stack))
			{
				items.add(item);
			}
		}
		
		if (items.size() < 1)
		{
			return ItemStack.copyItemStack(((ItemPoolItem) WeightedRandom.getRandomItem(player.worldObj.rand, containerItems)).stack);
		}
		else
		{
			return ItemStack.copyItemStack(((ItemPoolItem) WeightedRandom.getRandomItem(player.worldObj.rand, items)).stack);
		}
	}
}
