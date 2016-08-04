package flaxbeard.cyberware.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket.CyberwareSyncPacketHandler;
import flaxbeard.cyberware.common.network.DodgePacket.DodgePacketHandler;
import flaxbeard.cyberware.common.network.EngineeringDestroyPacket.EngineeringDestroyPacketHandler;
import flaxbeard.cyberware.common.network.GuiPacket.GuiPacketHandler;
import flaxbeard.cyberware.common.network.ParticlePacket.ParticlePacketHandler;
import flaxbeard.cyberware.common.network.ScannerSmashPacket.ScannerSmashPacketHandler;
import flaxbeard.cyberware.common.network.SurgeryRemovePacket.SurgeryRemovePacketHandler;
import flaxbeard.cyberware.common.network.SwitchHeldItemAndRotationPacket.SwitchHeldItemAndRotationPacketHandler;

public class CyberwarePacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Cyberware.MODID);
	
	public static void preInit()
	{
		INSTANCE.registerMessage(CyberwareSyncPacketHandler.class, CyberwareSyncPacket.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(SurgeryRemovePacketHandler.class, SurgeryRemovePacket.class, 1, Side.SERVER);
		INSTANCE.registerMessage(SwitchHeldItemAndRotationPacketHandler.class, SwitchHeldItemAndRotationPacket.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(DodgePacketHandler.class, DodgePacket.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(GuiPacketHandler.class, GuiPacket.class, 4, Side.SERVER);
		INSTANCE.registerMessage(ParticlePacketHandler.class, ParticlePacket.class, 5, Side.CLIENT);
		INSTANCE.registerMessage(EngineeringDestroyPacketHandler.class, EngineeringDestroyPacket.class, 6, Side.SERVER);
		INSTANCE.registerMessage(ScannerSmashPacketHandler.class, ScannerSmashPacket.class, 7, Side.CLIENT);

	}
}
