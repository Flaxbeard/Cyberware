package flaxbeard.cyberware.client.gui;

import javax.annotation.Nullable;

import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.client.gui.ContainerSurgery.SlotSurgery;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.lib.LibConstants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEngineeringTable extends Container
{
	public class SlotEngineering extends SlotItemHandler
	{

		public SlotEngineering(IItemHandler itemHandler, int index, int xPosition, int yPosition)
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
			engineering.markDirty();
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
		{
			engineering.markDirty();
		}
		
		@Override
		public void putStack(@Nullable ItemStack stack)
		{
			engineering.slots.overrideExtract = true;
			super.putStack(stack);
			engineering.slots.overrideExtract = false;
			engineering.markDirty();
		}
		
		@Override
		public boolean isItemValid(@Nullable ItemStack stack)
		{
			return engineering.slots.isItemValidForSlot(this.slotNumber, stack);
		}
	}
	
	private final TileEntityEngineeringTable engineering;
	
	
	public ContainerEngineeringTable(InventoryPlayer playerInventory, TileEntityEngineeringTable surgery)
	{
		this.engineering = surgery;
		
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 0, 15, 20));
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 1, 15, 53));
		
		for (int w = 0; w < 2; w++)
		{
			for (int h = 0; h < 3; h++)
			{
				this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 2 + h * 2 + w, 71 + w * 18, 17 + h * 18));
			}
		}
		
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 8, 115, 53));
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 9, 145, 21));


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
		return engineering.isUseableByPlayer(playerIn);
	}
	
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
	
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
	
			if (!(slot instanceof SlotEngineering))
			{
			
				
				if (index >= 3 && index < 30)
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
