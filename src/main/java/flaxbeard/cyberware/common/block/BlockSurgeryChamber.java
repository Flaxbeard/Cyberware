package flaxbeard.cyberware.common.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemSurgeryChamber;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgeryChamber;

public class BlockSurgeryChamber extends BlockContainer
{
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool OPEN = PropertyBool.create("open");
	public static final PropertyEnum<EnumChamberHalf> HALF = PropertyEnum.<EnumChamberHalf>create("half", EnumChamberHalf.class);
	public final Item ib;
	
	public BlockSurgeryChamber()
	{
		super(Material.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HALF, EnumChamberHalf.LOWER));
		
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		
		String name = "surgeryChamber";
		
		this.setRegistryName(name);
		GameRegistry.register(this);

		ib = new ItemSurgeryChamber(this);
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
		ib.setUnlocalizedName(Cyberware.MODID + "." + name);

		ib.setCreativeTab(Cyberware.creativeTab);
		
		GameRegistry.registerTileEntity(TileEntitySurgeryChamber.class, Cyberware.MODID + ":" + name);
		
		CyberwareContent.items.add(ib);
	}
	
	private static final AxisAlignedBB top = new AxisAlignedBB(0F, 15F / 16F, 0F, 1F, 1F, 1F);
	private static final AxisAlignedBB south = new AxisAlignedBB(0F, 0F, 0F, 1F, 1F, 1F / 16F);
	private static final AxisAlignedBB north = new AxisAlignedBB(0F, 0F, 15F / 16F, 1F, 1F, 1F);
	private static final AxisAlignedBB east = new AxisAlignedBB(0F, 0F, 0F, 1F / 16F, 1F, 1F);
	private static final AxisAlignedBB west = new AxisAlignedBB(15F / 16F, 0F, 0F, 1F, 1F, 1F);
	private static final AxisAlignedBB bottom = new AxisAlignedBB(0F, 0F, 0F, 1F, 1F / 16F, 1F);
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
	{
		EnumFacing face = state.getValue(FACING);
		boolean open = state.getValue(OPEN);
		
		if (state.getValue(HALF) == EnumChamberHalf.UPPER)
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, top);
			if (!open || face != EnumFacing.SOUTH)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, south);
			}
			if (!open || face != EnumFacing.NORTH)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, north);
			}
			if (!open || face != EnumFacing.EAST)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, east);
			}
			if (!open || face != EnumFacing.WEST)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, west);
			}
		}
		else
		{		
			addCollisionBoxToList(pos, entityBox, collidingBoxes, bottom);
			if (!open || face != EnumFacing.SOUTH)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, south);
			}
			if (!open || face != EnumFacing.NORTH)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, north);
			}
			if (!open || face != EnumFacing.EAST)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, east);
			}
			if (!open || face != EnumFacing.WEST)
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, west);
			}
		}
	}
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		boolean top = state.getValue(HALF) == EnumChamberHalf.UPPER;
		if (canOpen(top ? pos : pos.up(), worldIn))
		{
			toggleDoor(top, state, pos, worldIn);
			
			notifySurgeon(top ? pos : pos.up(), worldIn);
		}
		
		return true;
	}
	
	public void toggleDoor(boolean top, IBlockState state, BlockPos pos, World worldIn)
	{
		state = state.cycleProperty(OPEN);
		worldIn.setBlockState(pos, state, 2);
		
		BlockPos otherPos = pos.up();
		if (top)
		{
			otherPos = pos.down();
		}
		IBlockState otherState = worldIn.getBlockState(otherPos);

		if (otherState.getBlock() == this)
		{
			otherState = otherState.cycleProperty(OPEN);
			worldIn.setBlockState(otherPos, otherState, 2);
		}
	}
	
	private boolean canOpen(BlockPos pos, World worldIn)
	{
		TileEntity above = worldIn.getTileEntity(pos.up());
		
		if (above instanceof TileEntitySurgery)
		{
			return ((TileEntitySurgery) above).canOpen();
		}
		return true;
	}
	
	
	private void notifySurgeon(BlockPos pos, World worldIn)
	{
		TileEntity above = worldIn.getTileEntity(pos.up());
		
		if (above instanceof TileEntitySurgery)
		{
			((TileEntitySurgery) above).notifyChange();
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
	{
		if (state.getValue(HALF) == EnumChamberHalf.UPPER)
		{
			BlockPos blockpos = pos.down();
			IBlockState iblockstate = worldIn.getBlockState(blockpos);

			if (iblockstate.getBlock() != this)
			{
				worldIn.setBlockToAir(pos);
			}
			else if (blockIn != this)
			{
				iblockstate.neighborChanged(worldIn, blockpos, blockIn);
			}
		}
		else
		{
			boolean shouldBreak = false;
			BlockPos blockpos1 = pos.up();
			IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

			if (iblockstate1.getBlock() != this)
			{
				worldIn.setBlockToAir(pos);
				if (!worldIn.isRemote)
				{
					this.dropBlockAsItem(worldIn, pos, state, 0);
				}
			}
		}
	}
	
	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return state.getValue(HALF) == EnumChamberHalf.UPPER ? null : this.ib;
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return pos.getY() >= worldIn.getHeight() - 1 ? false : worldIn.getBlockState(pos.down()).isSideSolid(worldIn,  pos.down(), EnumFacing.UP) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
	}
	
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state)
	{
		return EnumPushReaction.DESTROY;
	}
	
	/*public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if (state.getValue(HALF) == EnumChamberHalf.UPPER)
		{
			IBlockState iblockstate1 = worldIn.getBlockState(pos.down());

			if (iblockstate1.getBlock() == this)
			{
				state = state.withProperty(FACING, iblockstate1.getValue(FACING)).withProperty(OPEN, iblockstate1.getValue(OPEN));
			}
		}

		return state;
	}*/
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState()
				.withProperty(HALF, (meta & 1) > 0 ? EnumChamberHalf.UPPER : EnumChamberHalf.LOWER)
				.withProperty(OPEN, (meta & 2) > 0 ? true : false)
				.withProperty(FACING, EnumFacing.getHorizontal(meta >> 2));
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(FACING).getHorizontalIndex() << 2) + (state.getValue(HALF) == EnumChamberHalf.UPPER ? 1 : 0) + (state.getValue(OPEN) ? 2 : 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {HALF, FACING, OPEN});
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		BlockPos blockpos = pos.down();
		BlockPos blockpos1 = pos.up();

		if (player.capabilities.isCreativeMode && state.getValue(HALF) == EnumChamberHalf.UPPER && worldIn.getBlockState(blockpos).getBlock() == this)
		{
			worldIn.setBlockToAir(blockpos);
		}

		if (state.getValue(HALF) == EnumChamberHalf.LOWER && worldIn.getBlockState(blockpos1).getBlock() == this)
		{
			if (player.capabilities.isCreativeMode)
			{
				worldIn.setBlockToAir(pos);
			}

			worldIn.setBlockToAir(blockpos1);
		}
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	
	public static enum EnumChamberHalf implements IStringSerializable
	{
		UPPER,
		LOWER;

		public String toString()
		{
			return this.getName();
		}

		public String getName()
		{
			return this == UPPER ? "upper" : "lower";
		}
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return (meta & 1) > 0 ? new TileEntitySurgeryChamber() : null;
	}
}
