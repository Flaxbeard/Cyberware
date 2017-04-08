package flaxbeard.cyberware.api.progression;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.block.tile.TileEntityRFIDContainer;

public class ProgressionHelper
{
	/**
	 * A shortcut to get you the ICyberwareProgressionData for a player.
	 * 
	 * @param targetPlayer	The player whose ICyberwareProgressionData you want
	 * @return				The ICyberwareProgressionData associated with the player
	 */
	public static ICyberwareProgressionData getCapability(EntityPlayer targetPlayer)
	{
		return targetPlayer.getCapability(CyberwareAPI.PROGRESSION_CAPABILITY, EnumFacing.EAST);
	}
	
	/**
	 * A shortcut method to determine if the entity that is inputted
	 * has ICyberwareProgressionData. Works with null entites.
	 * 
	 * @param targetEntity	The entity to test
	 * @return				If the entity has ICyberwareProgressionData
	 */
	public static boolean hasCapability(@Nullable EntityPlayer targetEntity)
	{
		if (targetEntity == null) return false;
		return targetEntity.hasCapability(CyberwareAPI.PROGRESSION_CAPABILITY, EnumFacing.EAST);
	}

	public static boolean isUnlocked(EntityPlayer player, ItemStack ware)
	{
		return getCapability(player).hasSeen(ware);
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
		ItemStack stack = ItemPool.generateRandomContainerItemForPlayer(openingPlayer);
		stack.stackSize = 1 + openingPlayer.worldObj.rand.nextInt(2);
		te.slots.setStackInSlot(openingPlayer.worldObj.rand.nextInt(te.slots.getSlots()), stack);
		if (stack.getItem() instanceof IDeconstructable)
		{
			ItemStack[] parts = ((IDeconstructable) stack.getItem()).getComponents(stack);
			for (ItemStack component : parts)
			{
				ItemStack modComponent = component.copy();
				modComponent.stackSize = (modComponent.stackSize * openingPlayer.worldObj.rand.nextInt(3)) + openingPlayer.worldObj.rand.nextInt(2) - 1;
				while (modComponent.stackSize > 0)
				{
					int amount = Math.min(modComponent.stackSize, openingPlayer.worldObj.rand.nextInt(3) + 1);
					int slot = openingPlayer.worldObj.rand.nextInt(te.slots.getSlots());
					
					int count = 0;
					while (te.slots.getStackInSlot(slot) != null && count < 12)
					{
						slot = openingPlayer.worldObj.rand.nextInt(te.slots.getSlots());
						count++;
					}
					
					ItemStack toPut = modComponent.copy();
					toPut.stackSize = amount;
					modComponent.stackSize -= amount;
					
					te.slots.setStackInSlot(slot, toPut);
				}
			}
		}
	}

	public static boolean canBeSeen(ItemStack entityItem)
	{
		for (ItemStack stack : ItemPool.getAllItems())
		{
			if (entityItem.getItem() == stack.getItem() && entityItem.getItemDamage() == stack.getItemDamage())
			{
				return true;
			}
		}
		return false;
	}
}
