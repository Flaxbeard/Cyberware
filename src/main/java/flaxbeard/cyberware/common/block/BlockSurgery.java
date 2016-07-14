package flaxbeard.cyberware.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemBlockCyberware;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.lib.LibConstants;

public class BlockSurgery extends BlockContainer
{

	public BlockSurgery()
	{
		super(Material.IRON);
		
		String name = "surgery";
		
		this.setRegistryName(name);
		GameRegistry.register(this);

		ItemBlock ib = new ItemBlockCyberware(this);
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		this.setCreativeTab(Cyberware.creativeTab);
		GameRegistry.registerTileEntity(TileEntitySurgery.class, Cyberware.MODID + ":" + name);
		
        CyberwareContent.blocks.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySurgery();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		
		if (tileentity instanceof TileEntitySurgery)
		{
			TileEntitySurgery surgery = (TileEntitySurgery) tileentity;
			if (player.isSneaking())
			{
				if (CyberwareAPI.hasCapability(player))
				{
					surgery.updatePlayerSlots(player);

					ICyberwareUserData cyberware = CyberwareAPI.getCapability(player);
					
					for (int slotIndex = 0; slotIndex < EnumSlot.values().length; slotIndex++)
					{
						EnumSlot slot = EnumSlot.values()[slotIndex];
						ItemStack[] wares = new ItemStack[LibConstants.WARE_PER_SLOT];
						
						int c = 0;
						for (int j = slotIndex * LibConstants.WARE_PER_SLOT; j < (slotIndex + 1) * LibConstants.WARE_PER_SLOT; j++)
						{
							ItemStack newStack = surgery.slots.getStackInSlot(j);
							ItemStack playerStack = surgery.slotsPlayer.getStackInSlot(j);
							if (newStack != null && newStack.stackSize > 0)
							{
								wares[c] = newStack.copy();
								if (playerStack != null && playerStack.stackSize > 0)
								{
									if (!player.inventory.addItemStackToInventory(playerStack) && !worldIn.isRemote)
									{
										
									}
								}
								c++;
							}
							else if (playerStack != null && playerStack.stackSize > 0)
							{
								if (surgery.discardSlots[j])
								{
									if (!player.inventory.addItemStackToInventory(playerStack) && !worldIn.isRemote)
									{
										
									}
								}
								else
								{
									wares[c] = surgery.slotsPlayer.getStackInSlot(j).copy();
									c++;
								}
							}
						}
						if (!worldIn.isRemote)
						{
							cyberware.setInstalledCyberware(slot, wares);
						}
						cyberware.setHasEssential(slot, !surgery.isEssentialMissing[slotIndex]);
					}
					
					if (!worldIn.isRemote)
					{
						CyberwareAPI.updateData(player);
					}
					surgery.slots = new ItemStackHandler(100);
					
					
				}
				
			}
			else
			{

				surgery.updatePlayerSlots(player);
				player.openGui(Cyberware.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			
		}
		
		return true;
		
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{ 
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntitySurgery && !worldIn.isRemote)
		{
    		TileEntitySurgery surgery = (TileEntitySurgery) tileentity;
    		
    		for (int i = 0; i < surgery.slots.getSlots(); i++)
    		{
    			ItemStack stack = surgery.slots.getStackInSlot(i);
    			if (stack != null)
    			{
    				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
    			}
    		}
		}
		super.breakBlock(worldIn, pos, state);

	}

}
