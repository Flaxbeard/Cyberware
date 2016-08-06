package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.gui.ContainerEngineeringTable;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EngineeringSwitchArchivePacket implements IMessage
{
	public EngineeringSwitchArchivePacket() {}
	
	private BlockPos pos;
	private int dimensionId;
	private int entityId;
	private boolean direction;
	
	public EngineeringSwitchArchivePacket(BlockPos pos, EntityPlayer player, boolean direction)
	{
		this.dimensionId = player.worldObj.provider.getDimension();
		this.entityId = player.getEntityId();
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(direction);
		buf.writeInt(entityId);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(dimensionId);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		direction = buf.readBoolean();
		entityId = buf.readInt();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		dimensionId = buf.readInt();
	}
	
	public static class EngineeringSwitchArchivePacketHandler implements IMessageHandler<EngineeringSwitchArchivePacket, IMessage>
	{

		@Override
		public IMessage onMessage(EngineeringSwitchArchivePacket message, MessageContext ctx)
		{
			DimensionManager.getWorld(message.dimensionId).addScheduledTask(new DoSync(message.pos, message.dimensionId, message.entityId, message.direction));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private BlockPos pos;
		private int dimensionId;
		private int entityId;
		private boolean direction;

		private DoSync(BlockPos pos, int dimensionId, int entityId, boolean direction)
		{
			this.pos = pos;
			this.dimensionId = dimensionId;
			this.entityId = entityId;
			this.direction = direction;
		}

		@Override
		public void run()
		{
			World world = DimensionManager.getWorld(dimensionId);
			Entity e = world.getEntityByID(entityId);
			if (e instanceof EntityPlayer)
			{
				EntityPlayer p = (EntityPlayer) e;
				if (p.openContainer instanceof ContainerEngineeringTable)
				{
					if (direction)
					{
						((ContainerEngineeringTable) p.openContainer).nextArchive();
					}
					else
					{
						((ContainerEngineeringTable) p.openContainer).prevArchive();
					}
					TileEntityEngineeringTable te = (TileEntityEngineeringTable) world.getTileEntity(pos);
					te.lastPlayerArchive.put(p.getCachedUniqueIdString(), ((ContainerEngineeringTable) p.openContainer).archive.getPos());
				}
			}
			
			
		}
		
	}
}
