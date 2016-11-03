package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateConfigPacket implements IMessage
{
	public UpdateConfigPacket() {}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(CyberwareConfig.ESSENCE);
		buf.writeInt(CyberwareConfig.CRITICAL_ESSENCE);
		
		buf.writeBoolean(CyberwareConfig.SURGERY_CRAFTING);
		
		buf.writeFloat(CyberwareConfig.ENGINEERING_CHANCE);
		buf.writeFloat(CyberwareConfig.SCANNER_CHANCE);
		buf.writeFloat(CyberwareConfig.SCANNER_CHANCE_ADDL);
		buf.writeInt(CyberwareConfig.SCANNER_TIME);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		CyberwareConfig.ESSENCE = buf.readInt();
		CyberwareConfig.CRITICAL_ESSENCE = buf.readInt();
		
		CyberwareConfig.SURGERY_CRAFTING = buf.readBoolean();
		
		CyberwareConfig.ENGINEERING_CHANCE = buf.readFloat();
		CyberwareConfig.SCANNER_CHANCE = buf.readFloat();
		CyberwareConfig.SCANNER_CHANCE_ADDL = buf.readFloat();
		CyberwareConfig.SCANNER_TIME = buf.readInt();
	}
	
	public static class UpdateConfigPacketHandler implements IMessageHandler<UpdateConfigPacket, IMessage>
	{

		@Override
		public IMessage onMessage(UpdateConfigPacket message, MessageContext ctx)
		{
			return null;
		}
		
	}
}
