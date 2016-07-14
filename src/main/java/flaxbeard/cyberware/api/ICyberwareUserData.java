package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;

public interface ICyberwareUserData
{	
	public ItemStack[] getInstalledCyberware(EnumSlot slot);
	public void setInstalledCyberware(EnumSlot slot, List<ItemStack> cyberware);
	public void setInstalledCyberware(EnumSlot slot, ItemStack[] cyberware);
	public boolean isCyberwareInstalled(ItemStack cyberware);
	public int getCyberwareRank(ItemStack cyberware);
	
	public NBTTagCompound serializeNBT();
	public void deserializeNBT(NBTTagCompound tag);
	
	
	public boolean hasEssential(EnumSlot slot);
	public void setHasEssential(EnumSlot slot, boolean has);
	public ItemStack getCyberware(ItemStack cyberware);
}
