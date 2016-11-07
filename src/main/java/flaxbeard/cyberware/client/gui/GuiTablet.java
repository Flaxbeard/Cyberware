package flaxbeard.cyberware.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.gui.tablet.TabletCatalogItem;
import flaxbeard.cyberware.client.gui.tablet.TabletContent;

public class GuiTablet extends GuiScreen
{
	public static final ResourceLocation TABLETHD = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablethd.png");
	public static final ResourceLocation TABLET_RESOURCES = new ResourceLocation(Cyberware.MODID + ":textures/gui/tabletResources.png");

	public static final ResourceLocation TABLET_OVERLAY = new ResourceLocation(Cyberware.MODID + ":textures/gui/tabletOverlay.png");
	public static final ResourceLocation BLUE_PX = new ResourceLocation(Cyberware.MODID + ":textures/gui/brightbluepx.png");
	public static final ResourceLocation ANIMS = new ResourceLocation(Cyberware.MODID + ":textures/gui/tabletAnimations.png");

	private int openTicks = 0;
	private int lastTicks = 0;
	private static ITabletPage page = TabletContent.catalog;
	private boolean dragging;
	private int dragOffset = 0;
	
	private int specialScaleFactor = 4;
	private int pagei = 0;
	private int pagej = 0;
	
	public GuiTablet()
	{
		openTicks = Minecraft.getMinecraft().thePlayer.ticksExisted;
		//page = TabletContent.mainMenu;
		dragging = false;
		dragOffset = 0;
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	private boolean leftDown = false;
	private boolean rightDown = false;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if (page.getParent() != null && rightDown)
		{
			page = page.getParent();
		}
		boolean init = true;
		GlStateManager.pushMatrix();
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

		specialScaleFactor = 4;
		int tabletHeight = 180;

		
		while (tabletHeight * specialScaleFactor > sr.getScaledHeight() * sr.getScaleFactor() - 20 && specialScaleFactor > 0)
		{
			if (specialScaleFactor == 4)
			{
				specialScaleFactor -= 2;
			}
			else
			{
				specialScaleFactor--;
			}
		}

		int realWidth = (sr.getScaledWidth() * sr.getScaleFactor()) / specialScaleFactor;
		int realHeight = (sr.getScaledHeight() * sr.getScaleFactor()) / specialScaleFactor;

		lastTicks = Minecraft.getMinecraft().thePlayer.ticksExisted;
		int ticks = lastTicks - openTicks;
		int maxWidth = 180;
		
		width = maxWidth;
		if (page != null)
		{
			width = page.getWidth(maxWidth, ticks, partialTicks);
		}
		
		int tabletWidth = 38 * 2 + width;
		int maxTabletWidth = 180 + 38 * 2;
		
		float scaleDownFactor = 1F / sr.getScaleFactor();
		GlStateManager.scale(scaleDownFactor, scaleDownFactor, 1F);
		GlStateManager.scale(specialScaleFactor, specialScaleFactor, 1F);
		
		mouseY = (int) (mouseY * sr.getScaleFactor() / specialScaleFactor);
		mouseX = (int) (mouseX * sr.getScaleFactor() / specialScaleFactor);
		int i = (realWidth - maxTabletWidth) / 2;
		int i2 = (realWidth - tabletWidth) / 2;
		int j = (realHeight - tabletHeight) / 2;
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(TABLETHD);
		this.drawTexturedModalRect(i2, j, 0, 0, 38, tabletHeight);

		
		this.drawTexturedModalRect(i2 + 38, j, 38, 0, Math.min(width, 2), tabletHeight);
		width -= Math.min(width, 2);
		this.drawTexturedModalRect(i2 + tabletWidth - 38 - 2, j, 226 - 40, 0, Math.min(width, 2), tabletHeight);
		width -= Math.min(width, 2);
		
		if (width > 0)
		{
			int x = 0;
			while (width > 146)
			{
				this.drawTexturedModalRect(i2 + 40 + x, j, 40, 0, 146, tabletHeight);
				width -= 146;
				x += 146;
			}
			this.drawTexturedModalRect(i2 + 40 + x, j, 40, 0, width, tabletHeight);
		}
		
		this.drawTexturedModalRect(i2 + tabletWidth - 38, j, 226 - 38, 0, 38, tabletHeight);
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		scissorInternal(i2 + 32, j + 11, tabletWidth - 32 * 2, tabletHeight - 22);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(i + 32, j + 10, 0F);
		pagei = i + 32;
		pagej = j + 32;
		if (page != null)
		{
			int modMouseX = mouseX - 32 - i;
			int modMouseY = mouseY - 10 - j;
			if (page instanceof IScrollWheel)
			{
				IScrollWheel swpage = ((IScrollWheel) page);
				int h = swpage.getHeight(this, maxTabletWidth - 64, tabletHeight, ticks, partialTicks);
				h = Math.max(tabletHeight, h);
				
				
				int scroll = swpage.getScrollAmount();
				int howFar = Math.round((scroll * 154F / h));
				int howBig = Math.round((tabletHeight * 154F / h));
				
				
				int d = Mouse.getDWheel();
				if (d != 0 && !dragging)
				{
					scroll = scroll - (d / 5);
				}
				
				if (!Mouse.isButtonDown(0) && dragging)
				{
					dragging = false;
				}
				
				if (dragging)
				{
					scroll = (int) ((modMouseY - dragOffset) * h / 154F);
				}
				
				boolean hoveringOverBar = (modMouseY >= 3 && modMouseY <= 154 + 3 && modMouseX >= 180 && modMouseX <= 185) || dragging;
				boolean hovering = hoveringOverBar && (modMouseY >= howFar + 3 && modMouseY <= howFar + howBig + 3);
				
				if (Mouse.isButtonDown(0) && !dragging && hovering)
				{
					dragging = true;
					dragOffset = modMouseY - howFar;
				}
				else if (Mouse.isButtonDown(0) && !dragging && hoveringOverBar)
				{
					scroll = (int) ((modMouseY - (howBig / 2)) * h / 154F);
				}
				
				scroll = Math.max(0, scroll);
				scroll = Math.min(scroll, h - tabletHeight);
				swpage.setScrollAmount(scroll);
				
				if (h != tabletHeight)
				{
					int barWidth = hoveringOverBar ? 2 : 1;
					int barPos = hoveringOverBar ? 182 : 183;
					GlStateManager.enableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, hoveringOverBar ? 0.9F : 0.6F);
					this.drawTexturedModalRect(barPos, 3 + howFar, 226, 0, barWidth, howBig);
					this.drawTexturedModalRect(barPos, 3, 228, 0, barWidth, howFar);
					this.drawTexturedModalRect(barPos, 3 + howFar + howBig, 228, 0, barWidth, 154 - howFar - howBig);
					GlStateManager.disableBlend();
	
					GlStateManager.translate(0F, -scroll, 0F);
					modMouseY += scroll;
				}
				
			}
			
			page.render(this, maxTabletWidth - 64, tabletHeight - 20, modMouseX, modMouseY, ticks, partialTicks, leftDown);
			
		}
		GlStateManager.popMatrix();
		
		/*
		/*boolean u = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(false);
		fontRendererObj.drawString("Hello Yulife!", i + 36 + 12, j + 18, 0x188EA2);
		fontRendererObj.drawString("This is the tablet!", i + 36 + 12, j + 28, 0x188EA2);
		fontRendererObj.setUnicodeFlag(u);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TABLETHD);
		
		
		if (init && ticks < 161)
		{
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
			
			if (ticks < 92 || ((ticks / 2) %2 == 0) || ticks > 110)
			{
				
				int move = (ticks) * 2;
				int move1 = Math.min(76, move);
				this.drawTexturedModalRect(i + maxTabletWidth / 2 - 10, j + 95 + 76 - move1, 0, 256 - move1, 20, move1);
				
				int move2 = Math.min(76, Math.max(move - 76, 0));
				this.drawTexturedModalRect(i + maxTabletWidth / 2 - 10, j + 95 - move2, 0, 256 - move2, 20, move2);

				int move3 = Math.min(10, Math.max(move - 76 - 76, 0));
				this.drawTexturedModalRect(i + maxTabletWidth / 2 - 10, j + 95 - 76 - move3, 0, 256 - move3, 20, move3);

			}
	
			GlStateManager.disableBlend();
			
			if (ticks > 112)
			{
				Minecraft.getMinecraft().getTextureManager().bindTexture(ANIMS);
				int frame = ((ticks - 112) / 2) % 24;
				int yframe = frame % 21;
				int xframe = frame / 21;
				
				this.drawTexturedModalRect(i + maxTabletWidth / 2 - 6, j + tabletHeight / 2 - 6, xframe * 12, yframe * 12, 12, 12);
			}
		}
		
		
		if ((ticks > 180 && (ticks / 2) %2 == 0) || ticks > 200)
		{
			GlStateManager.enableBlend();
			
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0F, 100F);
			GlStateManager.translate(i + maxTabletWidth / 2, j + tabletHeight / 2, 0F);
			//GL11.glScaled(Math.abs(Math.sin(ticks / 10F)), 1F, 1F);
			float rot = Math.min(89, Math.abs(90 - (ticks * 3 % 180)));

			GL11.glRotatef(rot, 0F, 1F, 0F);
			GlStateManager.translate(-(i + maxTabletWidth / 2), -(j + tabletHeight / 2), 0F);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
			this.drawTexturedModalRect(i + maxTabletWidth / 2 - 27, j + tabletHeight / 2 - 32, 52, 180, 54, 64);
			GlStateManager.translate(0F, 0F, 1F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
			this.drawTexturedModalRect(i + maxTabletWidth / 2 - 26, j + tabletHeight / 2 - 32, 52, 180, 54, 64);
			GlStateManager.translate(0F, 0F, -2F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
			this.drawTexturedModalRect(i + maxTabletWidth / 2 - 28, j + tabletHeight / 2 - 32, 52, 180, 54, 64);
			
			GlStateManager.popMatrix();
			
			GlStateManager.disableBlend();
		}
		
		*/

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		GlStateManager.color(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TABLET_OVERLAY);
		this.drawTexturedModalRect(i2, j, 0, 0, 38, tabletHeight);
		this.drawTexturedModalRect(i2 + tabletWidth - 38, j, 226 - 38, 0, 38, tabletHeight);

		boolean leftButton = false;
		boolean rightButton = false;
		if (page != null)
		{
			leftButton = page.leftButtonOn(ticks, partialTicks);
			rightButton = page.rightButtonOn(ticks, partialTicks);
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TABLETHD);

		if (leftButton)
		{
			this.drawTexturedModalRect(i2 + 19, j + 84, 21, 180, 8, 12);
		}
		if (rightButton)
		{
			this.drawTexturedModalRect(i2 + tabletWidth - 26, j + 78, 42, 180, 8, 24);
		}
		
		GlStateManager.popMatrix();
		
		leftDown = rightDown = false;
	}
	
	public void setPage(ITabletPage page)
	{
		this.page = page;
		this.openTicks = lastTicks;
	}

	public RenderItem getItemRenderer()
	{
		return itemRender;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		leftDown = mouseButton == 0;
		rightDown = mouseButton == 1;
	}
	
	private void scissorInternal(int x, int y, int xSize, int ySize)
	{
		x = x * specialScaleFactor;
		ySize = ySize * specialScaleFactor;
		y = mc.displayHeight - (y * specialScaleFactor) - ySize;
		xSize = xSize * specialScaleFactor;
		GL11.glScissor(x, y, xSize, ySize);
	}
	
	public void scissor(int x, int y, int xSize, int ySize)
	{
		x = x + pagei;
		y = y;
		x = x * specialScaleFactor;
		ySize = ySize * specialScaleFactor;
		y = mc.displayHeight - (y * specialScaleFactor) - ySize;
		xSize = xSize * specialScaleFactor;
		GL11.glScissor(x, y, xSize, ySize);
	}
	
	public void drawString(String s, int x, int y, int color)
	{
		fontRendererObj.drawString(s, x, y, color);
	}
	
	public void drawStringSmall(String s, int x, int y, int color)
	{
		drawStringSmall(s, x, y, color, 0, 0);
	}
	
	public void drawStringSmall(String s, int x, int y, int color, int smallXOffset, int smallYOffset)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(.5F, .5F, 1F);
		GlStateManager.translate(smallXOffset, smallYOffset, 0);
		
		fontRendererObj.drawString(s, 0, 0, color);
		
		GlStateManager.popMatrix();
	}
	
	public int getStringWidth(String s)
	{
		return fontRendererObj.getStringWidth(s);
	}
	
	public int getStringWidthSmall(String s)
	{
		return fontRendererObj.getStringWidth(s) / 2;
	}
	
	public int drawSplitString(String s, int x, int y, int width, int color)
	{
		fontRendererObj.drawSplitString(s, x, y, width, color);
		return fontRendererObj.listFormattedStringToWidth(s, width).size();
	}
	
	public int drawSplitStringSmall(String s, int x, int y, int width, int color)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(.5F, .5F, 1F);
		fontRendererObj.drawSplitString(s, 0, 0, width * 2, color);
		GlStateManager.popMatrix();

		return fontRendererObj.listFormattedStringToWidth(s, width * 2).size();
	}
	
	public int getSplitStringLines(String s, int width)
	{
		return fontRendererObj.listFormattedStringToWidth(s, width).size();
	}
	
	public int getSplitStringSmallLines(String s, int width)
	{
		return fontRendererObj.listFormattedStringToWidth(s, width * 2).size();
	}
	
	
	
}
