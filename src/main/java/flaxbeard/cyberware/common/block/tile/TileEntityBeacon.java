package flaxbeard.cyberware.common.block.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockBeaconLarge;
import flaxbeard.cyberware.common.lib.LibConstants;

public class TileEntityBeacon extends TileEntity implements ITickable
{
	private static List<Integer> tiers = new ArrayList<Integer>();
	private static Map<Integer, Map<Integer, Map<BlockPos, Integer>>> beaconPos = new HashMap<Integer, Map<Integer, Map<BlockPos, Integer>>>();
	public boolean initialized = false;
	private boolean wasWorking = false;
	private int count = 0;
	
	private static int TIER = 2;
	
	public static Map<Integer, Map<BlockPos, Integer>> posForTier(int tier)
	{
		Map<Integer, Map<BlockPos, Integer>> map = beaconPos.get(tier);

		if (map == null)
		{
			beaconPos.put(tier, new HashMap<Integer, Map<BlockPos, Integer>>());
			map = beaconPos.get(tier);
			tiers.add(tier);
			Collections.sort(tiers);
			Collections.reverse(tiers);
		}
		
		return map;
	}
	
	@Override
	public void update()
	{			
		boolean working = !worldObj.isBlockPowered(pos);
		
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
				if (state.getBlock() == CyberwareContent.radio)
				{
					boolean ns = state.getValue(BlockBeaconLarge.FACING) == EnumFacing.NORTH || state.getValue(BlockBeaconLarge.FACING) == EnumFacing.SOUTH;
					boolean backwards = state.getValue(BlockBeaconLarge.FACING) == EnumFacing.SOUTH || state.getValue(BlockBeaconLarge.FACING) == EnumFacing.EAST;
					float dist = .2F;
					float speedMod = .08F;
					int degrees = 45;
					for (int i = 0; i < 5; i++)
					{
						float sin = (float) Math.sin(Math.toRadians(degrees));
						float cos = (float) Math.cos(Math.toRadians(degrees));
						float xOffset = dist * sin;
						float yOffset = .2F + dist * cos;
						float xSpeed = speedMod * sin;
						float ySpeed = speedMod * cos;
						float backOffsetX = (backwards ^ ns ? -.3F : .3F);
						float backOffsetZ = (backwards ? .4F : -.4F);

						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
								pos.getX() + .5F + (ns ? xOffset + backOffsetX : backOffsetZ), 
								pos.getY() + .5F + yOffset, 
								pos.getZ() + .5F + (ns ? backOffsetZ : xOffset + backOffsetX), 
								ns ? xSpeed : 0, 
								ySpeed, 
								ns ? 0 : xSpeed,
								new int[] {255, 255, 255});
						
						worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
								pos.getX() + .5F + (ns ? -xOffset + backOffsetX : backOffsetZ), 
								pos.getY() + .5F + yOffset, 
								pos.getZ() + .5F + (ns ? backOffsetZ : -xOffset + backOffsetX), 
								ns ? -xSpeed : 0, 
								ySpeed, 
								ns ? 0 : -xSpeed,
								new int[] {255, 255, 255});
	
						degrees += 18;
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
			map.put(this.getPos(), LibConstants.BEACON_RANGE);
		}
	}
	
	@Override
	public void invalidate()
	{
		disable();
		super.invalidate();
	}
	
	public static int isInRange(World world, double posX, double posY, double posZ)
	{
		for (int tier : tiers)
		{
			Map<BlockPos, Integer> map = posForTier(tier).get(world.provider.getDimension());
			if (map == null)
			{
				posForTier(tier).put(world.provider.getDimension(), new HashMap<BlockPos, Integer>());
				map = posForTier(tier).get(world.provider.getDimension());
			}
			
			for (Entry<BlockPos, Integer> entry : map.entrySet())
			{
				float distance = (float) Math.sqrt((posX - entry.getKey().getX()) * (posX - entry.getKey().getX()) + (posZ - entry.getKey().getZ()) * (posZ - entry.getKey().getZ()));
				if (distance < entry.getValue())
				{
					return tier;
				}
			}
		}
		
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityPlayer.class,
				new AxisAlignedBB(posX - LibConstants.BEACON_RANGE_INTERNAL, 0, posZ - LibConstants.BEACON_RANGE_INTERNAL, posX + LibConstants.BEACON_RANGE_INTERNAL, 255, posZ + LibConstants.BEACON_RANGE_INTERNAL));
		
		ItemStack test = new ItemStack(CyberwareContent.brainUpgrades, 1, 5);
		for (EntityLivingBase entity : entities)
		{
			if (CyberwareAPI.hasCapability(entity))
			{
				if (CyberwareAPI.isCyberwareInstalled(entity, test))
				{
					if (EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(entity, test)))
					{
						return 1;
					}
				}
			}
		}
				
		
		return -1;
	}
}
