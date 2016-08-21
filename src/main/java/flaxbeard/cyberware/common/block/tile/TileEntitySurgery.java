package flaxbeard.cyberware.common.block.tile;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.handler.EssentialsMissingHandler;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

public class TileEntitySurgery extends TileEntity implements ITickable
{
	public ItemStackHandler slotsPlayer = new ItemStackHandler(120);
	public ItemStackHandler slots = new ItemStackHandler(120);
	public boolean[] discardSlots = new boolean[120];
	public boolean[] isEssentialMissing = new boolean[EnumSlot.values().length * 2];
	public int essence = 0;
	public int maxEssence = 0;
	public int wrongSlot = -1;
	public int ticksWrong = 0;
	public int lastEntity = -1;
	public int cooldownTicks = 0;
	public boolean missingPower = false;

	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	public void updatePlayerSlots(EntityLivingBase entity)
	{
		markDirty();
		if (CyberwareAPI.hasCapability(entity))
		{
			if (entity.getEntityId() != lastEntity)
			{
				for (int i = 0; i < discardSlots.length; i++)
				{
					discardSlots[i] = false;
				}
				lastEntity = entity.getEntityId();
			}
			ICyberwareUserData c = CyberwareAPI.getCapability(entity);
			
			// Update slotsPlayer with the items in the player's body
			int i = 0;
			for (EnumSlot slotType : EnumSlot.values())
			{
				for (int n = 0; n < LibConstants.WARE_PER_SLOT; n++)
				{
					ItemStack toPut = ItemStack.copyItemStack(c.getInstalledCyberware(slotType)[n]);
					
					// If there's a new item, don't set it to discard by default unless it conflicts
					if (!ItemStack.areItemStacksEqual(toPut, slotsPlayer.getStackInSlot(i * LibConstants.WARE_PER_SLOT + n)))
					{
						discardSlots[i * LibConstants.WARE_PER_SLOT + n] = false;
						if (doesItemConflict(toPut, slotType, n))
						{
							discardSlots[i * LibConstants.WARE_PER_SLOT + n] = true;
						}
					}
					slotsPlayer.setStackInSlot(i * LibConstants.WARE_PER_SLOT + n, toPut);

				}
				updateEssential(slotType);
				i++;
			}
			
			// Check for items with requirements that are no longer fulfilled
			boolean needToCheck = true;
			while (needToCheck)
			{
				i = 0;
				needToCheck = false;
				for (EnumSlot slotType : EnumSlot.values())
				{
					for (int n = 0; n < LibConstants.WARE_PER_SLOT; n++)
					{
						int index = i * LibConstants.WARE_PER_SLOT + n;
						
						ItemStack stack = slots.getStackInSlot(index);
						if (stack != null && !areRequirementsFulfilled(stack, slotType, n))
						{
							addItemStack(entity, stack);
							slots.setStackInSlot(index, null);
							needToCheck = true;
						}
					}
					i++;
				}
			}
			
			this.updateEssence();
		}
		else
		{
			slotsPlayer = new ItemStackHandler(120);
			for (EnumSlot slotType : EnumSlot.values())
			{
				updateEssential(slotType);
			}
		}
		wrongSlot = -1;
	}
	
	public boolean doesItemConflict(@Nullable ItemStack stack, EnumSlot slot, int n)
	{
		int row = slot.ordinal();
		if (stack != null)
		{
			for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
			{
				if (i != n)
				{
					int index = row * LibConstants.WARE_PER_SLOT + i;
					ItemStack slotStack = slots.getStackInSlot(index);
					ItemStack playerStack = slotsPlayer.getStackInSlot(index);
					
					ItemStack otherStack = slotStack != null ? slotStack : (discardSlots[index] ? null : playerStack);
					
					// Automatically incompatible with the same item/damage. Doesn't use areCyberwareStacksEqual because items conflict even if different grades.
					if (otherStack != null && (otherStack.getItem() == stack.getItem() && otherStack.getItemDamage() == stack.getItemDamage()))
					{
						setWrongSlot(index);
						return true;
					}
				
					// Incompatible if either stack doesn't like the other one
					if (otherStack != null && CyberwareAPI.getCyberware(otherStack).isIncompatible(otherStack, stack))
					{
						setWrongSlot(index);
						return true;
					}
					if (otherStack != null && CyberwareAPI.getCyberware(stack).isIncompatible(stack, otherStack))
					{
						setWrongSlot(index);
						return true;
					}
				}
			}
		}

		
		return false;
	}
	
	public void setWrongSlot(int index)
	{
		this.wrongSlot = index;
		Cyberware.proxy.wrong(this);
	}
	
	public void disableDependants(ItemStack stack, EnumSlot slot, int n)
	{
		int row = slot.ordinal();
		if (stack != null)
		{
			for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
			{
				if (i != n)
				{
					int index = row * LibConstants.WARE_PER_SLOT + i;
					ItemStack playerStack = slotsPlayer.getStackInSlot(index);
					
					
					if (!areRequirementsFulfilled(playerStack, slot, n))
					{
						discardSlots[index] = true;
					}
				}
			}
		}
	}
	
	public void enableDependsOn(ItemStack stack, EnumSlot slot, int n)
	{
		if (stack != null)
		{
			ICyberware ware = CyberwareAPI.getCyberware(stack);
			for (ItemStack[] neededItem : ware.required(stack))
			{
				boolean found = false;
				
				outerLoop:
				for (ItemStack needed : neededItem)
				{
	
					
					for (int row = 0; row < EnumSlot.values().length; row++)
					{
						for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
						{
							if (i != n)
							{
								int index = row * LibConstants.WARE_PER_SLOT + i;
								ItemStack playerStack = slotsPlayer.getStackInSlot(index);
								
								if (playerStack != null && playerStack.getItem() == needed.getItem() && playerStack.getItemDamage() == needed.getItemDamage())
								{
									found = true;
									discardSlots[index] = false;
									break outerLoop;
								}
							}
						}
					}
					
				}
				if (!found) System.out.println("BADDDD!!!!!!!!!");

			}
		}
	}
	
	public boolean canDisableItem(ItemStack stack, EnumSlot slot, int n)
	{
		if (stack != null)
		{
			for (int row = 0; row < EnumSlot.values().length; row++)
			{
				for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
				{
					if (i != n)
					{
						int index = row * LibConstants.WARE_PER_SLOT + i;
						ItemStack slotStack = slots.getStackInSlot(index);
						ItemStack playerStack = null;//slotsPlayer.getStackInSlot(index);
						
						ItemStack otherStack = slotStack != null ? slotStack : (discardSlots[index] ? null : playerStack);
						
						if (!areRequirementsFulfilled(otherStack, slot, n))
						{
							setWrongSlot(index);
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public boolean areRequirementsFulfilled(ItemStack stack, EnumSlot slot, int n)
	{
		if (stack != null)
		{
			ICyberware ware = CyberwareAPI.getCyberware(stack);
			for (ItemStack[] neededItem : ware.required(stack))
			{
				boolean found = false;

				outerLoop:
				for (ItemStack needed : neededItem)
				{
					
					for (int row = 0; row < EnumSlot.values().length; row++)
					{
						for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
						{
							if (i != n)
							{
								int index = row * LibConstants.WARE_PER_SLOT + i;
								ItemStack slotStack = slots.getStackInSlot(index);
								ItemStack playerStack = slotsPlayer.getStackInSlot(index);
								
								ItemStack otherStack = slotStack != null ? slotStack : (discardSlots[index] ? null : playerStack);
								
								if (otherStack != null && otherStack.getItem() == needed.getItem() && otherStack.getItemDamage() == needed.getItemDamage())
								{
									found = true;
									break outerLoop;
								}
							}
						}
					}
					
				}
				if (!found) return false;

			}
		}
		
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		slots.deserializeNBT(compound.getCompoundTag("inv"));
		slotsPlayer.deserializeNBT(compound.getCompoundTag("inv2"));
		
		NBTTagList list = (NBTTagList) compound.getTag("discard");
		for (int i = 0; i < list.tagCount(); i++)
		{
			this.discardSlots[i] = ((NBTTagByte) list.get(i)).getByte() > 0;
		}
		
		this.essence = compound.getInteger("essence");
		this.maxEssence = compound.getInteger("maxEssence");
		this.lastEntity = compound.getInteger("lastEntity");
		this.missingPower = compound.getBoolean("missingPower");
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		
		compound = super.writeToNBT(compound);
	
		compound.setInteger("essence", essence);
		compound.setInteger("maxEssence", maxEssence);
		compound.setInteger("lastEntity", lastEntity);
		compound.setBoolean("missingPower", missingPower);
		
		compound.setTag("inv", this.slots.serializeNBT());
		compound.setTag("inv2", this.slotsPlayer.serializeNBT());
		
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.discardSlots.length; i++)
		{
			list.appendTag(new NBTTagByte((byte) (this.discardSlots[i] ? 1 : 0)));
		}
		compound.setTag("discard", list);

		return compound;
	}
	
	public void updateEssential(EnumSlot slot)
	{
		if (slot.hasEssential())
		{
			byte answer = isEssential(slot);
			boolean foundFirst = (answer & 1) > 0;
			boolean foundSecond = (answer & 2) > 0;
			this.isEssentialMissing[slot.ordinal() * 2] = !foundFirst;
			this.isEssentialMissing[slot.ordinal() * 2 + 1] = !foundSecond;
		}
		else
		{
			this.isEssentialMissing[slot.ordinal() * 2] = false;
			this.isEssentialMissing[slot.ordinal() * 2 + 1] = false;
		}
	}

	private byte isEssential(EnumSlot slot)
	{
		byte r = 0;
		
		for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
		{
			int index = slot.ordinal() * LibConstants.WARE_PER_SLOT + i;
			ItemStack slotStack = slots.getStackInSlot(index);
			ItemStack playerStack = slotsPlayer.getStackInSlot(index);
			
			ItemStack stack = slotStack != null ? slotStack : (discardSlots[index] ? null : playerStack);

			if (stack != null)
			{
				ICyberware ware = CyberwareAPI.getCyberware(stack);
				if (ware.isEssential(stack))
				{
					if (slot.isSided() && ware instanceof ISidedLimb)
					{
						if (((ISidedLimb) ware).getSide(stack) == EnumSide.LEFT && (r & 1) == 0)
						{
							r += 1;
						}
						else if ((r & 2) == 0)
						{
							r += 2;
						}
					}
					else
					{
						return 3;
					}
				}
				
			}
		}
		return r;
	}

	@Override
	public void update()
	{
		if (inProgress && progressTicks < 80)
		{
			if (targetEntity != null && !targetEntity.isDead && CyberwareAPI.hasCapability(targetEntity))
			{
				BlockPos pos = getPos();
				
				if (progressTicks > 20 && progressTicks < 60)
				{
					targetEntity.posX = pos.getX() + .5F;
					targetEntity.posZ = pos.getZ() + .5F;
				}
				
				if (progressTicks >= 20 && progressTicks <= 60 && progressTicks % 5 == 0)
				{
					targetEntity.attackEntityFrom(EssentialsMissingHandler.surgery, 2F);
				}
				
				if (progressTicks == 60)
				{
					processUpdate();
				}
				
				progressTicks++;
				
				
				if (Cyberware.proxy.workingOnPlayer(targetEntity))
				{
					workingOnPlayer = true;
					playerProgressTicks = progressTicks;
				}
			}
			else
			{
				inProgress = false;
				progressTicks = 0;
				if (Cyberware.proxy.workingOnPlayer(targetEntity))
				{
					workingOnPlayer = false;
				}
				targetEntity = null;
				
				IBlockState state = worldObj.getBlockState(getPos().down());
				if (state.getBlock() instanceof BlockSurgeryChamber)
				{
					((BlockSurgeryChamber) state.getBlock()).toggleDoor(true, state, getPos().down(), worldObj);
				}
			}
		}
		else if (inProgress)
		{

			if (Cyberware.proxy.workingOnPlayer(targetEntity))
			{
				workingOnPlayer = false;
			}
			inProgress = false;
			progressTicks = 0;
			targetEntity = null;
			cooldownTicks = 60;
			
			IBlockState state = worldObj.getBlockState(getPos().down());
			if (state.getBlock() instanceof BlockSurgeryChamber)
			{
				((BlockSurgeryChamber) state.getBlock()).toggleDoor(true, state, getPos().down(), worldObj);
			}
		}
		
		if (cooldownTicks > 0)
		{
			cooldownTicks--;
		}
	}

	public void processUpdate()
	{
		updatePlayerSlots(targetEntity);
		
		BlockPos p = getPos();

		ICyberwareUserData cyberware = CyberwareAPI.getCapability(targetEntity);
		
		for (int slotIndex = 0; slotIndex < EnumSlot.values().length; slotIndex++)
		{
			EnumSlot slot = EnumSlot.values()[slotIndex];
			ItemStack[] wares = new ItemStack[LibConstants.WARE_PER_SLOT];
			
			int c = 0;
			for (int j = slotIndex * LibConstants.WARE_PER_SLOT; j < (slotIndex + 1) * LibConstants.WARE_PER_SLOT; j++)
			{
				ItemStack newStack = slots.getStackInSlot(j);

				ItemStack targetEntityStack = ItemStack.copyItemStack(slotsPlayer.getStackInSlot(j));
				if (newStack != null && newStack.stackSize > 0)
				{
					ItemStack ret = newStack.copy();
					if (CyberwareAPI.areCyberwareStacksEqual(ret, targetEntityStack))
					{
						int maxSize = CyberwareAPI.getCyberware(ret).installedStackSize(ret);

						if (ret.stackSize < maxSize)
						{
							int numToShift = Math.min(maxSize - ret.stackSize, targetEntityStack.stackSize);
							targetEntityStack.stackSize -= numToShift;
							ret.stackSize += numToShift;
						}
					}

					if (targetEntityStack != null && targetEntityStack.stackSize > 0)
					{
						targetEntityStack = CyberwareAPI.sanitize(targetEntityStack);

						addItemStack(targetEntity, targetEntityStack);
					}
					
					wares[c] = ret;

					c++;
				}
				else if (targetEntityStack != null && targetEntityStack.stackSize > 0)
				{
					if (discardSlots[j])
					{				
						targetEntityStack = CyberwareAPI.sanitize(targetEntityStack);

						addItemStack(targetEntity, targetEntityStack);
						
					}
					else
					{
						wares[c] = slotsPlayer.getStackInSlot(j).copy();
						c++;
					}
				}
			}
			if (!worldObj.isRemote)
			{
				cyberware.setInstalledCyberware(targetEntity, slot, wares);
			}
			cyberware.setHasEssential(slot, !isEssentialMissing[slotIndex * 2], !isEssentialMissing[slotIndex * 2 + 1]);
		}
		cyberware.setEssence(essence);
		cyberware.updateCapacity();
		cyberware.setImmune();
		if (!worldObj.isRemote)
		{
			CyberwareAPI.updateData(targetEntity);
		}
		slots = new ItemStackHandler(120);
		
	}

	private void addItemStack(EntityLivingBase entity, ItemStack stack)
	{
		boolean flag = true;
		
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = ((EntityPlayer) entity);
			flag = !player.inventory.addItemStackToInventory(stack);
		}

		if (flag && !worldObj.isRemote)
		{

			EntityItem item = new EntityItem(worldObj, getPos().getX() + .5F, getPos().getY() - 2F, getPos().getZ() + .5F, stack);
			worldObj.spawnEntityInWorld(item);
		}
	}

	public boolean canOpen()
	{
		return !inProgress && cooldownTicks <= 0;
	}

	public void notifyChange()
	{
		boolean opened = worldObj.getBlockState(getPos().down()).getValue(BlockSurgeryChamber.OPEN);

		if (!opened)
		{
			BlockPos p = getPos();
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.getX(), p.getY() - 2F, p.getZ(), p.getX() + 1F, p.getY(), p.getZ() + 1F));
			if (entities.size() == 1)
			{
				this.inProgress = true;
				this.progressTicks = 0;
				this.targetEntity = entities.get(0);
			}
		}
	}
	
	public boolean inProgress = false;
	public EntityLivingBase targetEntity = null;
	public int progressTicks = 0;
	public static boolean workingOnPlayer = false;
	public static int playerProgressTicks = 0;

	public void updateEssence()
	{
		this.maxEssence = this.essence = CyberwareConfig.ESSENCE; // TODO
		boolean hasConsume = false;
		boolean hasProduce = false;
		
		for (EnumSlot slot : EnumSlot.values())
		{
			for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++)
			{
				int index = slot.ordinal() * LibConstants.WARE_PER_SLOT + i;
				ItemStack slotStack = slots.getStackInSlot(index);
				ItemStack playerStack = slotsPlayer.getStackInSlot(index);
				
				ItemStack stack = slotStack != null ? slotStack : (discardSlots[index] ? null : playerStack);
				
				if (stack != null)
				{
					
					ItemStack ret = stack.copy();
					if (slotStack != null && ret != null && playerStack != null && CyberwareAPI.areCyberwareStacksEqual(playerStack, ret))
					{
						int maxSize = CyberwareAPI.getCyberware(ret).installedStackSize(ret);

						if (ret.stackSize < maxSize)
						{
							int numToShift = Math.min(maxSize - ret.stackSize, playerStack.stackSize);
							ret.stackSize += numToShift;
						}
					}
					ICyberware ware = CyberwareAPI.getCyberware(ret);

					this.essence -= ware.getEssenceCost(ret);
					
					if (ware instanceof ItemCyberware && ((ItemCyberware) ware).getPowerConsumption(ret) > 0)
					{
						hasConsume = true;
					}
					if (ware instanceof ItemCyberware && (((ItemCyberware) ware).getPowerProduction(ret) > 0 || ware == CyberwareContent.creativeBattery))
					{
						hasProduce = true;
					}
						
				}
			}
		}
		
		this.missingPower = hasConsume && !hasProduce;
	}
}
