package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;

public interface ICyberwareUserData
{	
	public NBTTagCompound serializeNBT();
	public void deserializeNBT(NBTTagCompound tag);
	
	public ItemStack[] getInstalledCyberware(EnumSlot slot);
	public boolean isCyberwareInstalled(ItemStack cyberware);
	public ItemStack getCyberware(ItemStack cyberware);
	public ItemStack getCyberwareInSlot(ItemStack cyberware, EnumSlot slot);
	public int getCyberwareRank(ItemStack cyberware);
	public ItemStack getLimb(EnumSlot arm, EnumSide left);
	public void setInstalledCyberware(EntityLivingBase entity, EnumSlot slot, List<ItemStack> cyberware);
	public void setInstalledCyberware(EntityLivingBase entity, EnumSlot slot, ItemStack[] cyberware);

	public int getCapacity();
	public int getStoredPower();
	public int getProduction();
	public int getConsumption();
	public float getPercentFull();
	
	public void updateCapacity();
	public void resetBuffer();
	public void setImmune();
	
	public void addPower(int amount, ItemStack inputter);
	public boolean usePower(ItemStack stack, int amount);
	public boolean usePower(ItemStack stack, int amount, boolean isPassive);
	
	public List<ItemStack> getPowerOutages();
	public List<Integer> getPowerOutageTimes();
	
	public boolean isAtCapacity(ItemStack stack);
	public boolean isAtCapacity(ItemStack stack, int buffer);

	public boolean hasEssential(EnumSlot slot);
	public void setHasEssential(EnumSlot slot, boolean hasEssential);
		
	public void resetWare(EntityLivingBase e);
	
	public int getMaxTolerance(EntityLivingBase e);
	public void setTolerance(EntityLivingBase e, int amnt);
	public int getTolerance(EntityLivingBase e);
	
	public boolean hasOpenedRadialMenu();
	public void setOpenedRadialMenu(boolean hasOpenedRadialMenu);
	
	public int getNumActiveItems();
	public List<ItemStack> getActiveItems();
	
	public void removeHotkey(int i);
	public void addHotkey(int i, ItemStack stack);
	public ItemStack getHotkey(int i);
	public Iterable<Integer> getHotkeys();
	
	public List<ItemStack> getHudjackItems();
	
	public NBTTagCompound getHudData();
	public void setHudData(NBTTagCompound comp);

	public void setHudColor(int color);
	public void setHudColor(float[] color);
	public int getHudColorHex();
	public float[] getHudColor();

	@Deprecated
	public int getEssence();
	@Deprecated
	public void setEssence(int e);
	@Deprecated
	public int getMaxEssence();
	
	@Deprecated
	public boolean hasEssential(EnumSlot slot, EnumSide side);
	@Deprecated
	public void setHasEssential(EnumSlot slot, boolean hasLeft, boolean hasRight);
}
