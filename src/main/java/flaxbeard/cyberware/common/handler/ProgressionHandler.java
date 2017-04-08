package flaxbeard.cyberware.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import flaxbeard.cyberware.api.progression.ProgressionHelper;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.ProgressionSyncPacket;

public class ProgressionHandler
{

	public static final ProgressionHandler INSTANCE = new ProgressionHandler();


	@SubscribeEvent
	public void pickupEvent(PlayerEvent.ItemPickupEvent event)
	{
		EntityPlayer p = event.player;
		
		if (ProgressionHelper.hasCapability(p) && ProgressionHelper.canBeSeen(event.pickedUp.getEntityItem()))
		{
			ProgressionHelper.getCapability(p).markSeen(event.pickedUp.getEntityItem());
			NBTTagCompound nbt = ProgressionHelper.getCapability(p).serializeNBT();
			CyberwarePacketHandler.INSTANCE.sendTo(new ProgressionSyncPacket(nbt, p.getEntityId()), (EntityPlayerMP) p);
		}
	}
}
