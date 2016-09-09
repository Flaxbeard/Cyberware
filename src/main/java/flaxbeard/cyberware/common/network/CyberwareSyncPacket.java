package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.CyberwareHudDataEvent;
import flaxbeard.cyberware.api.hud.IHudElement;
import flaxbeard.cyberware.api.hud.IHudSaveData;
import flaxbeard.cyberware.client.gui.hud.HudNBTData;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CyberwareSyncPacket implements IMessage
{
	public CyberwareSyncPacket() {}
	
	private NBTTagCompound data;
	private int entityId;

	public CyberwareSyncPacket(NBTTagCompound data, int entityId)
	{
		this.data = data;
		this.entityId = entityId;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityId);
		ByteBufUtils.writeTag(buf, data);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityId = buf.readInt();
		data = ByteBufUtils.readTag(buf);
	}
	
	public static class CyberwareSyncPacketHandler implements IMessageHandler<CyberwareSyncPacket, IMessage>
	{

		@Override
		public IMessage onMessage(CyberwareSyncPacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.entityId, message.data));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private int entityId;
		private NBTTagCompound data;
		
		public DoSync(int entityId, NBTTagCompound data)
		{
			this.entityId = entityId;
			this.data = data;
		}
		
		@Override
		public Void call() throws Exception
		{
			Entity targetEntity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
			if (targetEntity != null && CyberwareAPI.hasCapability(targetEntity))
			{
				ICyberwareUserData userData = CyberwareAPI.getCapability(targetEntity);
				userData.deserializeNBT(data);
				
				if (targetEntity == Minecraft.getMinecraft().thePlayer)
				{
					NBTTagCompound comp = userData.getHudData();
					
					CyberwareHudDataEvent hudEvent = new CyberwareHudDataEvent();
					MinecraftForge.EVENT_BUS.post(hudEvent);
					List<IHudElement> elements = hudEvent.getElements();
					
					for (IHudElement element : elements)
					{
						if (comp.hasKey(element.getUniqueName()))
						{
							element.load(new HudNBTData((NBTTagCompound) comp.getTag(element.getUniqueName())));
						}
					}
				}
			}
			
			return null;
		}
		
	}
}
