package flaxbeard.cyberware.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RFIDRegistry
{
	private static Map<Integer, Map<BlockPos, Integer>> rfidPos = new HashMap<Integer, Map<BlockPos, Integer>>();
	
	public static Map<BlockPos, Integer> positionsForDimension(World world)
	{
		return positionsForDimension(world.provider.getDimension());
	}
	
	public static Map<BlockPos, Integer> positionsForDimension(int dimensionId)
	{
		return rfidPos.get(dimensionId);
	}
	
	public static void remove(World world, BlockPos pos)
	{
		remove(world.provider.getDimension(), pos);
	}
	
	public static void remove(int dimensionId, BlockPos pos)
	{
		if (!rfidPos.containsKey(dimensionId))
		{
			return;
		}
		rfidPos.get(dimensionId).remove(pos);
		if (rfidPos.get(dimensionId).size() == 0)
		{
			rfidPos.remove(dimensionId);
		}
	}
	
	public static void add(World world, BlockPos pos, int range)
	{
		add(world.provider.getDimension(), pos, range);
	}
	
	public static void add(int dimensionId, BlockPos pos, int range)
	{
		if (!rfidPos.containsKey(dimensionId))
		{
			rfidPos.put(dimensionId, new HashMap<BlockPos, Integer>());
		}
		rfidPos.get(dimensionId).put(pos, range);
	}
	
	public static BlockPos getSignal(World world, double posX, double posY, double posZ)
	{
		return getSignal(world.provider.getDimension(), posX, posY, posZ);
	}
	
	public static BlockPos getSignal(int dimensionId, double posX, double posY, double posZ)
	{
		if (!rfidPos.containsKey(dimensionId))
		{
			return null;
		}
		
		Map<BlockPos, Integer> map = rfidPos.get(dimensionId);
		
		double minDist = Double.MAX_VALUE;
		BlockPos pos = null;
		for (Entry<BlockPos, Integer> entry : map.entrySet())
		{
			BlockPos pos2 = entry.getKey();
			double dist = Math.sqrt((posX - pos2.getX()) * (posX - pos2.getX()) + (posY - pos2.getY()) * (posY - pos2.getY()) + (posZ - pos2.getZ()) * (posZ - pos2.getZ()));
			
			if (dist < entry.getValue() && dist < minDist)
			{
				pos = pos2;
				minDist = dist;
			}
		}
		
		return pos;
	}
	
	public static double getDist(World world, double posX, double posY, double posZ, BlockPos signal)
	{
		return getDist(world.provider.getDimension(), posX, posY, posZ, signal);
	}
	
	public static double getDist(int dimensionId, double posX, double posY, double posZ, BlockPos signal)
	{
		if (!rfidPos.containsKey(dimensionId) || !rfidPos.get(dimensionId).containsKey(signal))
		{
			return -1;
		}
		
		int max = rfidPos.get(dimensionId).get(signal);
		double dist = Math.sqrt((posX - signal.getX()) * (posX - signal.getX()) + (posY - signal.getY()) * (posY - signal.getY()) + (posZ - signal.getZ()) * (posZ - signal.getZ()));
		return (dist < max) ? dist : -2;
	}
}
