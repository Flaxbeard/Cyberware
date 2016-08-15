package flaxbeard.cyberware.common.block.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import flaxbeard.cyberware.common.lib.LibConstants;

public class TileEntityBeacon extends TileEntity implements ITickable
{
	public static Map<Integer, Map<BlockPos, Integer>> beaconPos = new HashMap<Integer, Map<BlockPos, Integer>>();
	public boolean initialized = false;
	
	@Override
	public void update()
	{
		if (!initialized)
		{
			this.initialize();
		}
	}
	
	private void initialize()
	{
		Map<BlockPos, Integer> map = beaconPos.get(worldObj.provider.getDimension());
		if (map == null)
		{
			beaconPos.put(worldObj.provider.getDimension(), new HashMap<BlockPos, Integer>());
			map = beaconPos.get(worldObj.provider.getDimension());
		}
		if (!map.containsKey(this.getPos()))
		{
			map.put(this.getPos(), LibConstants.BEACON_RANGE);
		}
	}
	
	@Override
	public void invalidate()
	{
		Map<BlockPos, Integer> map = beaconPos.get(worldObj.provider.getDimension());
		if (map == null)
		{
			beaconPos.put(worldObj.provider.getDimension(), new HashMap<BlockPos, Integer>());
			map = beaconPos.get(worldObj.provider.getDimension());
		}
		if (map.containsKey(this.getPos()))
		{
			map.remove(this.getPos());
		}
	
		super.invalidate();
	}
	
	public static boolean isInRange(World world, double posX, double posY, double posZ)
	{
		Map<BlockPos, Integer> map = beaconPos.get(world.provider.getDimension());
		if (map == null)
		{
			beaconPos.put(world.provider.getDimension(), new HashMap<BlockPos, Integer>());
			map = beaconPos.get(world.provider.getDimension());
		}
		
		for (Entry<BlockPos, Integer> entry : map.entrySet())
		{
			float distance = (float) Math.sqrt((posX - entry.getKey().getX()) * (posX - entry.getKey().getX()) + (posZ - entry.getKey().getZ()) * (posZ - entry.getKey().getZ()));
			if (distance < entry.getValue())
			{
				return true;
			}
		}
		
		return false;
	}
}
