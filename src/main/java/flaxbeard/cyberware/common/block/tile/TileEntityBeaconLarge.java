package flaxbeard.cyberware.common.block.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import flaxbeard.cyberware.common.lib.LibConstants;

public class TileEntityBeaconLarge extends TileEntityBeacon implements ITickable
{
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
			map.put(this.getPos(), LibConstants.LARGE_BEACON_RANGE);
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
}
