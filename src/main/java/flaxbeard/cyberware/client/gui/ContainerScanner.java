package flaxbeard.cyberware.client.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.block.tile.TileEntityScanner;

public class ContainerScanner extends Container
{
	public class SlotScanner extends SlotItemHandler
	{
		public SlotScanner(IItemHandler itemHandler, int index, int xPosition, int yPosition)
		{
			super(itemHandler, index, xPosition, yPosition);
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn)
		{
			return true;
		}
		

		@Override
		public void onSlotChanged()
		{
			scanner.markDirty();
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
		{
			scanner.markDirty();
		}
		
		@Override
		public void putStack(@Nullable ItemStack stack)
		{
			scanner.slots.overrideExtract = true;
			super.putStack(stack);
			scanner.slots.overrideExtract = false;
			scanner.markDirty();
		}
		
		@Override
		public boolean isItemValid(@Nullable ItemStack stack)
		{
			return scanner.slots.isItemValidForSlot(this.slotNumber, stack);
		}
	}
	
	private final TileEntityScanner scanner;
	
	
	public ContainerScanner(InventoryPlayer playerInventory, TileEntityScanner scanner)
	{
		this.scanner = scanner;
		
		this.addSlotToContainer(new SlotScanner(scanner.guiSlots, 0, 35, 53));
		this.addSlotToContainer(new SlotScanner(scanner.guiSlots, 1, 15, 53));

		
		this.addSlotToContainer(new SlotScanner(scanner.guiSlots, 2, 141, 57));


		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return scanner.isUseableByPlayer(playerIn);
	}
	
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		boolean doUpdate = false;
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 2)
			{
				if (!this.mergeItemStack(itemstack1, 3, 39, true))
				{
					return null;
				}

				//slot.onSlotChange(itemstack1, itemstack);
				//engineering.updateRecipe();
			}
			else if (index > 2)
			{
				if (scanner.slots.isItemValidForSlot(1, itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 1, 2, false))
					{
						return null;
					}
				}
				else if (scanner.slots.isItemValidForSlot(0, itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 0, 1, false))
					{
						return null;
					}
				}
				else if (index >= 3 && index < 30)
				{
					if (!this.mergeItemStack(itemstack1, 30, 39, false))
					{
						return null;
					}
				}
				else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 3, 39, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}
		
		return itemstack;
	}
}
