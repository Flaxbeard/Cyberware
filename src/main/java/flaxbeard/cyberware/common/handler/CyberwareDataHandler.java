package flaxbeard.cyberware.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;

public class CyberwareDataHandler
{
	public static final CyberwareDataHandler INSTANCE = new CyberwareDataHandler();

	@SubscribeEvent
	public void attachCyberwareData(AttachCapabilitiesEvent.Entity event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			event.addCapability(CyberwareUserDataImpl.Provider.NAME, new CyberwareUserDataImpl.Provider());
		}
	}
	
	@SubscribeEvent
	public void playerDeathEvent(PlayerEvent.Clone event)
	{
	}
	
	@SubscribeEvent
	public void syncCyberwareData(EntityJoinWorldEvent event)
	{
		if (!event.getWorld().isRemote)
		{
			Entity e = event.getEntity();
			if (CyberwareAPI.hasCapability(e))
			{
				if (e instanceof EntityPlayer)
				{
					System.out.println("Sent data for player " + ((EntityPlayer) e).getName() + " to that player's client");
					NBTTagCompound nbt = CyberwareAPI.getCapability(e).serializeNBT();
					CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, e.getEntityId()), (EntityPlayerMP) e);
				}
			}
			
		}
	}

	@SubscribeEvent
	public void startTrackingEvent(StartTracking event)
	{			
		EntityPlayer tracker = event.getEntityPlayer();
		Entity target = event.getTarget();
		
		if (!target.worldObj.isRemote)
		{
			if (CyberwareAPI.hasCapability(target))
			{
				if (target instanceof EntityPlayer)
				{
					System.out.println("Sent data for player " + ((EntityPlayer) target).getName() + " to player " + tracker.getName());
				}
				
				NBTTagCompound nbt = CyberwareAPI.getCapability(target).serializeNBT();
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, target.getEntityId()), (EntityPlayerMP) tracker);
			}
		}
		
	}

}
