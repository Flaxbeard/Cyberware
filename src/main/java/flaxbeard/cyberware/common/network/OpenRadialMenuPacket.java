package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenRadialMenuPacket implements IMessage
{
	public OpenRadialMenuPacket() {}

	@Override
	public void toBytes(ByteBuf buf) {}
	
	@Override
	public void fromBytes(ByteBuf buf) {}
	
	public static class OpenRadialMenuPacketHandler implements IMessageHandler<OpenRadialMenuPacket, IMessage>
	{
		@Override
		public IMessage onMessage(OpenRadialMenuPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			DimensionManager.getWorld(player.worldObj.provider.getDimension()).addScheduledTask(new DoSync(player));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private EntityPlayer p;

		public DoSync(EntityPlayer p)
		{
			this.p = p;
		}

		
		@Override
		public void run()
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				d.setOpenedRadialMenu(true);
			}
		}
		

	}


}
