package flaxbeard.cyberware.common.item;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ILimbReplacement;

public class ItemProsthetics extends ItemCyberware implements ISidedLimb, ILimbReplacement
{
	
	public ItemProsthetics(String name, EnumSlot[] slots, String[] subnames)
	{
		super(name, slots, subnames);
	}

	@Override
	public boolean isEssential(ItemStack stack)
	{
		return true;		
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		ICyberware ware = CyberwareAPI.getCyberware(other);
		
		if (ware instanceof ISidedLimb)
		{
			return ware.isEssential(other) && ((ISidedLimb) ware).getSide(other) == this.getSide(stack);
		}
		return false;
	}
	
	@Override
	public EnumSide getSide(ItemStack stack)
	{
		return stack.getItemDamage() % 2 == 0 ? EnumSide.LEFT : EnumSide.RIGHT;
	}


	@Override
	public boolean isActive(ItemStack stack)
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private static final ResourceLocation texture = new ResourceLocation(Cyberware.MODID + ":textures/models/playerProsthetic.png");

	@Override
	public ResourceLocation getTexture(ItemStack stack)
	{
		return texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelPlayer getModel(ItemStack itemStack, boolean wideArms, ModelPlayer baseWide, ModelPlayer baseSkinny)
	{
		if (wideArms)
		{
			return baseWide;
		}
		else
		{
			return baseSkinny;
		}
	}
	
	@Override
	public Quality getQuality(ItemStack stack)
	{
		return null;
	}
	
	@Override
	public boolean canHoldQuality(ItemStack stack, Quality quality)
	{
		return false;
	}
	
	@Override
	public int getEssenceCost(ItemStack stack)
	{
		return 0;
	}
	
	
	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.MISCAUGS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (CyberwareAPI.hasCapability(player))
		{
			ICyberwareUserData data = CyberwareAPI.getCapability(player);
			if (!data.hasEssential(getSlot(stack), getSide(stack)))
			{
				ItemStack[] items = data.getInstalledCyberware(getSlot(stack));
				for (int i = 0; i < items.length; i++)
				{
					if (items[i] == null)
					{
						items[i] = stack.copy();
						data.setInstalledCyberware(player, getSlot(stack), items);
						if (getSide(stack) == EnumSide.LEFT)
						{
							boolean hr = data.hasEssential(getSlot(stack), EnumSide.RIGHT);
							data.setHasEssential(getSlot(stack), true, hr);
						}
						else
						{
							boolean hl = data.hasEssential(getSlot(stack), EnumSide.LEFT);
							data.setHasEssential(getSlot(stack), hl, true);
						}
						if (!player.capabilities.isCreativeMode)
						{
							--stack.stackSize;
						}
						
						if (!player.worldObj.isRemote)
						{
							CyberwareAPI.updateData(player);
						}
						
						return new ActionResult(EnumActionResult.SUCCESS, stack);
					}
				}
				
			}
		}
		return new ActionResult(EnumActionResult.FAIL, stack);

	}
}
