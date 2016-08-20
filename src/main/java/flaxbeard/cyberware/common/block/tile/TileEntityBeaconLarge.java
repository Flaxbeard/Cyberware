package flaxbeard.cyberware.common.block.tile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockBeaconLarge;
import flaxbeard.cyberware.common.block.BlockBeaconPost;
import flaxbeard.cyberware.common.lib.LibConstants;

public class TileEntityBeaconLarge extends TileEntityBeacon implements ITickable
{
	private boolean wasWorking = false;
	private int count = 0;
	
	private static int TIER = 3;
	
	@Override
	public void update()
	{
		IBlockState master = worldObj.getBlockState(pos.add(0, -10, 0));
		
		boolean powered = worldObj.isBlockPowered(pos.add(1, -10, 0))
				|| worldObj.isBlockPowered(pos.add(-1, -10, 0))
				|| worldObj.isBlockPowered(pos.add(0, -10, 1))
				|| worldObj.isBlockPowered(pos.add(0, -10, -1));
		boolean working = !powered && master.getBlock() == CyberwareContent.radioPost && master.getValue(BlockBeaconPost.TRANSFORMED) == 2;
		
		if (!wasWorking && working)
		{
			this.enable();
		}
		
		if (wasWorking && !working)
		{
			disable();
		}
		
		wasWorking = working;
		
		if (worldObj.isRemote && working)
		{
			count = (count + 1) % 20;
			if (count == 0)
			{
				IBlockState state = worldObj.getBlockState(pos);
				if (state.getBlock() == CyberwareContent.radioLarge)
				{
					boolean ns = state.getValue(BlockBeaconLarge.FACING) == EnumFacing.EAST || state.getValue(BlockBeaconLarge.FACING) == EnumFacing.WEST;
					float dist = .5F;
					float speedMod = .2F;
					int degrees = 45;
					for (int i = 0; i < 18; i++)
					{
						float sin = (float) Math.sin(Math.toRadians(degrees));
						float cos = (float) Math.cos(Math.toRadians(degrees));
						float xOffset = dist * sin;
						float yOffset = .2F + dist * cos;
						float xSpeed = speedMod * sin;
						float ySpeed = speedMod * cos;
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
								pos.getX() + .5F + (ns ? xOffset : 0), 
								pos.getY() + .5F + yOffset, 
								pos.getZ() + .5F + (ns ? 0 : xOffset), 
								ns ? xSpeed : 0, 
								ySpeed, 
								ns ? 0 : xSpeed,
								new int[] {255, 255, 255});
						
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
								pos.getX() + .5F - (ns ? xOffset : 0), 
								pos.getY() + .5F + yOffset, 
								pos.getZ() + .5F - (ns ? 0 : xOffset), 
								ns ? -xSpeed : 0, 
								ySpeed, 
								ns ? 0 : -xSpeed,
								new int[] {255, 255, 255});
	
						degrees += 5;
					}
				}
			}
		}
	}

	
	private void disable()
	{
		Map<BlockPos, Integer> map = posForTier(TIER).get(worldObj.provider.getDimension());
		if (map == null)
		{
			posForTier(TIER).put(worldObj.provider.getDimension(), new HashMap<BlockPos, Integer>());
			map = posForTier(TIER).get(worldObj.provider.getDimension());
		}
		if (map.containsKey(this.getPos()))
		{
			map.remove(this.getPos());
		}
	
	}

	private void enable()
	{
		Map<BlockPos, Integer> map = posForTier(TIER).get(worldObj.provider.getDimension());
		if (map == null)
		{
			posForTier(TIER).put(worldObj.provider.getDimension(), new HashMap<BlockPos, Integer>());
			map = posForTier(TIER).get(worldObj.provider.getDimension());
		}
		if (!map.containsKey(this.getPos()))
		{
			map.put(this.getPos(), LibConstants.LARGE_BEACON_RANGE);
		}
	}
	
	@Override
	public void invalidate()
	{
		disable();
		super.invalidate();
	}
}
