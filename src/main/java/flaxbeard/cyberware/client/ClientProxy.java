package flaxbeard.cyberware.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import flaxbeard.cyberware.client.render.TileEntitySurgeryChamberRenderer;
import flaxbeard.cyberware.common.CommonProxy;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgeryChamber;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.sprockets.Sprockets;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void preInit()
	{
		super.preInit();
		
		for (Block block : CyberwareContent.blocks)
		{
			registerRenders(block);
		}
		
		for (Item item : CyberwareContent.items)
		{
			registerRenders(item);
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySurgeryChamber.class, new TileEntitySurgeryChamberRenderer());
	}


	@Override
	public void init()
	{
		super.init();
		KeyBinds.init();
	}
	
	


	@Override
	public void postInit()
	{
		super.postInit();
	}
	
	private void registerRenders(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		ModelLoader.setCustomModelResourceLocation(item, 
				0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
	
	private void registerRenders(Item item)
	{
		if (item instanceof ItemCyberware && ((ItemCyberware) item).subnames.length > 0)
		{
			ItemCyberware ware = (ItemCyberware) item;
			for (int i = 0; i < ware.subnames.length; i++)
			{
				ModelLoader.setCustomModelResourceLocation(item, 
						i, new ModelResourceLocation(item.getRegistryName() + "_" + ware.subnames[i], "inventory"));
			}
		}
		else
		{
			ModelLoader.setCustomModelResourceLocation(item, 
					0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

}
