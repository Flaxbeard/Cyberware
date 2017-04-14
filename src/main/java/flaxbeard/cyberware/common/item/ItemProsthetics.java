package flaxbeard.cyberware.common.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import flaxbeard.cyberware.api.item.ICyberwareTabItem.EnumCategory;
import flaxbeard.cyberware.api.item.ILimbReplacement;
import flaxbeard.cyberware.client.render.RenderPlayerCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

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
}
