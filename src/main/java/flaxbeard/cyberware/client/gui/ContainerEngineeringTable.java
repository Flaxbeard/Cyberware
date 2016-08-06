package flaxbeard.cyberware.client.gui;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import flaxbeard.cyberware.common.block.tile.TileEntityBlueprintArchive;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;

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
			
			if (this.slotNumber >= 2 && this.slotNumber <= 8)
			{
				engineering.updateRecipe();
			}
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
			return (this.slotNumber >= 2 && this.slotNumber < 8) || engineering.slots.isItemValidForSlot(this.slotNumber, stack);
		}
	}
	
	private final TileEntityEngineeringTable engineering;
	public TileEntityBlueprintArchive archive;
	public int index = 0;
	public ArrayList<TileEntityBlueprintArchive> list = new ArrayList<TileEntityBlueprintArchive>();
	
	public ContainerEngineeringTable(String uuid, InventoryPlayer playerInventory, TileEntityEngineeringTable engineering)
	{
		this.engineering = engineering;
		archive = null;
		BlockPos target = null;
		if (engineering.lastPlayerArchive.containsKey(uuid))
		{
			target = engineering.lastPlayerArchive.get(uuid);
		}
		for (int y = -2; y < 2; y++)
		{
			for (int x = -2; x < 3; x++)
			{
				for (int z = -2; z < 3; z++)
				{
					BlockPos pos = engineering.getPos().add(x, y, z);
					TileEntity te = engineering.getWorld().getTileEntity(pos);
					if (te != null && te instanceof TileEntityBlueprintArchive)
					{
						if (archive == null || te.getPos().equals(target))
						{
							archive = (TileEntityBlueprintArchive) te;
							index = list.size();
						}

						list.add((TileEntityBlueprintArchive) te);
					}
				}
			}
		}
		
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
		
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotItemHandler(archive.slots, k + j * numRows, 181 + k * 18, 22 + j * 18));
				}
			}
		}
		
		engineering.updateRecipe();
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return engineering.isUseableByPlayer(playerIn) && (archive == null || archive.getWorld().getTileEntity(archive.getPos()) == archive);
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

			if (index == 9)
			{
				if (!this.mergeItemStack(itemstack1, 10, 46, true))
				{
					return null;
				}

				engineering.subtractResources();
				doUpdate = true;
				//slot.onSlotChange(itemstack1, itemstack);
				//engineering.updateRecipe();
			}
			else if (index == 8 && archive != null)
			{
				if (!this.mergeItemStack(itemstack1, 46, 64, true) && !this.mergeItemStack(itemstack1, 10, 46, false))
				{
					return null;
				}
			}
			else if (index == 1 && archive != null)
			{
				if (!this.mergeItemStack(itemstack1, 46, 64, true) && !this.mergeItemStack(itemstack1, 10, 46, false))
				{
					return null;
				}
			}
			else if (index > 9)
			{
				if (engineering.slots.isItemValidForSlot(1, itemstack1) && this.mergeItemStack(itemstack1, 1, 2, false))
				{

				}
				else if (engineering.slots.isItemValidForSlot(0, itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 0, 1, false))
					{
						return null;
					}
				}
				else if (engineering.slots.isItemValidForSlot(8, itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 8, 9, false))
					{
						return null;
					}
				}
				else if (index >= 10 && index < 37)
				{
					if ((archive == null || !this.mergeItemStack(itemstack1, 46, 64, false)) && !this.mergeItemStack(itemstack1, 2, 8, false) && !this.mergeItemStack(itemstack1, 37, 46, false))
					{
						return null;
					}
				}
				else if (index >= 46 && index < 64)
				{
					if (!this.mergeItemStack(itemstack1, 10, 37, false) && !this.mergeItemStack(itemstack1, 37, 46, false))
					{
						return null;
					}
				}
				else if (index >= 37 && index < 46 && !this.mergeItemStack(itemstack1, 2, 8, false) && !this.mergeItemStack(itemstack1, 10, 37, false) )
				{
					return null;
				}
			}
			else if ((archive == null || !this.mergeItemStack(itemstack1, 46, 64, false)) && !this.mergeItemStack(itemstack1, 10, 46, false))
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
		
		if (doUpdate)
		{
			engineering.updateRecipe();
		}

		return itemstack;
	}

	public void nextArchive()
	{
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.inventorySlots.remove(this.inventorySlots.size() - 1);
					this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1);

				}
			}
		}
		index = (index + 1) % list.size();
		archive = list.get(index);
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotItemHandler(archive.slots, k + j * numRows, 181 + k * 18, 22 + j * 18));
				}
			}
		}
	}

	public void prevArchive()
	{
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.inventorySlots.remove(this.inventorySlots.size() - 1);
					this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1);

				}
			}
		}
		index = (index + list.size() - 1) % list.size();
		archive = list.get(index);
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotItemHandler(archive.slots, k + j * numRows, 181 + k * 18, 22 + j * 18));
				}
			}
		}
	}
}
