package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticlePacket implements IMessage
{
	public ParticlePacket() {}
	
	private int effectId;
	private float x;
	private float y;
	private float z;

	public ParticlePacket(int effectId, float x, float y, float z)
	{
		this.effectId = effectId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(effectId);
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		effectId = buf.readInt();
		x = buf.readFloat();
		y = buf.readFloat();
		z = buf.readFloat();
	}
	
	public static class ParticlePacketHandler implements IMessageHandler<ParticlePacket, IMessage>
	{

		@Override
		public IMessage onMessage(ParticlePacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.effectId, message.x, message.y, message.z));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private int effectId;
		private float x;
		private float y;
		private float z;
		
		public DoSync(int effectId, float x, float y, float z)
		{
			this.effectId = effectId;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public Void call() throws Exception
		{
			World world = Minecraft.getMinecraft().theWorld;
			
			if (world != null)
			{
				switch (effectId)
				{
					case 0:
						for (int i = 0; i < 5; i++)
						{
							world.spawnParticle(EnumParticleTypes.HEART,
									x + 1F * (world.rand.nextFloat() - .5F),
									y + 1F * (world.rand.nextFloat() - .5F),
									z + 1F * (world.rand.nextFloat() - .5F),
									2F * (world.rand.nextFloat() - .5F),
									.5F,
									2F * (world.rand.nextFloat() - .5F),
									new int[0]);
						}
						break;
				}
			}
			
			return null;
		}
		

	}
	
	private static class DodgeNotification implements ItemCybereyeUpgrade.INotification
	{

		@Override
		public void render(int x, int y)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(ItemCybereyeUpgrade.HUD_TEXTURE);
			ClientUtils.drawTexturedModalRect(x + 1, y + 1, 0, 39, 15, 14);
		}

		@Override
		public int getDuration()
		{
			return 5;
		}
	}

}
