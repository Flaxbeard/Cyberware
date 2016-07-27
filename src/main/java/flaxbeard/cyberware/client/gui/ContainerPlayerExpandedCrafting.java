package flaxbeard.cyberware.client.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class ContainerPlayerExpandedCrafting extends ContainerPlayer
{

	public ContainerPlayerExpandedCrafting(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player)
	{
		super(playerInventory, localWorld, player);
		craftMatrix = new InventoryCrafting(this, 3, 3);

		/*this.inventorySlots.remove(1);
		this.inventorySlots.remove(1);
		this.inventorySlots.remove(1);
		this.inventorySlots.remove(1);*/
		
		this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28));

		
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 3; ++j)
			{
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 98 + j * 18, 18 + i * 18));
			}
		}
		
		for (int i = 0; i < 5; i++)
		{
			this.inventorySlots.get(i).xDisplayPosition = -999;
			this.inventorySlots.get(i).yDisplayPosition = -999;

		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);

		for (int i = 0; i < 9; ++i)
		{
			ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

			if (itemstack != null)
			{
				playerIn.dropItem(itemstack, false);
			}
		}

		this.craftResult.setInventorySlotContents(0, (ItemStack)null);
	}
	


}
