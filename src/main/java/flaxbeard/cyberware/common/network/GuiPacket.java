package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.Cyberware;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiPacket implements IMessage
{
	private int guid;
	private int x;
	private int y;
	private int z;

	public GuiPacket() {}
	
	public GuiPacket(int guid, int x, int y, int z)
	{
		this.guid = guid;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(guid);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		guid = buf.readInt();
	}
	
	public static class GuiPacketHandler implements IMessageHandler<GuiPacket, IMessage>
	{

		@Override
		public IMessage onMessage(GuiPacket message, MessageContext ctx)
		{
			EntityPlayerMP serverPlayer = ctx.getServerHandler().playerEntity;
			DimensionManager.getWorld(serverPlayer.worldObj.provider.getDimension()).addScheduledTask(new DoSync(ctx, message.guid, message.x, message.y, message.z));


			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private int guid;
		private int x;
		private int y;
		private int z;
		private MessageContext context;
		
		public DoSync(MessageContext ctx, int guid, int x, int y, int z)
		{
			this.context = ctx;
			this.guid = guid;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public void run()
		{
			EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;
			serverPlayer.openGui(Cyberware.INSTANCE, guid, serverPlayer.worldObj, x, y, z);
			
			
		}
		
	}
}
