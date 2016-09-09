package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncHudDataPacket implements IMessage
{
	public SyncHudDataPacket() {}
	
	private NBTTagCompound comp;

	public SyncHudDataPacket(NBTTagCompound comp)
	{
		this.comp = comp;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, comp);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		comp = ByteBufUtils.readTag(buf);
	}
	
	public static class SyncHotkeyPacketHandler implements IMessageHandler<SyncHudDataPacket, IMessage>
	{

		@Override
		public IMessage onMessage(SyncHudDataPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			DimensionManager.getWorld(player.worldObj.provider.getDimension()).addScheduledTask(new DoSync(message.comp, player));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private NBTTagCompound comp;
		private EntityPlayer p;

		public DoSync(NBTTagCompound comp, EntityPlayer p)
		{
			this.comp = comp;
			this.p = p;
		}

		
		@Override
		public void run()
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				
				d.setHudData(comp);
			}

		}
		

	}
	
}
