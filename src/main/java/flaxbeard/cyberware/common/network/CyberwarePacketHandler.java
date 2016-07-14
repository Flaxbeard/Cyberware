package flaxbeard.cyberware.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket.CyberwareSyncPacketHandler;
import flaxbeard.cyberware.common.network.SurgeryRemovePacket.SurgeryRemovePacketHandler;

public class CyberwarePacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Cyberware.MODID);
	
	public static void preInit()
	{
		INSTANCE.registerMessage(CyberwareSyncPacketHandler.class, CyberwareSyncPacket.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(SurgeryRemovePacketHandler.class, SurgeryRemovePacket.class, 1, Side.SERVER);
	}
}
