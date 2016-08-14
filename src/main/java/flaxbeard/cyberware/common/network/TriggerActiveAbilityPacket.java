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
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TriggerActiveAbilityPacket implements IMessage
{
	public TriggerActiveAbilityPacket() {}
	
	private ItemStack stack;

	public TriggerActiveAbilityPacket(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		stack = ByteBufUtils.readItemStack(buf);
	}
	
	public static class TriggerActiveAbilityPacketHandler implements IMessageHandler<TriggerActiveAbilityPacket, IMessage>
	{

		@Override
		public IMessage onMessage(TriggerActiveAbilityPacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.stack, ctx.getServerHandler().playerEntity));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private ItemStack stack;
		private EntityPlayer p;

		public DoSync(ItemStack stack, EntityPlayer p)
		{
			this.stack = stack;
			this.p = p;
		}

		
		@Override
		public Void call() throws Exception
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				CyberwareAPI.useActiveItem(p, d.getCyberware(stack));
			}

			return null;
		}
		

	}


}
