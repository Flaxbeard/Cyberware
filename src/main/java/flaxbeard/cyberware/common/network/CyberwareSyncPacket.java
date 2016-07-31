package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
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
				CyberwareAPI.getCapability(targetEntity).deserializeNBT(data);
				if (targetEntity instanceof EntityPlayer)
				{
					//System.out.println("Got data for player " + ((EntityPlayer) targetEntity).getName());
					/*if (targetEntity != Minecraft.getMinecraft().thePlayer)
					{
						ItemStack[] oldWares = CyberwareAPI.getCapability(targetEntity).getInstalledCyberware(EnumSlot.EYES);
						for (ItemStack i : oldWares)
						{
							if (i == null)
							{
								System.out.print("null ");
							}
							else
							{
								System.out.print(i.getItem().getRegistryName() + "[" + i.stackSize + "] ");
							}
						}
					}*/
				}
			}
			
			return null;
		}
		
	}
}
