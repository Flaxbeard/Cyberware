package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.client.ClientUtils;
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
	
	// http://stackoverflow.com/a/16206356/1754640
	private static class NotificationStack<T> extends Stack<T>
	{
		private int maxSize;

		public NotificationStack(int size)
		{
			super();
			this.maxSize = size;
		}

		@Override
		public Object push(Object object)
		{
			while (this.size() >= maxSize)
			{
				this.remove(0);
			}
			return super.push((T) object);
		}
	}
	
	public static class NotificationInstance
	{
		private float time;
		private INotification notification;
		
		public NotificationInstance(float time, INotification notification)
		{
			this.time = time;
			this.notification = notification;
		}
		
		public float getCreatedTime()
		{
			return time;
		}
		
		public INotification getNotification()
		{
			return notification;
		}
	}
	
	public static interface INotification
	{
		public void render(int x, int y);
		public int getDuration();
	}
	
	@SideOnly(Side.CLIENT)
	private static class NotificationArmor implements INotification
	{
		private boolean light;
		
		private NotificationArmor(boolean light)
		{
			this.light = light;
		}

		@Override
		public void render(int x, int y)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(HUD_TEXTURE);
			ClientUtils.drawTexturedModalRect(x, y + 1, 0, 25, 15, 14);
			if (light)
			{
				ClientUtils.drawTexturedModalRect(x + 9, y + 1 + 7, 15, 25, 7, 9);
			}
			else
			{
				ClientUtils.drawTexturedModalRect(x + 8, y + 1 + 7, 22, 25, 8, 9);
			}
		}

		@Override
		public int getDuration()
		{
			return 20;
		}
	}
	
	public static void addNotification(NotificationInstance not)
	{
		notifications.push(not);
	}
	
	public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Cyberware.MODID + ":textures/gui/hud.png");
	private static Iterable<ItemStack> inv;
	private static boolean lightArmor = false;
	private static Stack<NotificationInstance> notifications = new NotificationStack(5);
	private static int cachedCap = 0;
	private static int cachedTotal = 0;
	private static float cachedPercent = 0;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 2)))
		{
			float currTime = p.ticksExisted + event.getPartialTicks();
			
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			ICyberwareUserData data = CyberwareAPI.getCapability(p);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(HUD_TEXTURE);
	
			ScaledResolution res = event.getResolution();
			int left = 5;
			int top = 5;//- right_height;
			
			Iterable<ItemStack> currInv = p.getArmorInventoryList();
			if (currInv != inv)
			{
				inv = currInv;
				boolean temp = lightArmor;
				lightArmor = updateLightArmor();
				if (lightArmor != temp)
				{
					addNotification(new NotificationInstance(currTime, new NotificationArmor(lightArmor)));
				}
			}

			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			
			if (p.ticksExisted % 20 == 0)
			{
				cachedPercent = data.getPercentFull();
				cachedCap = data.getCapacity();
				cachedTotal = data.getStoredPower();
			}
			if (cachedPercent != -1)
			{
				int amount = Math.round((21F * cachedPercent));


				ClientUtils.drawTexturedModalRect(left, top, 0, 0, 13, 2 + (21 - amount));
				ClientUtils.drawTexturedModalRect(left, top + 2 + (21 - amount), 13, 2 + (21 - amount), 13, amount + 2);
				
				ClientUtils.drawTexturedModalRect(left, top + 2 + (21 - amount), 26, 2 + (21 - amount), 13, amount + 2);
				
				fr.drawStringWithShadow(cachedTotal + " / " + cachedCap, left + 15, top + 8, 0x4CFF00);
				top += 28;
			}
			
			List<NotificationInstance> nTR = new ArrayList<NotificationInstance>();
			for (int i = 0; i < notifications.size(); i++)
			{
				NotificationInstance ni = notifications.get(i);
				INotification notification = ni.getNotification();
				if (currTime - ni.getCreatedTime() < notification.getDuration() + 25)
				{
					double pct = Math.max(0F, ((currTime - ni.getCreatedTime() - notification.getDuration()) / 30F));
	
					float move = (float) ((20 * Math.sin(pct * (Math.PI / 2F))));
					
					GL11.glPushMatrix();
					GL11.glTranslatef(0F, move, 0F);
					int index = (notifications.size() - 1) - i;
					notification.render(5 + index * 18, res.getScaledHeight() - 5 - 14);
					GL11.glPopMatrix();
				}
				else
				{
					nTR.add(ni);
				}
			}
			
			for (NotificationInstance ni : nTR)
			{
				notifications.remove(ni);
			}
			
			RenderItem ir = Minecraft.getMinecraft().getRenderItem();
			List<ItemStack> stacks = data.getPowerOutages();
			List<Integer> stackTimes = data.getPowerOutageTimes();
			List<Integer> toRemove = new ArrayList<Integer>();
			left -= 1;
			float zL = ir.zLevel;
			ir.zLevel = -300;
			for (int i = stacks.size() - 1; i >= 0; i--)
			{
				ItemStack stack = stacks.get(i);
				if (stack != null)
				{
					int time = stackTimes.get(i);
					boolean keep = p.ticksExisted - time < 50;
					double pct = Math.max(0F, ((currTime - time - 20) / 30F));

					float move = (float) ((20 * Math.sin(pct * (Math.PI / 2F))));
					if (keep)
					{
						GL11.glPushMatrix();
						GL11.glTranslatef(-move, 0F, 0F);
						
						fr.drawStringWithShadow("!", left + 14, top + 8, 0xFF0000);
						
						RenderHelper.enableStandardItemLighting();
						ir.renderItemAndEffectIntoGUI(stack, left, top);
						RenderHelper.disableStandardItemLighting();

						GL11.glPopMatrix();
						top += 18;
					}
					else
					{
						toRemove.add(i);
					}
				}
			}
			ir.zLevel = zL;
			
			for (int i : toRemove)
			{
				stacks.remove(i);
				stackTimes.remove(i);
			}
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private boolean updateLightArmor()
	{
		for (ItemStack stack : inv)
		{
			if (stack != null && stack.getItem() instanceof ItemArmor)
			{
				if (((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(EntityEquipmentSlot.CHEST) > 4)
				{
					return false;
				}
			}
			else if (stack != null && stack.getItem() instanceof ISpecialArmor)
			{
				if (((ISpecialArmor) stack.getItem()).getProperties(Minecraft.getMinecraft().thePlayer, stack, DamageSource.cactus, 1, 1).AbsorbRatio * 25D > 4)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public List<String> getStackDesc(ItemStack stack)
	{
		if (stack.getItemDamage() != 4) return super.getStackDesc(stack);
		
		String before = I18n.format("cyberware.tooltip.cybereyeUpgrades.4.before");
		if (before.length() > 0) before = before + " ";
		String after = I18n.format("cyberware.tooltip.cybereyeUpgrades.4.after");
		if (after.length() > 0) after = " " + after;
		return new ArrayList(Arrays.asList(new String[] { before + Keyboard.getKeyName(KeyBinds.zoom.getKeyCode()) + after }));
	}
	
}
