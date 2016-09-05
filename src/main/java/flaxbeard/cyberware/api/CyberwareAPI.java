package flaxbeard.cyberware.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.oredict.OreDictionary;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;

public final class CyberwareAPI
{
	/**
	 * Store any functional data of your Cyberware in NBT under this tag, which will be cleared when new items are added or removed
	 * to ensure stacking and such works
	 */
	public static final String DATA_TAG = "cyberwareFunctionData";
	
	public static final String QUALITY_TAG = "cyberwareQuality";

	/**
	 * Quality for Cyberware scavenged from mobs
	 */
	public static final Quality QUALITY_SCAVENGED = new Quality("cyberware.quality.scavenged", "cyberware.quality.scavenged.nameModifier", "scavenged");
	
	/**
	 * Quality for Cyberware built at the Engineering Table
	 */
	public static final Quality QUALITY_MANUFACTURED = new Quality("cyberware.quality.manufactured");

	@CapabilityInject(ICyberwareUserData.class)
	public static final Capability<ICyberwareUserData> CYBERWARE_CAPABILITY = null;
	
	public static Map<ItemStack, ICyberware> linkedWare = new HashMap<ItemStack, ICyberware>();
	
	
	private static float[] mainColor = new float[] { 76 / 255F, 1F, 0F };
	private static int mainColorHex = 0x4CFF00;
	
	/**
	 * Sets the HUD color for the Hudjack, radial menu, and other AR HUD elements
	 * 
	 * @param color	A float representation of the desired color
	 */
	public static void setHUDColor(float[] color)
	{
		mainColor = color;
		int ri = Math.round(color[0] * 255);
		int gi = Math.round(color[1] * 255);
		int bi = Math.round(color[2] * 255);
		
		int rp = (ri << 16) & 0xFF0000;
		int gp = (gi << 8) & 0x00FF00;
		int bp = (bi) & 0x0000FF;
		
		mainColorHex = rp | gp | bp;
	}
	
	/**
	 * Sets the HUD color for the Hudjack, radial menu, and other AR HUD elements
	 * 
	 * @param color	A hexadecimal representation of the desired color
	 */
	public static void setHUDColor(int hexVal)
	{
		float r = ((hexVal >> 16) & 0x0000FF) / 255F;
		float g = ((hexVal >> 8) & 0x0000FF) / 255F;
		float b = ((hexVal) & 0x0000FF) / 255F;
		setHUDColor(r, g, b);
	}
	
	public static void setHUDColor(float r, float g, float b)
	{
		setHUDColor(new float[] { r, g, b });
	}
	
	public static int getHUDColorHex()
	{
		return mainColorHex;
	}
	
	public static float[] getHUDColor()
	{
		return mainColor;
	}
	
	/**
	 * Can be used by your ICyberware implementation's setQuality function. Helper method that
	 * writes a quality to an easily accessible NBT tag. See the partner function, readQualityTag
	 * 
	 * @param stack	The stack to write to
	 * @return		The modified stack
	 */
	public static ItemStack writeQualityTag(ItemStack stack, Quality quality)
	{
		if (stack == null) return stack;
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setString(QUALITY_TAG, quality.getUnlocalizedName());
		return stack;
	}
	
	public static Quality getQualityTag(ItemStack stack)
	{
		if (stack == null) return null;
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(QUALITY_TAG))
		{
			return null;
		}
		return Quality.getQualityFromString(stack.getTagCompound().getString(QUALITY_TAG));
	}
	
	/**
	 * Clears all NBT data from Cyberware related to its function, things like power storage or oxygen storage
	 * This ensures that removed Cyberware will stack. This should only be called on Cyberware that is being removed
	 * from the body or otherwise reset - otherwise it may interrupt functionality.
	 * 
	 * @param stack	The ItemStack to sanitize
	 * @return		A sanitized version of the stack
	 */
	public static ItemStack sanitize(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DATA_TAG))
			{
				stack.getTagCompound().removeTag(DATA_TAG);
			}
			if (stack.hasTagCompound() && stack.getTagCompound().hasNoTags())
			{
				stack.setTagCompound(null);
			}
		}
		
		return stack;
	}
	
	/**
	 * Gets the NBT data for Cyberware related to its function. This data is removed when a piece of Cyberware
	 * is removed, and is not counted when determining whether Cyberware stacks are the same for purposes of merging
	 * and such. This function will create a data tag if one does not exist.
	 * 
	 * @param stack	The ItemStack for which you want the data
	 * @return		The data, in the form of an NBTTagCompound
	 */
	public static NBTTagCompound getCyberwareNBT(ItemStack stack)
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		if (!stack.getTagCompound().hasKey(DATA_TAG))
		{
			stack.getTagCompound().setTag(DATA_TAG, new NBTTagCompound());
		}
		
		return stack.getTagCompound().getCompoundTag(DATA_TAG);
	}
	
	public static boolean areCyberwareStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		if (stack1 == null || stack2 == null) return false;
		
		ItemStack sanitized1 = sanitize(ItemStack.copyItemStack(stack1));
		ItemStack sanitized2 = sanitize(ItemStack.copyItemStack(stack2));
		return sanitized1.getItem() == sanitized2.getItem() && sanitized1.getItemDamage() == sanitized2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}
	
	/**
	 * Links an ItemStack to an instance of ICyberware. This option is generally worse than
	 * implementing ICyberware in your Item, but if you don't have access to the Item it's the
	 * best option. This version of the method links a specific meta value.
	 * 
	 * @param stack	The ItemStack to link
	 * @param link	An instance of ICyberware to link it to
	 */
	public static void linkCyberware(ItemStack stack, ICyberware link)
	{
		if (stack == null) return;
		
		ItemStack key = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		linkedWare.put(key, link);
	}
	
	/**
	 * Links an Item to an instance of ICyberware. This option is generally worse than
	 * implementing ICyberware in your Item, but if you don't have access to the Item it's the
	 * best option. This version of the method links all meta values.
	 * 
	 * @param item	The Item to link
	 * @param link	An instance of ICyberware to link it to
	 */
	public static void linkCyberware(Item item, ICyberware link)
	{
		if (item == null) return;
		
		ItemStack key = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
		linkedWare.put(key, link);
	}
	
	/**
	 * Determines if the inputted item stack is Cyberware. This means its item either
	 * implements ICyberware or is linked to one (in the case of vanilla items)
	 * 
	 * @param stack	The ItemStack to test
	 * @return		If the stack is valid Cyberware
	 */
	public static boolean isCyberware(ItemStack stack)
	{
		return stack != null && (stack.getItem() instanceof ICyberware || getLinkedWare(stack) != null);
	}
	
	/**
	 * Returns an instance of ICyberware linked with an itemstack, usually
	 * the item which extends ICyberware, though it may be a standalone
	 * ICyberware-implementing object
	 * 
	 * @param stack	The ItemStack, from which the linked ICyberware is found
	 * @return		The linked instance of ICyberware
	 */
	public static ICyberware getCyberware(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.getItem() instanceof ICyberware)
			{
				return (ICyberware) stack.getItem();
			}
			else if (getLinkedWare(stack) != null)
			{
				return getLinkedWare(stack);
			}
		}
		
		throw new RuntimeException("Cannot call getCyberware on a non-cyberware item!");
	}
	
	/**
	 * Determines if the inputted item stack can be destroyed in the Engineering Table,
	 * meaning it implements IDeconstructable.
	 * 
	 * @param stack	The ItemStack to test
	 * @return		If the stack can be deconstructed.
	 */
	public static boolean canDeconstruct(ItemStack stack)
	{
		return stack != null && (stack.getItem() instanceof IDeconstructable) && ((IDeconstructable) stack.getItem()).canDestroy(stack);
	}

	/**
	 * Returns a list of ItemStacks containing the components of a destructable
	 * item.
	 * 
	 * @param stack	The ItemStack to test
	 * @return		The components of the item
	 */
	public static ItemStack[] getComponents(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.getItem() instanceof IDeconstructable)
			{
				return ((IDeconstructable) stack.getItem()).getComponents(stack).clone();
			}
		}
		
		throw new RuntimeException("Cannot call getComponents on a non-cyberware item!");
	}
	
	private static ICyberware getLinkedWare(ItemStack stack)
	{
		if (stack == null) return null;
		
		ItemStack test = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		ICyberware result = getWareFromKey(test);
		if (result != null)
		{
			return result;
		}
		
		ItemStack testGeneric = new ItemStack(stack.getItem(), 1, OreDictionary.WILDCARD_VALUE);
		result = getWareFromKey(testGeneric);
		if (result != null)
		{
			return result;
		}
		
		return null;
	}
	
	private static ICyberware getWareFromKey(ItemStack key)
	{
		for (Entry<ItemStack, ICyberware> entry : linkedWare.entrySet())
		{
			ItemStack entryKey = entry.getKey();
			if (key.getItem() == entryKey.getItem() && key.getItemDamage() == entryKey.getItemDamage())
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * A shortcut method to determine if the entity that is inputted
	 * has ICyberwareUserData. Works with null entites.
	 * 
	 * @param targetEntity	The entity to test
	 * @return				If the entity has ICyberwareUserData
	 */
	public static boolean hasCapability(@Nullable Entity targetEntity)
	{
		if (targetEntity == null) return false;
		return targetEntity.hasCapability(CYBERWARE_CAPABILITY, EnumFacing.EAST);
	}
	
	/**
	 * Assistant method to hasCapability. A shortcut to get you the ICyberwareUserData
	 * of a specific entity. Note that you must verify if it has the capability first.
	 * 
	 * @param targetEntity	The entity whose ICyberwareUserData you want
	 * @return				The ICyberwareUserData associated with the entity
	 */
	public static ICyberwareUserData getCapability(Entity targetEntity)
	{
		return targetEntity.getCapability(CYBERWARE_CAPABILITY, EnumFacing.EAST);
	}
	
	/**
	 * A shortcut method for event handlers and the like to quickly tell if an entity
	 * has a piece of Cyberware installed. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				If the entity has the Cyberware
	 */
	public static boolean isCyberwareInstalled(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return false;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.isCyberwareInstalled(stack);
	}
	
	/**
	 * A shortcut method for event handlers and the like to quickly determine what level of
	 * Cyberware is installed. Returns 0 if none. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				If the entity has the Cyberware, the level, or 0 if not
	 */
	public static int getCyberwareRank(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return 0;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.getCyberwareRank(stack);
	}
	
	/**
	 * A shortcut method for event handlers and the like to get the itemstack for a piece
	 * of cyberware. Useful for NBT data. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				The ItemStack found, or null if none
	 */
	public static ItemStack getCyberware(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return null;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.getCyberware(stack);
	}
	
	public static void updateData(Entity targetEntity)
	{
		if (!targetEntity.worldObj.isRemote)
		{
			WorldServer world = (WorldServer) targetEntity.worldObj;
			
			NBTTagCompound nbt = CyberwareAPI.getCapability(targetEntity).serializeNBT();
			
			if (targetEntity instanceof EntityPlayer)
			{
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, targetEntity.getEntityId()), (EntityPlayerMP) targetEntity);
				//System.out.println("Sent data for player " + ((EntityPlayer) targetEntity).getName() + " to that player's client");
			}

			for (EntityPlayer trackingPlayer : world.getEntityTracker().getTrackingPlayers(targetEntity))
			{
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, targetEntity.getEntityId()), (EntityPlayerMP) trackingPlayer);
				
				if (targetEntity instanceof EntityPlayer)
				{
					//System.out.println("Sent data for player " + ((EntityPlayer) targetEntity).getName() + " to player " + trackingPlayer.getName());
				}
			}
		}

	}
	
	public static void useActiveItem(Entity entity, ItemStack stack)
	{
		((IMenuItem) stack.getItem()).use(entity, stack);
	}

}
