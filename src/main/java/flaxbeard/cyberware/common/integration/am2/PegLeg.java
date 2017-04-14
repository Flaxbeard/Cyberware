package flaxbeard.cyberware.common.integration.am2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ILimbReplacement;
import flaxbeard.cyberware.client.render.RenderPlayerCyberware;

public class PegLeg implements ICyberware, ISidedLimb, ILimbReplacement
{
	public PegLeg()
	{
	}

	@Override
	public EnumSlot getSlot(ItemStack stack)
	{
		return EnumSlot.LEG;
	}

	@Override
	public int installedStackSize(ItemStack stack)
	{
		return 1;
	}

	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		return new ItemStack[0][0];
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
	public boolean isEssential(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public List<String> getInfo(ItemStack stack)
	{
		List<String> ret = new ArrayList<String>();

		return ret;
	}

	@Override
	public int getCapacity(ItemStack wareStack)
	{
		return 0;
	}

	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack) {}

	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack) {}

	@Override
	public int getEssenceCost(ItemStack stack)
	{
		return 0;
	}

	@Override
	public Quality getQuality(ItemStack stack)
	{
		return null;
	}

	@Override
	public ItemStack setQuality(ItemStack stack, Quality quality)
	{
		return stack;
	}

	@Override
	public boolean canHoldQuality(ItemStack stack, Quality quality)
	{
		return false;
	}

	@Override
	public boolean isActive(ItemStack stack)
	{
		return true;
	}


	@SideOnly(Side.CLIENT)
	private static final ModelPlayer model = new ModelPegLeg();
	
	@SideOnly(Side.CLIENT)
	private static final ResourceLocation texture = new ResourceLocation(Cyberware.MODID + ":textures/models/pegLeg.png");
	
	@Override
	public ResourceLocation getTexture(ItemStack stack)
	{
		return texture;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelPlayer getModel(ItemStack itemStack, boolean wideArms, ModelPlayer baseWide, ModelPlayer baseSkinny)
	{
		return model;
	}

	@Override
	public EnumSide getSide(ItemStack stack)
	{
		return EnumSide.LEFT;
	}
}
