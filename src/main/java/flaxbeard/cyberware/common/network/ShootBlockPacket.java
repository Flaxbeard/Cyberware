package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.entity.EntityThrownBlock;
import flaxbeard.cyberware.common.item.ItemCyberarmTool;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ShootBlockPacket implements IMessage
{
	public ShootBlockPacket() {}
	

	public ShootBlockPacket(boolean left, boolean state)
	{}

	@Override
	public void toBytes(ByteBuf buf)
	{}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{}
	
	public static class ShootBlockPacketHandler implements IMessageHandler<ShootBlockPacket, IMessage>
	{

		@Override
		public IMessage onMessage(ShootBlockPacket message, MessageContext ctx)
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
				
				ItemStack active = ItemCyberarmTool.getActive(p);

				if (active != null  && (active.getItemDamage() / 2 == 2))
				{
					NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(active);
					World w = p.getEntityWorld();
					if (tag.hasKey("blockData"))
					{
	
						NBTTagCompound data = tag.getCompoundTag("blockData");
						String rn = data.getString("rn");
						int meta = data.getInteger("meta");
						
						Block b = Block.getBlockFromName(rn);
						//IBlockState state = 
						EntityThrownBlock falling = new EntityThrownBlock(w, p.posX, p.posY, p.posZ, b.getStateFromMeta(meta), p);
						falling.motionX = p.getLookVec().xCoord * 1;
						falling.motionY = p.getLookVec().yCoord * 1 + 0.5F;
						falling.motionZ = p.getLookVec().zCoord * 1;
						falling.fallTime = 1;
						
						if (data.hasKey("te"))
						{
							NBTTagCompound teTag = data.getCompoundTag("te");
							falling.tileEntityData = teTag;
						}
						
						w.spawnEntityInWorld(falling);
						tag.removeTag("blockData");
						
						
					}
				}
			}
		}
		

	}


}
