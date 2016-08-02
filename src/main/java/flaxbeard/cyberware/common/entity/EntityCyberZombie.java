package flaxbeard.cyberware.common.entity;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;

public class EntityCyberZombie extends EntityZombie
{
	
	public boolean hasWare;
	private CyberwareUserDataImpl cyberware;
	
	public EntityCyberZombie(World worldIn)
	{
		super(worldIn);
		cyberware = new CyberwareUserDataImpl();
		hasWare = false;
		//CyberwareDataHandler.addRandomCyberware(this);
	}
	
	@Override
	public boolean isVillager()
	{
		return false;
	}
	
	@Override
	public void onLivingUpdate()
	{
		if (!this.hasWare && !this.worldObj.isRemote)
		{
			CyberwareDataHandler.addRandomCyberware(this);
			hasWare = true;
		}
		super.onLivingUpdate();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		compound.setBoolean("hasRandomWare", hasWare);

		if (hasWare)
		{
			NBTTagCompound comp = cyberware.serializeNBT();
			compound.setTag("ware", comp);
		}
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		this.hasWare = compound.getBoolean("hasRandomWare");
		if (compound.hasKey("ware"))
		{
			cyberware.deserializeNBT(compound.getCompoundTag("ware"));
		}
	}
	
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
	{

		if (capability == CyberwareAPI.CYBERWARE_CAPABILITY)
		{
			return (T) cyberware;
		}
		System.out.println(capability);
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
	{
		return capability == CyberwareAPI.CYBERWARE_CAPABILITY || super.hasCapability(capability, facing);
	}
}
