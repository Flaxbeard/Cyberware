package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;

public interface ICyberwareUserData
{	
	public ItemStack[] getInstalledCyberware(EnumSlot slot);
	public void setInstalledCyberware(EntityLivingBase entity, EnumSlot slot, List<ItemStack> cyberware);
	public void setInstalledCyberware(EntityLivingBase entity, EnumSlot slot, ItemStack[] cyberware);
	public boolean isCyberwareInstalled(ItemStack cyberware);
	public int getCyberwareRank(ItemStack cyberware);
	
	public NBTTagCompound serializeNBT();
	public void deserializeNBT(NBTTagCompound tag);
	
	
	public boolean hasEssential(EnumSlot slot);
	public void setHasEssential(EnumSlot slot, boolean hasLeft, boolean hasRight);
	public ItemStack getCyberware(ItemStack cyberware);
	public void updateCapacity();
	public void resetBuffer();
	public void addPower(int amount, ItemStack inputter);
	public boolean isAtCapacity(ItemStack stack);
	public boolean isAtCapacity(ItemStack stack, int buffer);
	public float getPercentFull();
	public int getCapacity();
	public int getStoredPower();
	public boolean usePower(ItemStack stack, int amount);
	public List<ItemStack> getPowerOutages();
	public List<Integer> getPowerOutageTimes();
	public void setImmune();
	public boolean usePower(ItemStack stack, int amount, boolean isPassive);
	public boolean hasEssential(EnumSlot slot, EnumSide side);
	public int getEssence();
	public void setEssence(int e);
	void resetWare(EntityLivingBase e);
	int getMaxEssence();
	public int getNumActiveItems();
	public List<ItemStack> getActiveItems();
	public void removeHotkey(int i);
	public void addHotkey(int i, ItemStack stack);
	public ItemStack getHotkey(int i);
	public Iterable<Integer> getHotkeys();
}
