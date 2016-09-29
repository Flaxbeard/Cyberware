package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemCybereyeUpgrade extends ItemCyberware implements IMenuItem, IHudjack
{

	public ItemCybereyeUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		if (stack.getItemDamage() > 2 && stack.getItemDamage() != 4)
		{
			return new ItemStack[][] { 
					new ItemStack[] { new ItemStack(CyberwareContent.cybereyes) }, 
					new ItemStack[] { new ItemStack(this, 1, 2) }};
		}
		
		return new ItemStack[][] { 
				new ItemStack[] { new ItemStack(CyberwareContent.cybereyes) }};
	}

	private static List<EntityLivingBase> affected = new ArrayList<EntityLivingBase>();
	
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleHighlight(RenderTickEvent event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		ItemStack testItem = new ItemStack(this, 1, 3);
		if (CyberwareAPI.isCyberwareInstalled(p, testItem) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(p, testItem)))
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
	public void handleNightVision(CyberwareUpdateEvent event)
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

	private static int zoomSettingOn = 0;
	private static float fov = 0F;
	private static float sensitivity = 0F;
	private static EntityPlayer player = null;

	
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
				player = p;

				if (mc.gameSettings.thirdPersonView == 0)
				{
					
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
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 3 || stack.getItemDamage() == 2 || stack.getItemDamage() == 4;
	}

	@Override
	public void use(Entity e, ItemStack stack)
	{
		if (stack.getItemDamage() == 4)
		{
			if (e == this.player)
			{
				if (e.isSneaking())
				{
					zoomSettingOn = (zoomSettingOn + 4 - 1) % 4;
				}
				else
				{
					zoomSettingOn = (zoomSettingOn + 1) % 4;
				}
			}
			return;
		}
		EnableDisableHelper.toggle(stack);
	}

	@Override
	public String getUnlocalizedLabel(ItemStack stack)
	{
		if (stack.getItemDamage() == 4)
		{
			return "cyberware.gui.active.zoom";
		}
		return EnableDisableHelper.getUnlocalizedLabel(stack);
	}

	private static final float[] f = new float[] { 1F, 0F, 0F };
	
	@Override
	public float[] getColor(ItemStack stack)
	{
		if (stack.getItemDamage() == 4)
		{
			return null;
		}
		return EnableDisableHelper.isEnabled(stack) ? f : null;
	}

	@Override
	public boolean isActive(ItemStack stack)
	{
		return stack.getItemDamage() == 2 && EnableDisableHelper.isEnabled(stack);
	}
}
