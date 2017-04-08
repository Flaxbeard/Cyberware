package flaxbeard.cyberware.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.progression.CyberwareProgressionDataImpl;
import flaxbeard.cyberware.api.progression.ICyberwareProgressionData;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;
import flaxbeard.cyberware.common.handler.EssentialsMissingHandler;
import flaxbeard.cyberware.common.handler.GuiHandler;
import flaxbeard.cyberware.common.handler.MiscHandler;
import flaxbeard.cyberware.common.handler.ProgressionHandler;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;

public class CommonProxy
{
	public void preInit()
	{
		CapabilityManager.INSTANCE.register(ICyberwareUserData.class, CyberwareUserDataImpl.STORAGE, CyberwareUserDataImpl.class);
		CapabilityManager.INSTANCE.register(ICyberwareProgressionData.class, CyberwareProgressionDataImpl.STORAGE, CyberwareProgressionDataImpl.class);

		CyberwareContent.preInit();
		CyberwarePacketHandler.preInit();
	}
	
	public void init()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Cyberware.INSTANCE, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(CyberwareDataHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(CyberwareConfig.INSTANCE);
		MinecraftForge.EVENT_BUS.register(MiscHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EssentialsMissingHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ProgressionHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(EssentialsMissingHandler.INSTANCE);
	}
	
	public void postInit()
	{
		CyberwareConfig.postInit();
		CyberwareContent.postInit();
	}

	public void wrong(TileEntitySurgery tileEntitySurgery) {}

	public boolean workingOnPlayer(EntityLivingBase targetEntity)
	{
		return false;
	}
}
