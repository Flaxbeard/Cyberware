package flaxbeard.cyberware.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import flaxbeard.cyberware.client.render.RenderCyberZombie;
import flaxbeard.cyberware.client.render.TileEntitySurgeryChamberRenderer;
import flaxbeard.cyberware.common.CommonProxy;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgeryChamber;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import flaxbeard.cyberware.common.handler.CreativeMenuHandler;
import flaxbeard.cyberware.common.handler.EssentialsMissingHandlerClient;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import flaxbeard.cyberware.common.item.ItemCyberware;

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
		RenderingRegistry.registerEntityRenderingHandler(EntityCyberZombie.class, RenderCyberZombie::new);
	}


	@Override
	public void init()
	{
		super.init();
		KeyBinds.init();
		MinecraftForge.EVENT_BUS.register(EssentialsMissingHandlerClient.INSTANCE);
		MinecraftForge.EVENT_BUS.register(CreativeMenuHandler.INSTANCE);

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
		else if (item instanceof ItemBlueprint)
		{
			for (int i = 0; i < 2; i++)
			{
				ModelLoader.setCustomModelResourceLocation(item, 
						i, new ModelResourceLocation(item.getRegistryName() + (i == 1 ? "_blank" : ""), "inventory"));
			}
		}
		else
		{
			ModelLoader.setCustomModelResourceLocation(item, 
					0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

	@Override
	public void wrong(TileEntitySurgery tileEntitySurgery)
	{
		tileEntitySurgery.ticksWrong = Minecraft.getMinecraft().thePlayer.ticksExisted;
	}
	
	@Override
	public boolean workingOnPlayer(EntityLivingBase targetEntity)
	{
		return targetEntity == Minecraft.getMinecraft().thePlayer;
	}

}
