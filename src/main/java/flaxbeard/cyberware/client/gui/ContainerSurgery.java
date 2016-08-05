package flaxbeard.cyberware.client.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ContainerSurgery extends Container
{
	public class SlotSurgery extends SlotItemHandler
	{
		public final int savedXPosition;
		public final int savedYPosition;
		public final EnumSlot slot;
		private final int index;
		private IItemHandler playerItems;

		public SlotSurgery(IItemHandler itemHandler, IItemHandler playerItems, int index, int xPosition, int yPosition, EnumSlot slot)
		{
			super(itemHandler, index, xPosition, yPosition);
			
			savedXPosition = xPosition;
			savedYPosition = yPosition;
			this.slot = slot;
			this.index = index;
			this.playerItems = playerItems;
		}
		
		public ItemStack getPlayerStack()
		{
			return playerItems.getStackInSlot(slotNumber);
		}
		
		public boolean slotDiscarded()
		{
			return surgery.discardSlots[slotNumber];
		}
		
		public void setDiscarded(boolean dis)
		{
			surgery.discardSlots[slotNumber] = dis;
			surgery.updateEssential(slot);
			surgery.updateEssence();
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn)
		{
			return surgery.canDisableItem(getStack(), slot, index % LibConstants.WARE_PER_SLOT);
		}
		
		@Override
		public void onSlotChanged()
		{
			surgery.updateEssence();
			surgery.markDirty();
		}
		
		@Override
		public void putStack(ItemStack stack)
		{
			if (isItemValid(stack))
			{
				surgery.disableDependants(getPlayerStack(), slot, index % LibConstants.WARE_PER_SLOT);
				super.putStack(stack);
			}
			surgery.markDirty();
			surgery.updateEssential(slot);
			surgery.updateEssence();
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
	    {
			super.onPickupFromSlot(playerIn, stack);
			surgery.markDirty();
			surgery.updateEssential(slot);
			surgery.updateEssence();
	    }
		
		@Override
		public boolean isItemValid(@Nullable ItemStack stack)
		{
			//System.out.println(surgery.canDisableItem(getPlayerStack(), slot, index % LibConstants.WARE_PER_SLOT));
			ItemStack playerStack = getPlayerStack();
			//if (stack != null && playerStack != null && stack.getit) return false;
			if (getPlayerStack() != null && !surgery.canDisableItem(playerStack, slot, index % LibConstants.WARE_PER_SLOT)) return false;
			if (!(stack != null && stack.getItem() != null && CyberwareAPI.isCyberware(stack) && CyberwareAPI.getCyberware(stack).getSlot(stack) == this.slot)) return false;
			
			if (CyberwareAPI.areCyberwareStacksEqual(stack, playerStack))
			{
				int stackSize = CyberwareAPI.getCyberware(stack).installedStackSize(stack);
				if (playerStack.stackSize == stackSize) return false;
			}
			
			
			return !doesItemConflict(stack) && areRequirementsFulfilled(stack);
		}
		
		public boolean doesItemConflict(@Nullable ItemStack stack)
		{
			return surgery.doesItemConflict(stack, slot, index % LibConstants.WARE_PER_SLOT);
		}
		
		public boolean areRequirementsFulfilled(@Nullable ItemStack stack)
		{
			return surgery.areRequirementsFulfilled(stack, slot, index % LibConstants.WARE_PER_SLOT);
		}
		
		@Override
		public int getItemStackLimit(ItemStack stack)
		{
			if (stack == null || !(CyberwareAPI.isCyberware(stack)))
			{
				return 1;
			}
			ItemStack playerStack = getPlayerStack();
			int stackSize = CyberwareAPI.getCyberware(stack).installedStackSize(stack);
			if (CyberwareAPI.areCyberwareStacksEqual(playerStack, stack))
			{
				return stackSize - playerStack.stackSize;
			}
			return stackSize;
		}
	}
	
	private final TileEntitySurgery surgery;
	public GuiSurgery gui;
	
	public ContainerSurgery(InventoryPlayer playerInventory, TileEntitySurgery surgery)
	{
		this.surgery = surgery;
		
		int row = 0;
		int c = 0;
		for (EnumSlot slot : EnumSlot.values())
		{
			for (int n = 0; n < 8; n++)
			{
				this.addSlotToContainer(new SlotSurgery(surgery.slots, surgery.slotsPlayer, c, 9 + 20 * n, 109, slot));
				c++;
			}
			for (int n = 0; n < LibConstants.WARE_PER_SLOT - 8; n++)
			{
				this.addSlotToContainer(new SlotSurgery(surgery.slots, surgery.slotsPlayer, c, Integer.MIN_VALUE, Integer.MIN_VALUE, slot));
				c++;
			}
			row++;
		}

		for (int l = 0; l < 3; ++l)
		{
			for (int j1 = 0; j1 < 9; ++j1)
			{
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + 37));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1)
		{
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + 37));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return surgery.isUseableByPlayer(playerIn);
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
	
			if (!(slot instanceof SlotSurgery))
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
