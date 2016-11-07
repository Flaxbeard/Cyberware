package flaxbeard.cyberware.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import flaxbeard.cyberware.common.block.tile.TileEntityRFIDContainer;

public class ProgressionHelper
{
	public static boolean isUnlocked(ItemStack ware)
	{
		return ((ware.getItem().getIdFromItem(ware.getItem()) ^ (ware.getItemDamage())) % 2) == 0;
	}
	
	private static int range = 15;
	
	public static void populateLootChest(TileEntityRFIDContainer te, EntityPlayer openingPlayer)
	{
		boolean forPlayer = te.isForPlayer(openingPlayer);
		
		if (!forPlayer)
		{
			int x = te.getPos().getX();
			int y = te.getPos().getY();
			int z = te.getPos().getZ();
			float minDist = Float.MAX_VALUE;
			EntityPlayer tempPlayer = null;
			List<EntityPlayer> players = te.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range));
			for (EntityPlayer player : players)
			{
				float dist = (float) Math.sqrt((player.posX - x) * (player.posX - x) + (player.posY - y) * (player.posY - y) + (player.posZ - z) * (player.posZ - z));
				if (openingPlayer == null && dist < minDist)
				{
					tempPlayer = player;
					minDist = dist;
				}
				
				if (te.isForPlayer(player))
				{
					forPlayer = true;
					break;
				}
			}
						
			if (openingPlayer == null)
			{
				openingPlayer = tempPlayer;
			}
		}
		
		if (forPlayer)
		{
			generateGenericLoot(te, openingPlayer);
		}
		else
		{
			generateGenericLoot(te, openingPlayer);
		}
	}

	private static void generateGenericLoot(TileEntityRFIDContainer te, EntityPlayer openingPlayer)
	{
		System.out.println(openingPlayer == null ? "NULL" : openingPlayer.getName());
		te.slots.setStackInSlot(0, new ItemStack(Items.APPLE));
	}
}
