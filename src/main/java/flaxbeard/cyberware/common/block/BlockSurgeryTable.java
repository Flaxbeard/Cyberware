package flaxbeard.cyberware.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;

public class BlockSurgeryTable extends BlockBed
{
	
	public BlockSurgeryTable()
	{
		
		String name = "surgeryTable";
		
		this.setRegistryName(name);
		GameRegistry.register(this);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		GameRegistry.registerTileEntity(TileEntitySurgery.class, Cyberware.MODID + ":" + name);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return true;
		}
		else
		{
			if (state.getValue(PART) != BlockBed.EnumPartType.HEAD)
			{
				pos = pos.offset((EnumFacing)state.getValue(FACING));
				state = worldIn.getBlockState(pos);

				if (state.getBlock() != this)
				{
					return true;
				}
			}

			if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(pos) != Biomes.HELL)
			{
				if (((Boolean)state.getValue(OCCUPIED)).booleanValue())
				{
					EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

					if (entityplayer != null)
					{
						playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.occupied", new Object[0]));
						return true;
					}

					state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
					worldIn.setBlockState(pos, state, 4);
				}

				EntityPlayer.SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);

				if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
				{
					state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
					worldIn.setBlockState(pos, state, 4);
					return true;
				}
				else
				{
					if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
					{
						playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.noSleep", new Object[0]));
					}
					else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
					{
						playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.notSafe", new Object[0]));
					}

					return true;
				}
			}
			else
			{
				worldIn.setBlockToAir(pos);
				BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());

				if (worldIn.getBlockState(blockpos).getBlock() == this)
				{
					worldIn.setBlockToAir(blockpos);
				}

				worldIn.newExplosion((Entity)null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, true);
				return true;
			}
		}
	}

/*	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : CyberwareContent.surgeryTableItem;
	}

	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(CyberwareContent.surgeryTableItem);
	}*/

	@Nullable
	private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos)
	{
		for (EntityPlayer entityplayer : worldIn.playerEntities)
		{
			if (entityplayer.isPlayerSleeping() && entityplayer.playerLocation.equals(pos))
			{
				return entityplayer;
			}
		}

		return null;
	}
	
	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player)
	{
		return true;
	}
	
	@SubscribeEvent
	public void handleSleep(PlayerSleepInBedEvent event)
	{

	}
	
	private void setRenderOffsetForSleep(EntityPlayer player, EnumFacing p_175139_1_)
    {
		player.renderOffsetX = 0.0F;
		player.renderOffsetZ = 0.0F;

        switch (p_175139_1_)
        {
            case SOUTH:
            	player.renderOffsetZ = -1.8F;
                break;
            case NORTH:
            	player.renderOffsetZ = 1.8F;
                break;
            case WEST:
            	player.renderOffsetX = 1.8F;
                break;
            case EAST:
            	player.renderOffsetX = -1.8F;
        }
    }

}
