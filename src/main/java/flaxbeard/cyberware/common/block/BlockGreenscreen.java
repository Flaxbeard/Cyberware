package flaxbeard.cyberware.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.item.ItemBlockCyberware;

public class BlockGreenscreen extends Block
{

	public BlockGreenscreen()
	{
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		
		String name = "green";
		
		this.setRegistryName(name);
		GameRegistry.register(this);

		ItemBlock ib = new ItemBlockCyberware(this);
		ib.setRegistryName(name);
		GameRegistry.register(ib);
		
		this.setUnlocalizedName(Cyberware.MODID + "." + name);

		this.setCreativeTab(Cyberware.creativeTab);
		
		CyberwareContent.blocks.add(this);
		
		this.setLightLevel(15);
	}



}
