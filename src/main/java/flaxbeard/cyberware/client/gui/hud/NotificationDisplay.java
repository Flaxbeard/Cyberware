package flaxbeard.cyberware.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.HudElementBase;
import flaxbeard.cyberware.api.hud.IHudElement;
import flaxbeard.cyberware.api.hud.INotification;
import flaxbeard.cyberware.api.hud.NotificationInstance;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.block.tile.TileEntityBeacon;
import flaxbeard.cyberware.common.handler.HudHandler;

public class NotificationDisplay extends HudElementBase
{
	
	public NotificationDisplay()
	{
		super("cyberware:notification");
		setDefaultX(5);
		setDefaultY(5 - 20);
		setWidth(5 * 18);
		setHeight(14 + 20 + 4);
		setDefaultVerticalAnchor(EnumAnchorVertical.BOTTOM);
	}
	
	private static Iterable<ItemStack> inv;
	private static int radioRange = -1;
	private static boolean lightArmor = false;
	private static final NotificationInstance[] examples = new NotificationInstance[] { new NotificationInstance(0, new NotificationArmor(true)),
		new NotificationInstance(0, new NotificationArmor(false)),
		new NotificationInstance(0, new NotificationArmor(true)),
		new NotificationInstance(0, new NotificationArmor(false))
	};

	@Override
	public void renderElement(int x, int y, EntityPlayer p, ScaledResolution resolution, boolean hudjackAvailable, boolean isConfigOpen, float partialTicks)
	{
		if (!isHidden() && hudjackAvailable)
		{
			boolean flipVert = getVerticalAnchor() == EnumAnchorVertical.TOP;
			boolean flipHoriz = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;

			float currTime = p.ticksExisted + partialTicks;
			
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			ICyberwareUserData data = CyberwareAPI.getCapability(p);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(HudHandler.HUD_TEXTURE);
	
			Iterable<ItemStack> currInv = p.getArmorInventoryList();
			if (currInv != inv)
			{
				inv = currInv;
				boolean temp = lightArmor;
				lightArmor = updateLightArmor();
				if (lightArmor != temp)
				{
					HudHandler.addNotification(new NotificationInstance(currTime, new NotificationArmor(lightArmor)));
				}
			}
			
			int temp = radioRange;
			radioRange = TileEntityBeacon.isInRange(p.worldObj, p.posX, p.posY, p.posZ);
			if (radioRange != temp)
			{
				HudHandler.addNotification(new NotificationInstance(currTime, new NotificationRadio(radioRange)));
			}
			// Render some placeholder notifications if the Hud config GUI is open so that the player can see what it'll look like in use
			if (isConfigOpen)
			{
				for (int i = 0; i < examples.length; i++)
				{
					NotificationInstance ni = examples[i];
					INotification notification = ni.getNotification();
					double pct = 0F;
					if (i == 0)
					{
						pct = (p.ticksExisted % 40F) / 40F;
					}
	
					float move = (float) ((20 * Math.sin(pct * (Math.PI / 2F))));
					
					GL11.glPushMatrix();
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					GL11.glTranslatef(0F, flipVert ? -move : move, 0F);
					int index = (examples.length - 1) - i;
					int xPos = flipHoriz ? (x + getWidth() - ((index + 1) * 18)) : (x + index * 18);
					notification.render(xPos, y + (flipVert ? 20 : 0));
					GL11.glPopMatrix();
					
				}
			}
			else
			{
				List<NotificationInstance> nTR = new ArrayList<NotificationInstance>();
				for (int i = 0; i < HudHandler.notifications.size(); i++)
				{
					NotificationInstance ni = HudHandler.notifications.get(i);
					INotification notification = ni.getNotification();
					if (currTime - ni.getCreatedTime() < notification.getDuration() + 25)
					{
						double pct = Math.max(0F, ((currTime - ni.getCreatedTime() - notification.getDuration()) / 30F));
		
						float move = (float) ((20 * Math.sin(pct * (Math.PI / 2F))));
						
						GL11.glPushMatrix();
						GL11.glColor3f(1.0F, 1.0F, 1.0F);
						GL11.glTranslatef(0F, flipVert ? -move : move, 0F);
						int index = (HudHandler.notifications.size() - 1) - i;
						int xPos = flipHoriz ? (x + getWidth() - ((index + 1) * 18)) : (x + index * 18);
						notification.render(xPos, y + (flipVert ? 20 : 0));
						GL11.glPopMatrix();
					}
					else
					{
						nTR.add(ni);
					}
				}
				
				for (NotificationInstance ni : nTR)
				{
					HudHandler.notifications.remove(ni);
				}
			}
			
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
			Minecraft.getMinecraft().getTextureManager().bindTexture(HudHandler.HUD_TEXTURE);
			GL11.glPushMatrix();
			float[] color = CyberwareAPI.getHUDColor();
			GL11.glColor3f(color[0], color[1], color[2]);
			ClientUtils.drawTexturedModalRect(x, y + 1, 0, 25, 15, 14);
			GL11.glPopMatrix();
			GL11.glColor3f(1F, 1F, 1F);

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
	
	@SideOnly(Side.CLIENT)
	private static class NotificationRadio implements INotification
	{
		private int tier;
		
		private NotificationRadio(int tier)
		{
			this.tier = tier;
		}

		@Override
		public void render(int x, int y)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(HudHandler.HUD_TEXTURE);
			if (tier > 0)
			{
				GlStateManager.pushMatrix();
				float[] color = CyberwareAPI.getHUDColor();
				GL11.glColor3f(color[0], color[1], color[2]);
				ClientUtils.drawTexturedModalRect(x, y + 1, 13, 39, 15, 14);
				GlStateManager.popMatrix();
				
				String v = tier == 1 ? I18n.format("cyberware.gui.radioInternal") : Integer.toString(tier - 1);
				FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
				fr.drawStringWithShadow(v, x + 15 - fr.getStringWidth(v), y + 9, 0xFFFFFF);
			}
			else
			{
				float[] color = CyberwareAPI.getHUDColor();
				GL11.glColor3f(color[0], color[1], color[2]);
				ClientUtils.drawTexturedModalRect(x, y + 1, 28, 39, 15, 14);

			}
		}

		@Override
		public int getDuration()
		{
			return 40;
		}
	}
}
