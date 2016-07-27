package flaxbeard.cyberware.common.block.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;

public class ItemSurgeryTable extends Item
{
	public ItemSurgeryTable()
	{
		String name = "surgeryTable";
		
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
        this.setMaxDamage(0);
        
		this.setCreativeTab(Cyberware.creativeTab);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	/*public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}
		else if (facing != EnumFacing.UP)
		{
			return EnumActionResult.FAIL;
		}
		else
		{
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();
			boolean flag = block.isReplaceable(worldIn, pos);

			if (!flag)
			{
				pos = pos.up();
			}

			int i = MathHelper.floor_double((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing enumfacing = EnumFacing.getHorizontal(i);
			BlockPos blockpos = pos.offset(enumfacing);

			if (playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(blockpos, facing, stack))
			{
				boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
				boolean flag2 = flag || worldIn.isAirBlock(pos);
				boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

				if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isFullyOpaque() && worldIn.getBlockState(blockpos.down()).isFullyOpaque())
				{
					IBlockState iblockstate1 = CyberwareContent.surgeryTable.getDefaultState().withProperty(BlockSurgeryTable.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockSurgeryTable.FACING, enumfacing).withProperty(BlockSurgeryTable.PART, BlockBed.EnumPartType.FOOT);

					if (worldIn.setBlockState(pos, iblockstate1, 11))
					{
						IBlockState iblockstate2 = iblockstate1.withProperty(BlockSurgeryTable.PART, BlockBed.EnumPartType.HEAD);
						worldIn.setBlockState(blockpos, iblockstate2, 11);
					}

					SoundType soundtype = iblockstate1.getBlock().getSoundType();
					worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					--stack.stackSize;
					return EnumActionResult.SUCCESS;
				}
				else
				{
					return EnumActionResult.FAIL;
				}
			}
			else
			{
				return EnumActionResult.FAIL;
			}
		}
	}*/
}