package flaxbeard.cyberware.client.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox.ItemStackHandlerComponent;

public class ContainerComponentBox extends Container
{
	private ItemStackHandler slots;
	private int numRows;
	private final TileEntityComponentBox box;
	private final ItemStack item;

	public ContainerComponentBox(IInventory playerInventory, TileEntityComponentBox box)
	{
		this.box = box;
		this.item = null;
		this.slots = box.slots;
		this.numRows = slots.getSlots() / 9;
		int i = (this.numRows - 4) * 18;

		for (int j = 0; j < this.numRows; ++j)
		{
			for (int k = 0; k < 9; ++k)
			{
				this.addSlotToContainer(new SlotItemHandler(slots, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (int l = 0; l < 3; ++l)
		{
			for (int j1 = 0; j1 < 9; ++j1)
			{
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1)
		{
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}
	}

	public ContainerComponentBox(IInventory playerInventory, ItemStack itemStack)
	{
		this.box = null;
		this.item = itemStack;
		slots = new ItemStackHandlerComponent(18);

		if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("contents"))
		{
			slots.deserializeNBT(itemStack.getTagCompound().getCompoundTag("contents"));
		}
		
		this.numRows = slots.getSlots() / 9;
		int i = (this.numRows - 4) * 18;

		for (int j = 0; j < this.numRows; ++j)
		{
			for (int k = 0; k < 9; ++k)
			{
				this.addSlotToContainer(new SlotItemHandler(slots, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (int l = 0; l < 3; ++l)
		{
			for (int j1 = 0; j1 < 9; ++j1)
			{
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1)
		{
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return box == null ? playerIn.inventory.mainInventory[playerIn.inventory.currentItem] == item : this.box.isUseableByPlayer(playerIn);
	}

	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		
		if (item != null)
		{
			NBTTagCompound comp = slots.serializeNBT();
			if (!item.hasTagCompound())
			{
				item.setTagCompound(new NBTTagCompound());
			}
			item.getTagCompound().setTag("contents", comp);
		}
	}
	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.numRows * 9)
			{
				if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
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
		}

		return itemstack;
	}

}