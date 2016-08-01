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
	
				ItemStack toDestroy = engineering.slots.getStackInSlot(0);
				
				if (CyberwareAPI.canDeconstruct(toDestroy) && toDestroy.stackSize > 0)
				{
					ItemStack paperSlot = engineering.slots.getStackInSlot(1);
					boolean doBlueprint = paperSlot != null && paperSlot.stackSize > 0;
					
					ItemStack[] components = CyberwareAPI.getComponents(toDestroy).clone();

					List<ItemStack> random = new ArrayList<ItemStack>();
					for (ItemStack component : components)
					{
						if (component != null)
						{
							for (int i = 0; i < component.stackSize; i++)
							{
								ItemStack copy = component.copy();
								copy.stackSize = 1;
								random.add(copy);
							}
						}
					}
					
					int numToRemove = world.getDifficulty().getDifficultyId() + 1;
					for (int i = 0; i < numToRemove; i++)
					{
						random.remove(world.rand.nextInt(random.size()));
					}

					ItemStackHandler handler = new ItemStackHandler(6);
					for (int i = 0; i < 6; i++)
					{
						handler.setStackInSlot(i, ItemStack.copyItemStack(engineering.slots.getStackInSlot(i + 2)));
					}
					boolean canInsert = true;
					
					// Check if drops will fit
					for (ItemStack drop : components)
					{
						ItemStack left = drop.copy();
						boolean wasAble = false;
						for (int slot = 0; slot < 6; slot++)
						{
							left = handler.insertItem(slot, left, false);
							if (left == null)
							{
								wasAble = true;
								break;
							}
						}
						
						if (!wasAble)
						{
							canInsert = false;
							break;
						}
					}
					
					// Check if blueprint will fit
					if (doBlueprint)
					{
						ItemStack left = ItemBlueprint.getBlueprintForItem(toDestroy);
						boolean wasAble = false;
						for (int slot = 0; slot < 6; slot++)
						{
							left = handler.insertItem(slot, left, false);
							if (left == null)
							{
								wasAble = true;
								break;
							}
						}
						
						if (!wasAble)
						{
							canInsert = false;
						}
					}

					
					if (canInsert)
					{
						if (doBlueprint && engineering.getWorld().rand.nextFloat() < CyberwareConfig.ENGINEERING_CHANCE)
						{
							ItemStack blue = ItemBlueprint.getBlueprintForItem(toDestroy);
							random.add(blue);
							
							ItemStack current = engineering.slots.getStackInSlot(1);
							current.stackSize--;
							if (current.stackSize <= 0)
							{
								current = null;
							}
							engineering.slots.setStackInSlot(1, current);
						}
						
						for (ItemStack drop : random)
						{
							ItemStack dropLeft = drop.copy();
							for (int slot = 2; slot < 8; slot++)
							{
								if (engineering.slots.getStackInSlot(slot) != null)
								{
									dropLeft = engineering.slots.insertItem(slot, dropLeft, false);
									if (dropLeft == null)
									{
										break;
									}
								}
							}
							
							for (int slot = 2; slot < 8; slot++)
							{
								dropLeft = engineering.slots.insertItem(slot, dropLeft, false);
								if (dropLeft == null)
								{
									break;
								}
							}
						}
						
						ItemStack current = engineering.slots.getStackInSlot(0);
						current.stackSize--;
						if (current.stackSize <= 0)
						{
							current = null;
						}
						engineering.slots.setStackInSlot(0, current);
						engineering.updateRecipe();
					}
				}

			}
			
			
		}
		
	}
}
