package flaxbeard.cyberware.api.progression;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;

public class CyberwareProgressionDataImpl implements ICyberwareProgressionData
{
	public static final IStorage<ICyberwareProgressionData> STORAGE = new CyberwareProgressionDataStorage();

	private List<ItemStack> discoveredItems = new ArrayList<ItemStack>();

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (ItemStack item : discoveredItems)
		{
			NBTTagCompound itemCompound = new NBTTagCompound();
			item.writeToNBT(itemCompound);
			list.appendTag(itemCompound);
		}
		compound.setTag("discoveredItems", list);
		return compound;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		discoveredItems = new ArrayList<ItemStack>();
		
		NBTTagList list = (NBTTagList) tag.getTag("discoveredItems");
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound itemCompound = list.getCompoundTagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(itemCompound);
			discoveredItems.add(stack);
		}
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagCompound>
	{
		public static final ResourceLocation NAME = new ResourceLocation(Cyberware.MODID, "progression");
		
		private final ICyberwareProgressionData cap = new CyberwareProgressionDataImpl();

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return capability == CyberwareAPI.PROGRESSION_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			if (capability == CyberwareAPI.PROGRESSION_CAPABILITY)
			{
				return CyberwareAPI.PROGRESSION_CAPABILITY.cast(cap);
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

	@Override
	public boolean hasSeen(ItemStack ware)
	{
		for (ItemStack stack : discoveredItems)
		{
			if (stack.getItem() == ware.getItem() && stack.getItemDamage() == ware.getItemDamage())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void markSeen(ItemStack ware)
	{
		ware = ware.copy();
		if (!hasSeen(ware))
		{
			if (ware.hasTagCompound())
			{
				ware.setTagCompound(null);
			}
			ware.stackSize = 1;
			discoveredItems.add(ware);
		}
	}
	
	private static class CyberwareProgressionDataStorage implements IStorage<ICyberwareProgressionData>
	{
		@Override
		public NBTBase writeNBT(Capability<ICyberwareProgressionData> capability, ICyberwareProgressionData instance, EnumFacing side)
		{
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<ICyberwareProgressionData> capability, ICyberwareProgressionData instance, EnumFacing side, NBTBase nbt)
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
}
