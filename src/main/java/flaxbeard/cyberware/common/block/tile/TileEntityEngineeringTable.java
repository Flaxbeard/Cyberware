package flaxbeard.cyberware.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.oredict.OreDictionary;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IBlueprint;
import flaxbeard.cyberware.common.misc.SpecificWrapper;

public class TileEntityEngineeringTable extends TileEntity
{
	public class ItemStackHandlerEngineering extends ItemStackHandler
	{
		public boolean overrideExtract = false;
		
		public ItemStackHandlerEngineering(int i)
		{
			super(i);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (!isItemValidForSlot(slot, stack)) return stack;
			
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if (!canRemoveItem(slot)) return null;
			
			return super.extractItem(slot, amount, simulate);
		}

		public boolean canRemoveItem(int slot)
		{
			if (overrideExtract) return true;
			if (slot == 1 || slot == 8) return false;
			return true;
		}

		public boolean isItemValidForSlot(int slot, ItemStack stack)
		{
			switch (slot)
			{
				case 0:
					return CyberwareAPI.canDeconstruct(stack);
				case 1:
					int[] ids = OreDictionary.getOreIDs(stack);
					int paperId = OreDictionary.getOreID("paper");
					for (int id : ids)
					{
						if (id == paperId)
						{
							return true;
						}
					}
					return false;
				case 8:
					return stack != null && stack.getItem() instanceof IBlueprint;
				case 9:
					return false;
			}
			return true;
		}
	}
	
	public class GuiWrapper implements IItemHandlerModifiable
	{
		private ItemStackHandlerEngineering slots;

		public GuiWrapper(ItemStackHandlerEngineering slots)
		{
			this.slots = slots;
		}

		@Override
		public int getSlots()
		{
			return slots.getSlots();
		}

		@Override
		public ItemStack getStackInSlot(int slot)
		{
			return slots.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			return slots.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			slots.overrideExtract = true;
			ItemStack ret = slots.extractItem(slot, amount, simulate);
			slots.overrideExtract = false;
			return ret;
		}

		@Override
		public void setStackInSlot(int slot, ItemStack stack)
		{
			slots.setStackInSlot(slot, stack);
		}
		
	}
	
	public ItemStackHandlerEngineering slots = new ItemStackHandlerEngineering(10);
	private final RangedWrapper slotsTopSides = new RangedWrapper(slots, 0, 7);
	private final SpecificWrapper slotsBottom = new SpecificWrapper(slots, 2, 3, 4, 5, 6, 7, 9);
	public final GuiWrapper guiSlots = new GuiWrapper(slots);
	public String customName = null;
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (facing == EnumFacing.DOWN)
			{
				return (T) slotsBottom;
			}
			else
			{
				return (T) slotsTopSides;
			}
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		slots.deserializeNBT(compound.getCompoundTag("inv"));
		
		if (compound.hasKey("CustomName", 8))
		{
			customName = compound.getString("CustomName");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		
		compound.setTag("inv", this.slots.serializeNBT());
		
		if (this.hasCustomName())
		{
			compound.setString("CustomName", customName);
		}
		
		return compound;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	public String getName()
	{
		return this.hasCustomName() ? customName : "cyberware.container.engineering";
	}

	public boolean hasCustomName()
	{
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomInventoryName(String p_145951_1_)
	{
		this.customName = p_145951_1_;
	}
	
	public ITextComponent getDisplayName()
	{
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}
	
}
