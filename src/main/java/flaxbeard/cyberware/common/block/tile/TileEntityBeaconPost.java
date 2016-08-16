package flaxbeard.cyberware.common.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockBeaconPost;

public class TileEntityBeaconPost extends TileEntity
{
	public static class TileEntityBeaconPostMaster extends TileEntityBeaconPost
	{
		@SideOnly(Side.CLIENT)
		@Override
		public AxisAlignedBB getRenderBoundingBox()
		{
			return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 10, pos.getZ() + 1);
		}
		
		
		@Override
		public void setMasterLoc(BlockPos start)
		{
			throw new IllegalStateException("NO");
		}
	}
	
	public BlockPos master = null;
	public boolean destructing = false;


	public void setMasterLoc(BlockPos start)
	{
		System.out.println("SET MASTER LOC");
		this.master = start;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 2);
		this.markDirty();
	}
	
	@Override
	public void invalidate()
	{
		
		super.invalidate();

	}

	public void destruct()
	{
		if (!destructing)
		{
			destructing = true;
			for (int y = 0; y <= 9; y++)
			{
				for (int x = -1; x <= 1; x++)
				{
					for (int z = -1; z <= 1; z++)
					{
						if (y > 3 && (x != 0 && z != 0))
						{
							continue;
						}
						
						if (y > 4 && (x != 0 || z != 0))
						{
							continue;
						}
						
						BlockPos newPos = pos.add(x, y, z);
			
						IBlockState state = worldObj.getBlockState(newPos);
						Block block = state.getBlock();
						if (block == CyberwareContent.radioPost && state.getValue(BlockBeaconPost.TRANSFORMED) > 0)
						{
							TileEntityBeaconPost bp = (TileEntityBeaconPost) worldObj.getTileEntity(newPos);
	
							worldObj.setBlockState(newPos, state.withProperty(BlockBeaconPost.TRANSFORMED, 0), 2);
							
						}
					
					}
				}
			}
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if (!(this instanceof TileEntityBeaconPostMaster))
		{
			int x = compound.getInteger("x");
			int y = compound.getInteger("y");
			int z = compound.getInteger("z");
			this.master = new BlockPos(x, y, z);
		}
		
		System.out.println(master);
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
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		
		if (!(this instanceof TileEntityBeaconPostMaster))
		{
			compound.setInteger("x", master.getX());
			compound.setInteger("y", master.getY());
			compound.setInteger("z", master.getZ());
			System.out.println(master);

		}
				
		return compound;
		
	}
	

}
