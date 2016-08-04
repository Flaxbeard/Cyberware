package flaxbeard.cyberware.common.block.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.oredict.OreDictionary;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IBlueprint;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import flaxbeard.cyberware.common.misc.SpecificWrapper;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.ScannerSmashPacket;

public class TileEntityEngineeringTable extends TileEntity implements ITickable
{
	public static class TileEntityEngineeringDummy extends TileEntity
	{
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			TileEntity above = worldObj.getTileEntity(pos.add(0, 1, 0));
			if (above != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			{
				return above.hasCapability(capability, facing);
			}
			return super.hasCapability(capability, facing);
		}
		

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			TileEntity above = worldObj.getTileEntity(pos.add(0, 1, 0));
			if (above != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			{
				return above.getCapability(capability, facing);
			}
			return super.getCapability(capability, facing);
		}
	}
	
	public class ItemStackHandlerEngineering extends ItemStackHandler
	{
		public boolean overrideExtract = false;
		private TileEntityEngineeringTable table;
		
		public ItemStackHandlerEngineering(TileEntityEngineeringTable table, int i)
		{
			super(i);
			this.table = table;
		}
		
		@Override
	    public void setStackInSlot(int slot, ItemStack stack)
	    {
			boolean check = false;
			if (slot == 0 && this.getStackInSlot(0) == null && !worldObj.isRemote)
			{
				check = true;
			}
			
			super.setStackInSlot(slot, stack);
			
			if (check)
			{
				table.worldObj.setBlockState(getPos(), table.worldObj.getBlockState(getPos()), 2);
				table.worldObj.notifyBlockUpdate(pos, table.worldObj.getBlockState(getPos()), table.worldObj.getBlockState(getPos()), 2);
			}
			
			if (slot >= 2 && slot <= 8)
			{
				table.updateRecipe();
			}
	    }

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (!isItemValidForSlot(slot, stack)) return stack;
			
			boolean check = false;
			if (slot == 0 && this.getStackInSlot(0) == null && !simulate && !worldObj.isRemote)
			{
				check = true;
			}
			
			ItemStack result = super.insertItem(slot, stack, simulate);
			
			if (check)
			{
				table.worldObj.setBlockState(getPos(), table.worldObj.getBlockState(getPos()), 2);
				table.worldObj.notifyBlockUpdate(pos, table.worldObj.getBlockState(getPos()), table.worldObj.getBlockState(getPos()), 2);
			}

			if (slot >= 2 && slot <= 8 && !simulate)
			{
				table.updateRecipe();
			}
			return result;
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if (!canRemoveItem(slot)) return null;
			

			ItemStack result = super.extractItem(slot, amount, simulate);
			if (slot == 9 && result != null && !simulate)
			{

				table.subtractResources();
				
			}
			if (slot >= 2 && slot <= 7 && !simulate)
			{
				table.updateRecipe();
			}

			return result;
		}

		public boolean canRemoveItem(int slot)
		{
			if (overrideExtract) return true;
			if (getStackInSlot(8) != null && (slot >= 2 && slot <= 7)) return false;
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
				default:
					return overrideExtract || !CyberwareAPI.canDeconstruct(stack);
			}
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
			slots.overrideExtract = true;
			ItemStack res = slots.insertItem(slot, stack, simulate);
			slots.overrideExtract = false;
			return res;
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
			slots.overrideExtract = true;
			slots.setStackInSlot(slot, stack);
			slots.overrideExtract = false;
		}
		
	}
	
	public ItemStackHandlerEngineering slots = new ItemStackHandlerEngineering(this, 10);
	private final RangedWrapper slotsTopSides = new RangedWrapper(slots, 0, 7);
	private final SpecificWrapper slotsBottom = new SpecificWrapper(slots, 2, 3, 4, 5, 6, 7, 9);
	public final GuiWrapper guiSlots = new GuiWrapper(slots);
	public String customName = null;
	public float clickedTime = -100F;
	private int time;
	
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
		
		this.time = compound.getInteger("time");
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
		
		compound.setInteger("time", time);
		
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

	public void updateRecipe()
	{
		ItemStack blueprintStack = slots.getStackInSlot(8);
		if (blueprintStack != null && blueprintStack.getItem() instanceof IBlueprint)
		{
			IBlueprint blueprint = (IBlueprint) blueprintStack.getItem();
			ItemStack[] toCheck = new ItemStack[6];
			for (int i = 0; i < 6; i++)
			{
				toCheck[i] = ItemStack.copyItemStack(slots.getStackInSlot(i + 2));
			}
			ItemStack result = ItemStack.copyItemStack(blueprint.getResult(blueprintStack, toCheck));
			if (result != null)
			{
				result.stackSize = 1;
			}
			this.slots.setStackInSlot(9, result);
		}
		else
		{
			this.slots.setStackInSlot(9, null);
		}
	}
	
	public void subtractResources()
	{
		ItemStack blueprintStack = slots.getStackInSlot(8);
		if (blueprintStack != null && blueprintStack.getItem() instanceof IBlueprint)
		{
			IBlueprint blueprint = (IBlueprint) blueprintStack.getItem();
			ItemStack[] toCheck = new ItemStack[6];
			for (int i = 0; i < 6; i++)
			{
				toCheck[i] = ItemStack.copyItemStack(slots.getStackInSlot(i + 2));
			}
			ItemStack[] result = blueprint.consumeItems(blueprintStack, toCheck);
			for (int i = 0; i < 6; i++)
			{
				slots.setStackInSlot(i + 2, result[i]);
			}
			this.updateRecipe();
		}
		else
		{
			throw new IllegalStateException("Tried to subtract resources when no blueprint was available!");
		}
	}

	// Runs on the server 
	public void smash(boolean pkt)
	{
		ItemStack toDestroy = slots.getStackInSlot(0);
		
		if (CyberwareAPI.canDeconstruct(toDestroy) && toDestroy.stackSize > 0)
		{
			ItemStack paperSlot = slots.getStackInSlot(1);
			boolean doBlueprint = paperSlot != null && paperSlot.stackSize > 0;
			
			ItemStack[] components = CyberwareAPI.getComponents(toDestroy).clone();

			List<ItemStack> random = new ArrayList<ItemStack>();
			for (ItemStack component : components)
			{
				if (component != null)
				{
					for (int i = 0; i < component.stackSize; i++)
					{
						ItemStack copy = component.copy();
						copy.stackSize = 1;
						random.add(copy);
					}
				}
			}
			
			int numToRemove = 1;
			switch (worldObj.getDifficulty())
			{
				case EASY:
					numToRemove = 1;
					break;
				case HARD:
					numToRemove = 3;
					break;
				case NORMAL:
					numToRemove = 2;
					break;
				case PEACEFUL:
					numToRemove = 1;
					break;
				default:
					break;
			}
			numToRemove = Math.min(numToRemove, random.size() - 1);
			for (int i = 0; i < numToRemove; i++)
			{
				random.remove(worldObj.rand.nextInt(random.size()));
			}

			ItemStackHandler handler = new ItemStackHandler(6);
			for (int i = 0; i < 6; i++)
			{
				handler.setStackInSlot(i, ItemStack.copyItemStack(slots.getStackInSlot(i + 2)));
			}
			boolean canInsert = true;
			
			// Check if drops will fit
			for (ItemStack drop : components)
			{
				ItemStack left = drop.copy();
				boolean wasAble = false;
				for (int slot = 0; slot < 6; slot++)
				{
					left = handler.insertItem(slot, left, false);
					if (left == null)
					{
						wasAble = true;
						break;
					}
				}
				
				if (!wasAble)
				{
					canInsert = false;
					break;
				}
			}
			
			// Check if blueprint will fit
			if (doBlueprint)
			{
				ItemStack left = ItemBlueprint.getBlueprintForItem(toDestroy);
				boolean wasAble = false;
				for (int slot = 0; slot < 6; slot++)
				{
					left = handler.insertItem(slot, left, false);
					if (left == null)
					{
						wasAble = true;
						break;
					}
				}
				
				if (!wasAble)
				{
					canInsert = false;
				}
			}

			
			if (canInsert)
			{
				if (pkt)
				{
					CyberwarePacketHandler.INSTANCE.sendToAllAround(new ScannerSmashPacket(pos.getX(), pos.getY(), pos.getZ()), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 25));
				}
				
				if (!worldObj.isRemote)
				{
					if (doBlueprint && getWorld().rand.nextFloat() < (CyberwareConfig.ENGINEERING_CHANCE / 100F))
					{
						ItemStack blue = ItemBlueprint.getBlueprintForItem(toDestroy);
						random.add(blue);
						
						ItemStack current = slots.getStackInSlot(1);
						current.stackSize--;
						if (current.stackSize <= 0)
						{
							current = null;
						}
						slots.setStackInSlot(1, current);
					}
			
					
					for (ItemStack drop : random)
					{
						ItemStack dropLeft = drop.copy();
						for (int slot = 2; slot < 8; slot++)
						{
							if (slots.getStackInSlot(slot) != null)
							{
								dropLeft = slots.insertItem(slot, dropLeft, false);
								if (dropLeft == null)
								{
									break;
								}
							}
						}
						
						for (int slot = 2; slot < 8; slot++)
						{
							dropLeft = slots.insertItem(slot, dropLeft, false);
							if (dropLeft == null)
							{
								break;
							}
						}
					}
					
					ItemStack current = slots.getStackInSlot(0);
					current.stackSize--;
					if (current.stackSize <= 0 || current == null)
					{
						worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 2);

						current = null;
					}
					slots.setStackInSlot(0, current);
					updateRecipe();
				}
				else
				{
					smashSounds();
				}
			}
		}

	}


	@Override
	public void update()
	{
		if (worldObj.isBlockPowered(getPos()) || worldObj.isBlockPowered(getPos().add(0, -1, 0)))
		{
			if (time == 0)
			{
				this.smash(false);
			}
			time = (time + 1) % 25;
		}
		else
		{
			time = 0;
		}
	}


	public void smashSounds()
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		clickedTime = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
		worldObj.playSound(x, y, z, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1F, 1F, false);
		worldObj.playSound(x, y, z, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1F, .5F, false);
		for (int i = 0; i < 10; i++)
		{
			worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, x + .5F, y, z + .5F, .25F * (worldObj.rand.nextFloat() - .5F), .1F, .25F * (worldObj.rand.nextFloat() - .5F), 
					new int[] { Item.getIdFromItem(slots.getStackInSlot(0).getItem()) } );
		}
	}

	
}
