package flaxbeard.cyberware.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.HudElementBase;
import flaxbeard.cyberware.api.hud.IHudElement.EnumAnchorHorizontal;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.handler.HudHandler;

public class PowerDisplay extends HudElementBase
{
	private static int cachedCap = 0;
	private static int cachedTotal = 0;
	private static float cachedPercent = 0;

	private static int cachedProd = 0;
	private static int cachedCons = 0;
	
	public PowerDisplay()
	{
		super("cyberware:power");
		setDefaultX(5);
		setDefaultY(5);
		setHeight(25);
		setWidth(101);
	}

	@Override
	public void renderElement(int x, int y, EntityPlayer p, ScaledResolution resolution, boolean hudjackAvailable, boolean isConfigOpen, float partialTicks)
	{
		if (!isHidden() && hudjackAvailable)
		{
			boolean flipHoriz = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;

			float currTime = p.ticksExisted + partialTicks;
			
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			ICyberwareUserData data = CyberwareAPI.getCapability(p);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(HudHandler.HUD_TEXTURE);
	
			
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			
			if (p.ticksExisted % 20 == 0)
			{
				cachedPercent = data.getPercentFull();
				cachedCap = data.getCapacity();
				cachedTotal = data.getStoredPower();
				cachedProd = data.getProduction();
				cachedCons = data.getConsumption();
			}
			
			float[] color = CyberwareAPI.getHUDColor();
			int colorHex = CyberwareAPI.getHUDColorHex();
			
			if (cachedPercent != -1)
			{
				int amount = Math.round((21F * cachedPercent));
	
				boolean danger = (cachedPercent <= .2F);
				boolean superDanger = danger && (cachedPercent <= .05F);
				int xOffset = (danger ? 39 : 0);
				
				if (!superDanger || p.ticksExisted % 4 != 0)
				{
					int moveX = flipHoriz ? (x + getWidth() - 13) : x;
					GlStateManager.pushMatrix();
					if (!danger) GlStateManager.color(color[0], color[1], color[2]);
					ClientUtils.drawTexturedModalRect(moveX, y, xOffset, 0, 13, 2 + (21 - amount));
					ClientUtils.drawTexturedModalRect(moveX, y + 2 + (21 - amount), 13 + xOffset, 2 + (21 - amount), 13, amount + 2);
					
					ClientUtils.drawTexturedModalRect(moveX, y + 2 + (21 - amount), 26 + xOffset, 2 + (21 - amount), 13, amount + 2);
					GlStateManager.popMatrix();
	
					String output = cachedTotal + " / " + cachedCap;
					int textX = flipHoriz ? x + getWidth() - 15 - fr.getStringWidth(output) : x + 15;
					fr.drawStringWithShadow(output, textX, y + 4, danger ? 0xFF0000 : colorHex);
					
					output = "-" + cachedCons + " / +" + cachedProd;
					textX = flipHoriz ? x + getWidth() - 15 - fr.getStringWidth(output) : x + 15;
					fr.drawStringWithShadow(output, textX, y + 14, danger ? 0xFF0000 : colorHex);
				}
			}
			
			GL11.glPopMatrix();
		}
	}
}
