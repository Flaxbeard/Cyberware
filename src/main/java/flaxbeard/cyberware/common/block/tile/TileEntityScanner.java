package flaxbeard.cyberware.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.oredict.OreDictionary;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.ItemBlueprint;

public class TileEntityScanner extends TileEntity implements ITickable
{
	public class ItemStackHandlerScanner extends ItemStackHandler
	{
		public boolean overrideExtract = false;
		private TileEntityScanner table;
		
		public ItemStackHandlerScanner(TileEntityScanner table, int i)
		{
			super(i);
			this.table = table;
		}
		
		@Override
	    public void setStackInSlot(int slot, ItemStack stack)
	    {
			super.setStackInSlot(slot, stack);

	    }

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (!isItemValidForSlot(slot, stack)) return stack;
			
			ItemStack result = super.insertItem(slot, stack, simulate);
			return result;
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if (!canRemoveItem(slot)) return null;
			

			ItemStack result = super.extractItem(slot, amount, simulate);

			return result;
		}

		public boolean canRemoveItem(int slot)
		{
			if (overrideExtract) return true;
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
				case 2:
					return false;
			}
			return true;
		}
	}
	
	public class GuiWrapper implements IItemHandlerModifiable
	{
		private ItemStackHandlerScanner slots;

		public GuiWrapper(ItemStackHandlerScanner slots)
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
	
	public ItemStackHandlerScanner slots = new ItemStackHandlerScanner(this, 3);
	private final RangedWrapper slotsTopSides = new RangedWrapper(slots, 0, 2);
	private final RangedWrapper slotsBottom = new RangedWrapper(slots, 2, 3);
	private final RangedWrapper slotsBottom2 = new RangedWrapper(slots, 0, 1);
	public final GuiWrapper guiSlots = new GuiWrapper(slots);
	public String customName = null;
	public int ticks = 0;
	public int ticksMove = 0;
	public int lastX = 0, x = 0;
	public int lastZ = 0, z = 0;
	
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
				if (slots.getStackInSlot(2) != null && slots.getStackInSlot(0) != null)
				{
					return (T) slotsBottom2;
				}
				else
				{
					return (T) slotsBottom;
				}
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
		
		this.ticks = compound.getInteger("ticks");
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
		
		compound.setInteger("ticks", ticks);
		
		return compound;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound data = pkt.getNbtCompound();
		this.readFromNBT(data);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound data = new NBTTagCompound();
		this.writeToNBT(data);
		return new SPacketUpdateTileEntity(pos, 0, data);
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}
	
	
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	public String getName()
	{
		return this.hasCustomName() ? customName : "cyberware.container.scanner";
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


	@Override
	public void update()
	{
		ItemStack toDestroy = slots.getStackInSlot(0);
		if (CyberwareAPI.canDeconstruct(toDestroy) && toDestroy.stackSize > 0 && (slots.getStackInSlot(2) == null || slots.getStackInSlot(2).stackSize == 0))
		{
			ticks++;
			
			if (ticksMove > ticks || (ticks - ticksMove > Math.max((Math.abs(lastX - x) * 3), (Math.abs(lastZ - z) * 3)) + 10))
			{
				ticksMove = ticks;
				lastX = x;
				lastZ = z;
				while (x == lastX)
				{
					x = worldObj.rand.nextInt(11);
				}
				while (z == lastZ)
				{
					z = worldObj.rand.nextInt(11);
				}
			}
			if (ticks > CyberwareConfig.SCANNER_TIME)
			{
				ticks = 0;
				ticksMove = 0;

				if (!worldObj.isRemote && (slots.getStackInSlot(1) != null && slots.getStackInSlot(1).stackSize > 0))
				{
					float chance = CyberwareConfig.SCANNER_CHANCE + (CyberwareConfig.SCANNER_CHANCE_ADDL * (slots.getStackInSlot(0).stackSize - 1));
					chance = Math.min(chance, 50F);
					
					if (worldObj.rand.nextFloat() < (chance / 100F))
					{
						ItemStack blue = ItemBlueprint.getBlueprintForItem(toDestroy);
						slots.setStackInSlot(2, blue);
						ItemStack current = slots.getStackInSlot(1);
						current.stackSize--;
						if (current.stackSize <= 0)
						{
							current = null;
						}
						slots.setStackInSlot(1, current);
						worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 2);

					}
					
				}
			}
			this.markDirty();
		}
		else
		{
			x = lastX = z = lastZ = 0;
			if (ticks != 0)
			{
				this.ticks = 0;
				this.markDirty();
			}
		}
	}


	public float getProgress()
	{
		return ticks * 1F / CyberwareConfig.SCANNER_TIME;
	}

	
}
