package flaxbeard.cyberware.common.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemBlockCyberware;
import flaxbeard.cyberware.common.block.tile.TileEntityBeaconPost;
import flaxbeard.cyberware.common.block.tile.TileEntityBeaconPost.TileEntityBeaconPostMaster;

public class BlockBeaconPost extends BlockContainer
{
	/** Whether this fence connects in the northern direction */
	public static final PropertyBool NORTH = PropertyBool.create("north");
	/** Whether this fence connects in the eastern direction */
	public static final PropertyBool EAST = PropertyBool.create("east");
	/** Whether this fence connects in the southern direction */
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	/** Whether this fence connects in the western direction */
	public static final PropertyBool WEST = PropertyBool.create("west");
	
	public static final PropertyInteger TRANSFORMED = PropertyInteger.create("transformed", 0, 2);

	
	protected static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[] {new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	public static final AxisAlignedBB PILLAR_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1D, 0.625D);
	public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.625D, 0.625D, 1D, 1.0D);
	public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.375D, 1D, 0.625D);
	public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1D, 0.375D);
	public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.625D, 0.0D, 0.375D, 1.0D, 1D, 0.625D);

	public BlockBeaconPost()
	{
		super(Material.IRON);
		
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		
		String name = "radioPost";
		
		this.setRegistryName(name);
		GameRegistry.register(this);
		

		ItemBlock ib = new ItemBlockCyberware(this);
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		this.setCreativeTab(Cyberware.creativeTab);
		
		CyberwareContent.blocks.add(this);
		
		GameRegistry.registerTileEntity(TileEntityBeaconPost.class, Cyberware.MODID + ":" + name);
		GameRegistry.registerTileEntity(TileEntityBeaconPostMaster.class, Cyberware.MODID + ":" + name + "_master");

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(TRANSFORMED, 0)
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false)));
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		BlockPos complete = complete(worldIn, pos);
		
		if (complete != null)
		{
			
		}
	}

	private BlockPos complete(World world, BlockPos pos)
	{
		for (int y = -9; y <= 0; y++)
		{
			for (int x = -1; x <= 1; x++)
			{
				for (int z = -1; z <= 1; z++)
				{
					BlockPos start = pos.add(x, y, z);
					
					BlockPos result = complete(world, pos, start);
					if (result != null)
					{
						return result;
					}
				}
			}
		}
		
		return null;
	}
	
	private BlockPos complete(World world, BlockPos pos, BlockPos start)
	{
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
					
					BlockPos newPos = start.add(x, y, z);
					
					IBlockState state = world.getBlockState(newPos);
					Block block = state.getBlock();
					if (block != this || state.getValue(TRANSFORMED) != 0)
					{
						return null;
					}
				}
			}
		}
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
					
					BlockPos newPos = start.add(x, y, z);
					
	
					if (newPos.equals(start))
					{
						world.setBlockState(newPos, world.getBlockState(newPos).withProperty(TRANSFORMED, 2), 2);
						
						TileEntityBeaconPost post = (TileEntityBeaconPost) world.getTileEntity(newPos);
					}
					else
					{
						world.setBlockState(newPos, world.getBlockState(newPos).withProperty(TRANSFORMED, 1), 2);
						
						TileEntityBeaconPost post = (TileEntityBeaconPost) world.getTileEntity(newPos);
						post.setMasterLoc(start);
					}
				}
			}
		}
		return start;
	}

	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
	{
		state = state.getActualState(worldIn, pos);
		
		if (state.getValue(TRANSFORMED) > 0)
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state, worldIn, pos));
			return;
		}
		
		addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR_AABB);

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		}
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		state = this.getActualState(state, source, pos);
		return BOUNDING_BOXES[getBoundingBoxIdx(state)];
	}

	/**
	 * Returns the correct index into boundingBoxes, based on what the fence is connected to.
	 */
	private static int getBoundingBoxIdx(IBlockState state)
	{
		int i = 0;

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			i |= 1 << EnumFacing.EAST.getHorizontalIndex();
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			i |= 1 << EnumFacing.WEST.getHorizontalIndex();
		}

		return i;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return false;
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block == Blocks.BARRIER ? false : ((!(block instanceof BlockBeaconPost) || block.getMaterial(iblockstate) != this.blockMaterial) && !(block instanceof BlockFenceGate) ? (block.getMaterial(iblockstate).isOpaque() && iblockstate.isFullCube() ? block.getMaterial(iblockstate) != Material.GOURD : false) : true);
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return worldIn.isRemote ? true : ItemLead.attachToFence(playerIn, worldIn, pos);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TRANSFORMED);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(TRANSFORMED, meta);
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north()))).withProperty(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east()))).withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south()))).withProperty(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())));
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		switch (rot)
		{
			case CLOCKWISE_180:
				return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90:
				return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
			case CLOCKWISE_90:
				return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
			default:
				return state;
		}
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		switch (mirrorIn)
		{
			case LEFT_RIGHT:
				return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
			case FRONT_BACK:
				return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
			default:
				return super.withMirror(state, mirrorIn);
		}
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, TRANSFORMED});
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return state.getValue(TRANSFORMED) > 0 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		switch (meta)
		{
			case 2:
				return new TileEntityBeaconPostMaster();
			case 1:
				return new TileEntityBeaconPost();
			default:
				return null;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(world != null && state.getValue(TRANSFORMED) > 0)
		{

			TileEntity te = world.getTileEntity(pos);
			if (te != null && te instanceof TileEntityBeaconPost)
			{
				TileEntityBeaconPost post = (TileEntityBeaconPost) te;
				if (state.getValue(TRANSFORMED) == 2)
				{
					System.out.println("DESTRUCTING");
					post.destruct();
				}
				else if (post.master != null && !post.master.equals(pos) && !post.destructing)
				{
					TileEntity masterTe = world.getTileEntity(post.master);
					
					if (masterTe != null && masterTe instanceof TileEntityBeaconPost)
					{
						TileEntityBeaconPost post2 = (TileEntityBeaconPost) masterTe;
						
						if (post2 instanceof TileEntityBeaconPostMaster && !post2.destructing)
						{
							post2.destruct();
						}
					}
				}
			}
		}
	}

	
}