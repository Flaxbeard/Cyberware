package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ICyberware
{
	public EnumSlot getSlot(ItemStack stack);
	public int installedStackSize(ItemStack stack);
	public ItemStack[] required(ItemStack stack);
	public boolean isIncompatible(ItemStack stack, ItemStack comparison);
	boolean isEssential(ItemStack stack);
	public List<String> getInfo(ItemStack stack);

	public enum EnumSlot
	{
		EYES(12, "eyes"),
		CRANIUM(11, "cranium"),
		HEART(14, "heart"),
		LUNGS(15, "lungs");

		private final int slotNumber;
		private final String name;
		
		private EnumSlot(int slot, String name)
		{
			this.slotNumber = slot;
			this.name = name;
		}
		
		public int getSlotNumber()
		{
			return slotNumber;
		}
		
		public static EnumSlot getSlotByPage(int page)
		{
			for (EnumSlot slot : values())
			{
				if (slot.getSlotNumber() == page)
				{
					return slot;
				}
			}
			return null;
		}

		public String getName()
		{
			return name;
		}
	}
}
