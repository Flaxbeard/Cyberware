package flaxbeard.cyberware.client.gui;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.handler.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class GuiTablet extends GuiScreen
{
	public static final ResourceLocation TABLET = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet.png");

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL11.glPushMatrix();
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

		int specialScaleFactor = 4;

		int realWidth = (sr.getScaledWidth() * sr.getScaleFactor()) / specialScaleFactor;
		int realHeight = (sr.getScaledHeight() * sr.getScaleFactor()) / specialScaleFactor;
		

		int tabletWidth = 280;
		int tabletHeight = 180;
		
		float scaleDownFactor = 1F / sr.getScaleFactor();
		GL11.glScalef(scaleDownFactor, scaleDownFactor, 1F);
		GL11.glScalef(specialScaleFactor, specialScaleFactor, 1F);
		
		int i = (realWidth - tabletWidth) / 2;
		int j = (realHeight - tabletHeight) / 2;
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(TABLET);
		this.drawTexturedModalRect(i, j, 0, 0, tabletWidth / 2, tabletHeight);
		this.drawTexturedModalRect(i + tabletWidth / 2, j, 11, 0, tabletWidth / 2, tabletHeight);
		
		boolean u = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(true);
		fontRendererObj.drawString("Test test test", i + 12, j + 12, 0x188EA2);
		fontRendererObj.setUnicodeFlag(u);
		
		GL11.glPopMatrix();
	}
}
