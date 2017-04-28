package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.common.item.ItemCyberarmTool;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleCyberarmPacket implements IMessage
{
	public ToggleCyberarmPacket() {}
	
	private boolean left;
	private boolean state;

	public ToggleCyberarmPacket(boolean left, boolean state)
	{
		this.left = left;
		this.state = state;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(left);
		buf.writeBoolean(state);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		left = buf.readBoolean();
		state = buf.readBoolean();
	}
	
	public static class ToggleCyberarmPacketHandler implements IMessageHandler<ToggleCyberarmPacket, IMessage>
	{

		@Override
		public IMessage onMessage(ToggleCyberarmPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			DimensionManager.getWorld(player.worldObj.provider.getDimension()).addScheduledTask(new DoSync(message.left, message.state, player));

			return null;
		}
		
	}
	
	private static class DoSync implements Runnable
	{
		private boolean left;
		private boolean state;
		private EntityPlayer p;

		public DoSync(boolean left, boolean state, EntityPlayer p)
		{
			this.left = left;
			this.state = state;
			this.p = p;
		}

		
		@Override
		public void run()
		{
			if (p != null && CyberwareAPI.hasCapability(p))
			{
				
				boolean wasActive = ItemCyberarmTool.isActive(p);

				
				ICyberwareUserData d = CyberwareAPI.getCapability(p);
				
				ItemStack limb = CyberwareAPI.getCapability(p).getLimb(EnumSlot.ARM, left ? EnumSide.LEFT : EnumSide.RIGHT);

				ItemCyberarmTool.toggleEnabledState(limb, state);
				
				if (!ItemCyberarmTool.isActive(p) && wasActive && p.getHeldItemMainhand() != null)
				{
					p.getAttributeMap().applyAttributeModifiers(p.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
				}
			}
		}
		

	}


}
