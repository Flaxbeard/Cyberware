package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemStackHandler;

public class EngineeringDestroyPacket implements IMessage
{
	public EngineeringDestroyPacket() {}
	
	private BlockPos pos;
	private int dimensionId;

	public EngineeringDestroyPacket(BlockPos pos, int dimensionId)
	{
		this.pos = pos;
		this.dimensionId = dimensionId;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(dimensionId);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		dimensionId = buf.readInt();
	}
	
	public static class EngineeringDestroyPacketHandler implements IMessageHandler<EngineeringDestroyPacket, IMessage>
	{

		@Override
		public IMessage onMessage(EngineeringDestroyPacket message, MessageContext ctx)
		{
			DimensionManager.getWorld(message.dimensionId).addScheduledTask(new DoSync(message.pos, message.dimensionId));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private BlockPos pos;
		private int dimensionId;

		private DoSync(BlockPos pos, int dimensionId)
		{
			this.pos = pos;
			this.dimensionId = dimensionId;
		}

		@Override
		public void run()
		{
			World world = DimensionManager.getWorld(dimensionId);
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityEngineeringTable)
			{
				TileEntityEngineeringTable engineering = (TileEntityEngineeringTable) te;
	
				engineering.smash(true);
			}
			
			
		}
		
	}
}
