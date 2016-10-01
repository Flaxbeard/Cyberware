package flaxbeard.cyberware.common.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemBlockCyberware;
import flaxbeard.cyberware.common.block.tile.TileEntityRFIDContainer;

public class BlockRFIDContainer extends BlockContainer
{
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");

	public BlockRFIDContainer()
	{
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		
		String name = "rfidContainer";
		
		this.setRegistryName(name);
		GameRegistry.register(this);

		ItemBlock ib = new ItemBlockCyberware(this, new int[] { 0, 1 });
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		this.setCreativeTab(Cyberware.creativeTab);
		GameRegistry.registerTileEntity(TileEntityRFIDContainer.class, Cyberware.MODID + ":" + name);
		
		CyberwareContent.blocks.add(this);
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ENABLED, false));
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(ENABLED, meta > 0);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityRFIDContainer();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		if (stack.hasDisplayName())
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityRFIDContainer)
			{
				((TileEntityRFIDContainer) tileentity).setCustomInventoryName(stack.getDisplayName());
			}
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		boolean enabled = (meta & 1) == 1;
		meta = meta >> 1;
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ENABLED, enabled);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (((EnumFacing)state.getValue(FACING)).getIndex() << 1) + ((state.getValue(ENABLED)) ? 1 : 0);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, ENABLED});
	}
	

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileEntityRFIDContainer)
		{
			((TileEntityRFIDContainer) tileentity).disable();
			/*if (player.isCreative() && player.isSneaking())
			{
				TileEntityScanner scanner = ((TileEntityScanner) tileentity);
				scanner.ticks = CyberwareConfig.SCANNER_TIME - 200;
			}*/
			
			if (state.getValue(ENABLED))
			{
				System.out.println("RE");
				worldIn.setBlockState(pos, state.withProperty(ENABLED, false), 2);
				// TODO
				worldIn.setBlockToAir(pos);
				
				if (worldIn.isRemote)
				{
					spawnNewChest(worldIn, pos, 0);
				}
			}

			//player.openGui(Cyberware.INSTANCE, 4, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
		
	}
	
	private void spawnNewChest(World world, BlockPos pos, int t)
	{
		float direction = world.rand.nextFloat();
		double xComp = Math.sin(direction);
		double zComp = Math.cos(direction);
		int x = (int) (200 * xComp) + pos.getX();
		int z = (int) (200 * zComp) + pos.getZ();
		int y = 254;
		
		while (world.getBlockState(new BlockPos(x, y, z)).getMaterial() != Material.SAND && world.getBlockState(new BlockPos(x, y, z)).getMaterial() != Material.GROUND && world.getBlockState(new BlockPos(x, y, z)).getMaterial() != Material.ROCK && y > 5)
		{
			y--;
		}
		
		if (y <= 5 && t < 25)
		{
			spawnNewChest(world, pos, t + 1);
		}
		else
		{
			int counter = 0;
			while (counter < 10 && !isSpotValid(world, x, y, z))
			{
				counter++;
				y--;
			}
			if (counter >= 10 && t < 25)
			{
				spawnNewChest(world, pos, t + 1);
			}
			else
			{
				world.setBlockState(new BlockPos(x, y, z), this.getDefaultState().withProperty(ENABLED, true));
				System.out.println(x + " " + y + " " + z);

			}
		}
	}

	private boolean isSpotValid(World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		if (world.getBlockState(pos).getMaterial() == Material.SAND || world.getBlockState(pos).getMaterial() == Material.GROUND || world.getBlockState(pos).getMaterial() == Material.ROCK)
		{
			BlockPos pos2 = pos.add(1, 0, 0);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			pos2 = pos.add(-1, 0, 0);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			pos2 = pos.add(0, 1, 0);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			pos2 = pos.add(0, -1, 0);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			pos2 = pos.add(0, 0, 1);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			pos2 = pos.add(0, 0, -1);
			if (!(world.getBlockState(pos2).getMaterial() == Material.SAND || world.getBlockState(pos2).getMaterial() == Material.GROUND || world.getBlockState(pos2).getMaterial() == Material.ROCK)) return false;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{ 
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityRFIDContainer && !worldIn.isRemote)
		{
			TileEntityRFIDContainer scanner = (TileEntityRFIDContainer) tileentity;
			
			for (int i = 0; i < scanner.slots.getSlots(); i++)
			{
				ItemStack stack = scanner.slots.getStackInSlot(i);
				if (stack != null)
				{
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
		super.breakBlock(worldIn, pos, state);

	}
	
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(this);
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}

}
