package flaxbeard.cyberware.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
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
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemBlockCyberware;
import flaxbeard.cyberware.common.block.tile.TileEntityCharger;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;

public class BlockCharger extends BlockContainer
{

	public BlockCharger()
	{
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		
		String name = "charger";
		
		this.setRegistryName(name);
		GameRegistry.register(this);

		ItemBlock ib = new ItemBlockCyberware(this);
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		this.setCreativeTab(Cyberware.creativeTab);
		GameRegistry.registerTileEntity(TileEntityCharger.class, Cyberware.MODID + ":" + name);
		
        CyberwareContent.blocks.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityCharger();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
}
