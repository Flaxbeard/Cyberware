package flaxbeard.cyberware.client.gui;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityBlueprintArchive;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox.ItemStackHandlerComponent;
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
			super.onSlotChanged();
			engineering.markDirty();
			
			if (this.slotNumber >= 2 && this.slotNumber <= 8)
			{
				engineering.updateRecipe();
			}
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
		{
			super.onPickupFromSlot(playerIn, stack);
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
	
	public class SlotInv extends Slot
	{
		public SlotInv(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public void onSlotChanged()
		{
			super.onSlotChanged();
			seeIfCheckNewBox();
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
		{
			super.onPickupFromSlot(playerIn, stack);
			seeIfCheckNewBox();
		}
		
		@Override
		public void putStack(ItemStack stack)
		{
			super.putStack(stack);
			seeIfCheckNewBox();
		}
		
		private void seeIfCheckNewBox()
		{
			if (componentBoxList.size() <= 1)
			{
				checkForNewBoxes();
			}
		}
		
	}
	
	public class SlotComponentBox extends SlotItemHandler
	{

		public SlotComponentBox(IItemHandler itemHandler, int index, int xPosition, int yPosition)
		{
			super(itemHandler, index, xPosition, yPosition);
		}
		
		@Override
		public void onSlotChanged()
		{
			super.onSlotChanged();
			updateNBT();
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
		{
			super.onPickupFromSlot(playerIn, stack);
			updateNBT();
		}
		
		@Override
		public void putStack(ItemStack stack)
		{
			super.putStack(stack);
			updateNBT();
		}
		
		private void updateNBT()
		{
			if (componentBox != null && componentBox instanceof Integer)
			{
				ItemStack item = playerInv.mainInventory[(Integer) componentBox];
				if (item != null && item.getItem() == CyberwareContent.componentBox.ib)
				{
					NBTTagCompound comp = componentHandler.serializeNBT();
					if (!item.hasTagCompound())
					{
						item.setTagCompound(new NBTTagCompound());
					}
					item.getTagCompound().setTag("contents", comp);
				}
			}
		}
		
	}
	
	private final TileEntityEngineeringTable engineering;
	public TileEntityBlueprintArchive archive;
	public int archiveIndex = 0;
	public ArrayList<TileEntityBlueprintArchive> archiveList = new ArrayList<TileEntityBlueprintArchive>();
	
	public Object componentBox;
	public ArrayList<Object> componentBoxList = new ArrayList<Object>();
	public int componentBoxIndex = 0;
	
	ItemStackHandler componentHandler = null;
	public InventoryPlayer playerInv;
	
	public ContainerEngineeringTable(String uuid, InventoryPlayer playerInventory, TileEntityEngineeringTable engineering)
	{
		this.playerInv = playerInventory;
		this.engineering = engineering;
		archive = null;
		componentBox = null;
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
							archiveIndex = archiveList.size();
						}

						archiveList.add((TileEntityBlueprintArchive) te);
					}
				}
			}
		}
		
		for (int i = 0; i < playerInventory.mainInventory.length; i++)
		{
			ItemStack stack = playerInventory.mainInventory[i];
			if (stack != null && stack.getItem() == CyberwareContent.componentBox.ib)
			{
				if (!stack.hasTagCompound())
				{
					stack.setTagCompound(new NBTTagCompound());
				}
				if (!stack.getTagCompound().hasKey("contents"))
				{
					ItemStackHandler slots = new ItemStackHandlerComponent(18);
					stack.getTagCompound().setTag("contents", slots.serializeNBT());
				}
				if (componentBox == null )
				{
					componentBox = i;
					componentBoxIndex = componentBoxList.size();
				}

				componentBoxList.add(i);
			}
		}
		
		for (int y = -2; y < 2; y++)
		{
			for (int x = -2; x < 3; x++)
			{
				for (int z = -2; z < 3; z++)
				{
					BlockPos pos = engineering.getPos().add(x, y, z);
					TileEntity te = engineering.getWorld().getTileEntity(pos);
					if (te != null && te instanceof TileEntityComponentBox)
					{
						if (componentBox == null )
						{
							componentBox = (TileEntityComponentBox) te;
							componentBoxIndex = componentBoxList.size();
						}

						componentBoxList.add((TileEntityComponentBox) te);
					}
				}
			}
		}
		
		int offset = componentBox == null ? 0 : 65;
		
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 0, 15 + offset, 20));
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 1, 15 + offset, 53));
		
		for (int w = 0; w < 2; w++)
		{
			for (int h = 0; h < 3; h++)
			{
				this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 2 + h * 2 + w, 71 + w * 18 + offset, 17 + h * 18));
			}
		}
		
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 8, 115 + offset, 53));
		this.addSlotToContainer(new SlotEngineering(engineering.guiSlots, 9, 145 + offset, 21));


		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new SlotInv(playerInventory, j + i * 9 + 9, 8 + j * 18 + offset, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlotToContainer(new SlotInv(playerInventory, k, 8 + k * 18 + offset, 142));
		}
		
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotItemHandler(archive.slots, k + j * numRows, 181 + k * 18 + offset, 22 + j * 18));
				}
			}
		}
		if (componentBox != null)
		{
			if (componentBox instanceof TileEntityComponentBox)
			{
				componentHandler = ((TileEntityComponentBox) componentBox).slots;
			}
			else
			{
				ItemStackHandler slots = new ItemStackHandlerComponent(18);
				ItemStack item = playerInv.mainInventory[(Integer) componentBox];
				slots.deserializeNBT(item.getTagCompound().getCompoundTag("contents"));
				componentHandler = slots;
			}
			int numRows = componentHandler.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotComponentBox(componentHandler, k + j * numRows, 8 + k * 18, 22 + j * 18));
				}
			}
		}
		
		engineering.updateRecipe();
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		while (archive != null && archive.getWorld().getTileEntity(archive.getPos()) != archive)
		{
			archiveList.remove(this.archiveIndex);
			if (archiveList.size() == 0)
			{
				int offset = (this.componentBox == null ? 0 : 18);
				int numRows = 18 / 6;
				for (int j = 0; j < 6; ++j)
				{
					for (int k = 0; k < numRows; ++k)
					{
						this.inventorySlots.remove(this.inventorySlots.size() - 1 - offset);
						this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1 - offset);

					}
				}
				archive = null;
			}
			else
			{
				archiveIndex = archiveIndex - 1;
				this.nextArchive();
			}
		}
		
		while (componentBox != null
				&& ((componentBox instanceof TileEntityComponentBox && ((TileEntityComponentBox) componentBox).getWorld().getTileEntity(((TileEntityComponentBox) componentBox).getPos()) != componentBox))
				|| (componentBox instanceof Integer && (playerInv.mainInventory[(Integer) componentBox] == null || playerInv.mainInventory[(Integer) componentBox].getItem() != CyberwareContent.componentBox.ib)))
		{
			componentBoxList.remove(this.componentBoxIndex);
			if (componentBoxList.size() == 0)
			{
				int numRows = 18 / 6;
				for (int j = 0; j < 6; ++j)
				{
					for (int k = 0; k < numRows; ++k)
					{
						this.inventorySlots.remove(this.inventorySlots.size() - 1);
						this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1);

					}
				}
				componentBox = null;
				this.componentHandler = null;
			}
			else
			{
				componentBoxIndex = componentBoxIndex - 1;
				this.nextComponentBox();
			}
		}
		return playerIn == null ? false : engineering.isUseableByPlayer(playerIn);
	}


	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		boolean doUpdate = false;
		
		int componentLow = (this.archive == null ? 46 : 64);
		int componentHigh = componentLow + 18;
		int archiveLow = 46;
		int archiveHigh = archiveLow + 18;
		
		
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
				if (!this.mergeItemStack(itemstack1, archiveLow, archiveHigh, true) && !this.mergeItemStack(itemstack1, 10, 46, false))
				{
					return null;
				}
			}
			else if (index == 1 && archive != null)
			{
				if (!this.mergeItemStack(itemstack1, archiveLow, archiveHigh, true) && !this.mergeItemStack(itemstack1, 10, 46, false))
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
					if ((archive == null || !this.mergeItemStack(itemstack1, archiveLow, archiveHigh, false)) && !this.mergeItemStack(itemstack1, 2, 8, false)  && (componentBox == null || !this.mergeItemStack(itemstack1, componentLow, componentHigh, false))  && !this.mergeItemStack(itemstack1, 37, 46, false))
					{
						return null;
					}
				}
				else if (index >= archiveLow && index < archiveHigh)
				{
					if (!this.mergeItemStack(itemstack1, 10, 37, false) && !this.mergeItemStack(itemstack1, 37, 46, false))
					{
						return null;
					}
				}
				else if (index >= componentLow && index < componentHigh)
				{
					
					if (!this.mergeItemStack(itemstack1, 2, 8, false) && !this.mergeItemStack(itemstack1, 10, 37, false) && !this.mergeItemStack(itemstack1, 37, 46, false))
					{
						return null;
					}
				}
				else if (index >= 37 && index < 46 && !this.mergeItemStack(itemstack1, 2, 8, false) && !this.mergeItemStack(itemstack1, 10, 37, false) )
				{
					return null;
				}
			}
			else if ((componentBox == null || !this.mergeItemStack(itemstack1, componentLow, componentHigh, false)) && (archive == null || !this.mergeItemStack(itemstack1, archiveLow, archiveHigh, false)) && !this.mergeItemStack(itemstack1, 10, 46, false))
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
	

	public void checkForNewBoxes()
	{
		for (int i = 0; i < playerInv.mainInventory.length; i++)
		{
			if (!this.componentBoxList.contains(i))
			{
				ItemStack stack = playerInv.mainInventory[i];
				if (stack != null && stack.getItem() == CyberwareContent.componentBox.ib)
				{
					if (!stack.hasTagCompound())
					{
						stack.setTagCompound(new NBTTagCompound());
					}
					if (!stack.getTagCompound().hasKey("contents"))
					{
						ItemStackHandler slots = new ItemStackHandlerComponent(18);
						stack.getTagCompound().setTag("contents", slots.serializeNBT());
					}
					if (componentBox == null )
					{
						componentBox = i;
						componentBoxIndex = componentBoxList.size();
					}
	
					componentBoxList.add(i);
				}
			}
		}
	}


	public void nextArchive()
	{
		clearSlots();
		archiveIndex = (archiveIndex + 1) % archiveList.size();
		archive = archiveList.get(archiveIndex);
		createSlots();
	}

	public void prevArchive()
	{
		clearSlots();
		archiveIndex = (archiveIndex + archiveList.size() - 1) % archiveList.size();
		archive = archiveList.get(archiveIndex);
		createSlots();
	}
	
	
	public void nextComponentBox()
	{
		checkForNewBoxes();
		clearSlots();
		handleClosingBox();
		componentBoxIndex = (componentBoxIndex + 1) % componentBoxList.size();
		componentBox = componentBoxList.get(componentBoxIndex);
		createSlots();
		
	}
	
	public void prevComponentBox()
	{
		checkForNewBoxes();
		clearSlots();
		handleClosingBox();
		componentBoxIndex = (componentBoxIndex + componentBoxList.size() - 1) % componentBoxList.size();
		componentBox = componentBoxList.get(componentBoxIndex);
		createSlots();
	}
	
	public void clearSlots()
	{
		int numOccupied = 0;
		if (componentBox != null) numOccupied += 18;
		if (archive != null) numOccupied += 18;
		for (int k = 0; k < numOccupied; ++k)
		{
			this.inventorySlots.remove(this.inventorySlots.size() - 1);
			this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1);

		}
	}
	
	public void createSlots()
	{
		canInteractWith(null);
		if (archive != null)
		{
			int numRows = archive.slots.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotItemHandler(archive.slots, k + j * numRows, 181 + k * 18 + (componentBox == null ? 0 : 65), 22 + j * 18));
				}
			}
		}
		if (componentBox != null)
		{
			if (componentBox instanceof TileEntityComponentBox)
			{
				componentHandler = ((TileEntityComponentBox) componentBox).slots;
			}
			else
			{
				ItemStackHandler slots = new ItemStackHandlerComponent(18);
				ItemStack item = playerInv.mainInventory[(Integer) componentBox];
				slots.deserializeNBT(item.getTagCompound().getCompoundTag("contents"));
				componentHandler = slots;
			}
			
			int numRows = componentHandler.getSlots() / 6;
			for (int j = 0; j < 6; ++j)
			{
				for (int k = 0; k < numRows; ++k)
				{
					this.addSlotToContainer(new SlotComponentBox(componentHandler, k + j * numRows, 8 + k * 18, 22 + j * 18));
				}
			}
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		handleClosingBox();
	}
	
	public void handleClosingBox()
	{

	}
}
