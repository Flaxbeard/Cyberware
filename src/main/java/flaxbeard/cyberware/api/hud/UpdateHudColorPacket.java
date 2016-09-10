package flaxbeard.cyberware.api.hud;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateHudColorPacket implements IMessage
{
	public UpdateHudColorPacket() {}

	private int color;

	public UpdateHudColorPacket(int color)
	{
		this.color = color;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(color);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		color = buf.readInt();
	}
	
	public static class UpdateHudColorPacketHandler implements IMessageHandler<UpdateHudColorPacket, IMessage>
	{

		@Override
		public IMessage onMessage(UpdateHudColorPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			DimensionManager.getWorld(player.worldObj.provider.getDimension()).addScheduledTask(new DoSync(message.color, player));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private int color;
		private EntityPlayer p;

		public DoSync(int color, EntityPlayer p)
		{
			this.color = color;
			this.p = p;
		}

		
		@Override
		public void run()
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				d.setHudColor(color);
			}
		}
		

	}


}
