package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade.NotificationInstance;
import io.netty.buffer.ByteBuf;

import java.util.Random;
import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DodgePacket implements IMessage
{
	public DodgePacket() {}
	
	private int entityId;

	public DodgePacket(int entityId)
	{
		this.entityId = entityId;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityId);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityId = buf.readInt();
	}
	
	public static class DodgePacketHandler implements IMessageHandler<DodgePacket, IMessage>
	{

		@Override
		public IMessage onMessage(DodgePacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.entityId));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private int entityId;
		
		public DoSync(int entityId)
		{
			this.entityId = entityId;
		}
		
		@Override
		public Void call() throws Exception
		{
			Entity targetEntity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
			
			if (targetEntity != null)
			{
				for (int i = 0; i < 25; i++)
				{
					Random rand = targetEntity.worldObj.rand;
					targetEntity.worldObj.spawnParticle(EnumParticleTypes.SPELL, targetEntity.posX, targetEntity.posY + rand.nextFloat() * targetEntity.height, targetEntity.posZ, 
							(rand.nextFloat() - .5F) * .2F,
							0,
							(rand.nextFloat() - .5F) * .2F,
							new int[] {255, 255, 255});
				
				}
				
				targetEntity.playSound(SoundEvents.ENTITY_FIREWORK_SHOOT, 1F, 1F);
				
				if (targetEntity == Minecraft.getMinecraft().thePlayer)
				{
					ItemCybereyeUpgrade.addNotification(new NotificationInstance(targetEntity.ticksExisted, new DodgeNotification()));
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
