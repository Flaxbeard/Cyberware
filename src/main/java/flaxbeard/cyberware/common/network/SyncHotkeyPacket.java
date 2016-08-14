package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.HotkeyHelper;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncHotkeyPacket implements IMessage
{
	public SyncHotkeyPacket() {}
	
	private int selectedPart;
	private int key;

	public SyncHotkeyPacket(int selectedPart, int key)
	{
		this.selectedPart = selectedPart;
		this.key = key;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(selectedPart);
		buf.writeInt(key);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		selectedPart = buf.readInt();
		key = buf.readInt();
	}
	
	public static class SyncHotkeyPacketHandler implements IMessageHandler<SyncHotkeyPacket, IMessage>
	{

		@Override
		public IMessage onMessage(SyncHotkeyPacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.selectedPart, message.key, ctx.getServerHandler().playerEntity));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private int selectedPart;
		private int key;
		private EntityPlayer p;

		public DoSync(int selectedPart, int key, EntityPlayer p)
		{
			this.selectedPart = selectedPart;
			this.key = key;
			this.p = p;
		}

		
		@Override
		public Void call() throws Exception
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				HotkeyHelper.removeHotkey(d, key);
				HotkeyHelper.assignHotkey(d, d.getActiveItems().get(selectedPart), key);
			}

			return null;
		}
		

	}
	
}
