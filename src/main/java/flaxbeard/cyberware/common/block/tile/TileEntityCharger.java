package flaxbeard.cyberware.common.block.tile;

import java.util.List;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;
import cofh.api.energy.IEnergyReceiver;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareConfig;

@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI|energy")
public class TileEntityCharger extends TileEntity implements ITickable, IEnergyReceiver
{
	private PowerContainer container = new PowerContainer();
	
	@CapabilityInject(ITeslaConsumer.class)
	private static Capability TESLA_CONSUMER;
	@CapabilityInject(ITeslaProducer.class)
	private static Capability TESLA_PRODUCER;
	@CapabilityInject(ITeslaHolder.class)
	private static Capability TESLA_HOLDER;
	
	private boolean last = false;

	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		container.deserializeNBT(compound.getCompoundTag("power"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag("power", container.serializeNBT());
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
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_HOLDER)
		{
			return (T) this.container;
		}
			
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_HOLDER)
		{
			return true;
		}
			
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public void update()
	{

		List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 2.5F, pos.getZ() + 1F));
		for (EntityLivingBase entity : entities)
		{
			
			if (CyberwareAPI.hasCapability(entity))
			{
				
				ICyberwareUserData data = CyberwareAPI.getCapability(entity);
				
				if(!data.isAtCapacity(null, 20) && (container.getStoredPower() >= CyberwareConfig.TESLA_PER_POWER))
				{
					container.takePower(CyberwareConfig.TESLA_PER_POWER, false);
					data.addPower(20, null);
					
					if (entity.ticksExisted % 5 == 0)
					{
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F, 0F, .05F, 0F, new int[] { 255, 150, 255 } );
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F, .04F, .05F, .04F, new int[] { 255, 150, 255 } );
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F, -.04F, .05F, .04F, new int[] { 255, 150, 255 } );
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F, .04F, .05F, -.04F, new int[] { 255, 150, 255 } );
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F, -.04F, .05F, -.04F, new int[] { 255, 150, 255 } );

					}

				}
			}
		}
		
		boolean hasPower = (container.getStoredPower() >= CyberwareConfig.TESLA_PER_POWER);
		if (hasPower != last && !worldObj.isRemote)
		{
			IBlockState state = worldObj.getBlockState(getPos());
			worldObj.notifyBlockUpdate(pos, state, state, 2);
			last = hasPower;
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return (int) this.container.getStoredPower();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return (int) this.container.getCapacity();
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return (int) this.container.givePower(maxReceive, simulate);
	}
}
