package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ScannerSmashPacket implements IMessage
{
	public ScannerSmashPacket() {}
	
	private int x;
	private int y;
	private int z;

	public ScannerSmashPacket(int x, int y, int z)
	{
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
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}
	
	public static class ScannerSmashPacketHandler implements IMessageHandler<ScannerSmashPacket, IMessage>
	{

		@Override
		public IMessage onMessage(ScannerSmashPacket message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new DoSync(message.x, message.y, message.z));

			return null;
		}
		
	}
	
	private static class DoSync implements Callable<Void>
	{
		private int x;
		private int y;
		private int z;
		
		public DoSync(int x, int y, int z)
		{
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
				TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
				if (te != null && te instanceof TileEntityEngineeringTable)
				{
					TileEntityEngineeringTable eng = (TileEntityEngineeringTable) te;
					eng.smashSounds();
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
