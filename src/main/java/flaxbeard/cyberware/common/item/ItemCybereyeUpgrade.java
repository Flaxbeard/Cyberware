package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.client.KeyBinds;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemCybereyeUpgrade extends ItemCyberware
{

	public ItemCybereyeUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (p != null)
		{

		}
	}

	@Override
	public ItemStack[] required(ItemStack stack)
	{
		if (stack.getItemDamage() > 2 && stack.getItemDamage() != 4)
		{
			return new ItemStack[] { new ItemStack(CyberwareContent.cybereyes), new ItemStack(this, 1, 2) };
		}
		
		return new ItemStack[] { new ItemStack(CyberwareContent.cybereyes) };
	}
	
	@SubscribeEvent
	public void handleFogColors(FogColors event)
	{
		//event.setRed(0.5F);
		//event.setBlue(0.5F);
		//event.setGreen(0.5F);

	}
	
	private static List<EntityLivingBase> affected = new ArrayList<EntityLivingBase>();
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleHighlight(RenderTickEvent event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 3)))
		{
			
			
			if (event.phase == Phase.START)
			{
				affected.clear();
				float range = 25F;
				List<EntityLivingBase> test = p.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.posX - range, p.posY - range, p.posZ - range, p.posX + p.width + range, p.posY + p.height + range, p.posZ + p.width + range));
				for (EntityLivingBase e : test)
				{
					if (p.getDistanceToEntity(e) <= range && e != p && !e.isGlowing())
					{

						e.setGlowing(true);
						affected.add(e);
					}
				}
			}
			else if (event.phase == Phase.END)
			{
				
				for (EntityLivingBase e : affected)
				{
					e.setGlowing(false);
				}
				
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleFog(FogDensity event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 1)))
		{
			if (p.isInsideOfMaterial(Material.WATER))
			{
				event.setDensity(0.01F);
				event.setCanceled(true);
			}
			else if (p.isInsideOfMaterial(Material.LAVA))
			{
				event.setDensity(0.7F);
				event.setCanceled(true);
			}

		}

	}
	
	@SubscribeEvent
	public void handleNightVision(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)))
		{

			e.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 53, true, false));
		}
		else
		{
			PotionEffect effect = e.getActivePotionEffect(MobEffects.NIGHT_VISION);
			if (effect != null && effect.getAmplifier() == 53)
			{
				e.removePotionEffect(MobEffects.NIGHT_VISION);
			}
		}
	}
	
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleWaterVision(RenderBlockOverlayEvent event)
	{
		if (CyberwareAPI.isCyberwareInstalled(event.getPlayer(), new ItemStack(this, 1, 1)))
		{
			if (event.getBlockForOverlay().getMaterial() == Material.WATER || event.getBlockForOverlay().getMaterial() == Material.LAVA)
			{
				event.setCanceled(true);
			}
		}
	}
	
	private static boolean inUse = false;
	private static boolean wasInUse = false;

	private static boolean wasKeyDown = false;
	private static int zoomSettingOn = 0;
	private static float fov = 0F;
	private static float sensitivity = 0F;

	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickStart(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (event.phase == Phase.START)
		{
			wasInUse = inUse;
				
			EntityPlayer p = mc.thePlayer;
			
			if (!inUse && !wasInUse)
			{
				fov = mc.gameSettings.fovSetting;
				sensitivity = mc.gameSettings.mouseSensitivity;
			}
			
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 4)))
			{
				if (mc.gameSettings.thirdPersonView == 0)
				{
					if (KeyBinds.zoom.isPressed() && !wasKeyDown)
					{
						if (p.isSneaking())
						{
							zoomSettingOn = (zoomSettingOn + 4 - 1) % 4;
						}
						else
						{
							zoomSettingOn = (zoomSettingOn + 1) % 4;
						}
						wasKeyDown = true;
					}
					if (!KeyBinds.zoom.isPressed())
					{
						wasKeyDown = false;
					}
					
					switch (zoomSettingOn)
					{
						case 0:
							mc.gameSettings.fovSetting = fov;
							mc.gameSettings.mouseSensitivity = sensitivity;
							break;
						case 1:
							mc.gameSettings.fovSetting = fov;
							mc.gameSettings.mouseSensitivity = sensitivity;
							int i = 0;
							while (Math.abs((mc.gameSettings.fovSetting - ((fov + 5F)) / 2.0F)) > 2.5F && i < 200)
							{
								mc.gameSettings.fovSetting -= 2.5F;
								mc.gameSettings.mouseSensitivity -= 0.01F;
								i++;
							}
							break;
						case 2:
							mc.gameSettings.fovSetting = fov;
							mc.gameSettings.mouseSensitivity = sensitivity;
							i = 0;
							while (Math.abs((mc.gameSettings.fovSetting - ((fov + 5F)) / 5.0F)) > 2.5F && i < 200)
							{
								mc.gameSettings.fovSetting -= 2.5F;
								mc.gameSettings.mouseSensitivity -= 0.01F;
								i++;
							}
							break;
						case 3:
							mc.gameSettings.fovSetting = fov;
							mc.gameSettings.mouseSensitivity = sensitivity;
							i = 0;
							while (Math.abs((mc.gameSettings.fovSetting - ((fov + 5F)) / 12.0F)) > 2.5F && i < 200)
							{
								mc.gameSettings.fovSetting -= 2.5F;
								mc.gameSettings.mouseSensitivity -= 0.01F;
								i++;
							}
							break;
					}
				}
				
			}
			else
			{
				zoomSettingOn = 0;
			}
			inUse = zoomSettingOn != 0;

			if (!inUse && wasInUse)
			{
				mc.gameSettings.fovSetting = fov;
				mc.gameSettings.mouseSensitivity = sensitivity;
			}

		}
	}
	
	@Override
	public String[] getDesciption(ItemStack stack)
	{
		if (stack.getItemDamage() != 4) return super.getDesciption(stack);
		
		String before = I18n.format("cyberware.tooltip.cybereyeUpgrades.4.before");
		if (before.length() > 0) before = before + " ";
		String after = I18n.format("cyberware.tooltip.cybereyeUpgrades.4.after");
		if (after.length() > 0) after = " " + after;
		return new String[] { before + Keyboard.getKeyName(KeyBinds.zoom.getKeyCode()) + after };
	}
	
}
