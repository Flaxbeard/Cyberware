package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.ICyberware;

public class VanillaWares
{
	public static class SpiderEyeWare implements ICyberware
	{
		public SpiderEyeWare()
		{
			FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		}

		@Override
		public EnumSlot getSlot(ItemStack stack)
		{
			return EnumSlot.EYES;
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
			return CyberwareAPI.getCyberware(other).isEssential(other);
		}

		@Override
		public boolean isEssential(ItemStack stack)
		{
			return true;
		}
		
		@SubscribeEvent
		public void handleSpiderNightVision(CyberwareUpdateEvent event)
		{
			EntityLivingBase e = event.getEntityLiving();
			
			if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(Items.SPIDER_EYE)))
			{
				e.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, -53, true, false));
			}
			else
			{
				PotionEffect effect = e.getActivePotionEffect(MobEffects.NIGHT_VISION);
				if (effect != null && effect.getAmplifier() == -53)
				{
					e.removePotionEffect(MobEffects.NIGHT_VISION);
				}
			}
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onDrawScreenPost(RenderGameOverlayEvent.Pre event)
		{
			if (event.getType() == ElementType.CROSSHAIRS)
			{
				EntityPlayer p = Minecraft.getMinecraft().thePlayer;
				if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(Items.SPIDER_EYE)))
				{
					GlStateManager.translate(0, event.getResolution().getScaledHeight() / 5, 0);
				}
			}
		}
		
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
		{
			if (event.getType() == ElementType.CROSSHAIRS)
			{
				EntityPlayer p = Minecraft.getMinecraft().thePlayer;
				if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(Items.SPIDER_EYE)))
				{
					GlStateManager.translate(0, -event.getResolution().getScaledHeight() / 5, 0);
				}
			}
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void handleSpiderVision(TickEvent.ClientTickEvent event)
		{
			if (event.phase == TickEvent.Phase.START)
			{
				EntityPlayer e = Minecraft.getMinecraft().thePlayer;
				
				if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(Items.SPIDER_EYE)))
				{
					if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() == null)
					{
						Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/spider.json"));
					}
				}
				else if (e != null && !e.isSpectator())
				{
					ShaderGroup sg = Minecraft.getMinecraft().entityRenderer.getShaderGroup();
					if (sg != null && sg.getShaderGroupName().equals("minecraft:shaders/post/spider.json"))
					{
						Minecraft.getMinecraft().entityRenderer.stopUseShader();
					}
				}
				
			}
		}

		@Override
		public List<String> getInfo(ItemStack stack)
		{
			List<String> ret = new ArrayList<String>();
			String[] desc = this.getDesciption(stack);
			if (desc != null && desc.length > 0)
			{
				String format = desc[0];
				if (format.length() > 0)
				{
					ret.addAll(Arrays.asList(desc));
				}
			}
			return ret;
		}

		private String[] getDesciption(ItemStack stack)
		{
			return I18n.format("cyberware.tooltip.spiderEye").split("\\\\n");
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
			return 5;
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
		
	}
}
