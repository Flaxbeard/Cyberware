package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.lib.LibConstants;

public class CyberwareUserDataImpl implements ICyberwareUserData
{
	public static final IStorage<ICyberwareUserData> STORAGE = new CyberwareUserDataStorage();

	private ItemStack[][] wares = new ItemStack[EnumSlot.values().length][LibConstants.WARE_PER_SLOT];
	private boolean[] missingEssentials = new boolean[LibConstants.WARE_PER_SLOT];
	
	public CyberwareUserDataImpl()
	{
		for (int i = 0; i < wares.length; i++)
		{
			wares[i] = CyberwareConfig.getStartingItems(EnumSlot.values()[i]);
		}
	}
	
	@Override
	public ItemStack[] getInstalledCyberware(EnumSlot slot)
	{
		return wares[slot.ordinal()];
	}
	
	@Override
	public boolean hasEssential(EnumSlot slot)
	{
		return !missingEssentials[slot.ordinal()];
	}
	
	@Override
	public void setHasEssential(EnumSlot slot, boolean has)
	{
		missingEssentials[slot.ordinal()] = !has;
	}

	@Override
	public void setInstalledCyberware(EnumSlot slot, List<ItemStack> cyberware)
	{
		wares[slot.ordinal()] = cyberware.toArray(new ItemStack[0]);
	}
	
	@Override
	public void setInstalledCyberware(EnumSlot slot, ItemStack[] cyberware)
	{
		wares[slot.ordinal()] = cyberware;
	}
	
	@Override
	public boolean isCyberwareInstalled(ItemStack cyberware)
	{
		return getCyberwareRank(cyberware) > 0;
	}
	
	@Override
	public int getCyberwareRank(ItemStack cyberware)
	{
		ItemStack cw = getCyberware(cyberware);
		
		if (cw != null)
		{
			return cw.stackSize;
		}
		
		return 0;
	}
	
	@Override
	public ItemStack getCyberware(ItemStack cyberware)
	{
		ItemStack[] slotItems = getInstalledCyberware(CyberwareAPI.getCyberware(cyberware).getSlot(cyberware));
		for (ItemStack item : slotItems)
		{
			if (item != null && item.getItem() == cyberware.getItem() && item.getItemDamage() == cyberware.getItemDamage())
			{
				return item;
			}
		}
		return null;
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		
		for (EnumSlot slot : EnumSlot.values())
		{
			NBTTagList list2 = new NBTTagList();
			for (ItemStack cyberware : getInstalledCyberware(slot))
			{
				NBTTagCompound temp = new NBTTagCompound();
				if (cyberware != null)
				{
					temp = cyberware.writeToNBT(temp);
				}
				list2.appendTag(temp);
			}
			list.appendTag(list2);
		}
		
		compound.setTag("cyberware", list);
		
		NBTTagList essentialList = new NBTTagList();
		for (int i = 0; i < this.missingEssentials.length; i++)
		{
			essentialList.appendTag(new NBTTagByte((byte) (this.missingEssentials[i] ? 1 : 0)));
		}
		compound.setTag("discard", essentialList);
		
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		NBTTagList essentialList = (NBTTagList) tag.getTag("discard");
		for (int i = 0; i < essentialList.tagCount(); i++)
		{
			this.missingEssentials[i] = ((NBTTagByte) essentialList.get(i)).getByte() > 0;
		}
		
		NBTTagList list = (NBTTagList) tag.getTag("cyberware");
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			EnumSlot slot = EnumSlot.values()[i];
			
			NBTTagList list2 = (NBTTagList) list.get(i);
			ItemStack[] cyberware = new ItemStack[LibConstants.WARE_PER_SLOT];
			
			for (int j = 0; j < list2.tagCount() && j < cyberware.length; j++)
			{
				cyberware[j] = ItemStack.loadItemStackFromNBT(list2.getCompoundTagAt(j));
			}
			
			setInstalledCyberware(slot, cyberware);
		}
		
	}
	
	private static class CyberwareUserDataStorage implements IStorage<ICyberwareUserData>
	{
		@Override
		public NBTBase writeNBT(Capability<ICyberwareUserData> capability, ICyberwareUserData instance, EnumFacing side)
		{
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<ICyberwareUserData> capability, ICyberwareUserData instance, EnumFacing side, NBTBase nbt)
		{
			if (nbt instanceof NBTTagCompound)
			{
				instance.deserializeNBT((NBTTagCompound) nbt);
			}
			else
			{
				throw new IllegalStateException("Cyberware NBT should be a NBTTagCompound!");
			}
		}
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagCompound>
	{
		public static final ResourceLocation NAME = new ResourceLocation(Cyberware.MODID, "cyberware");
		
		private final ICyberwareUserData cap = new CyberwareUserDataImpl();

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return capability == CyberwareAPI.CYBERWARE_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			if (capability == CyberwareAPI.CYBERWARE_CAPABILITY)
			{
				return CyberwareAPI.CYBERWARE_CAPABILITY.cast(cap);
			}
			
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			return cap.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			cap.deserializeNBT(nbt);
		}
		
	}

}
