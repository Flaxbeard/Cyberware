package flaxbeard.cyberware.api.progression;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;

public interface ICyberwareProgressionData
{	
	public NBTTagCompound serializeNBT();
	public void deserializeNBT(NBTTagCompound tag);
}
