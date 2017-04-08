package flaxbeard.cyberware.api.progression;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public interface ICyberwareProgressionData
{	
	public NBTTagCompound serializeNBT();
	public void deserializeNBT(NBTTagCompound tag);
	public boolean hasSeen(ItemStack ware);
	public void markSeen(ItemStack ware);

}
