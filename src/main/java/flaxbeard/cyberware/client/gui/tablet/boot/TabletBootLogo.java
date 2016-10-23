package flaxbeard.cyberware.client.gui.tablet.boot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.gui.GuiTablet;
import flaxbeard.cyberware.client.gui.tablet.TabletContent;

public class TabletBootLogo implements ITabletPage
{
	
	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();

		
		if (ticks > 5)
		{
			int biggest = 115;

			
			int progress = ((ticks - 5) / 2) * 3;
			progress = Math.min(progress, 64);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0F, 100F);
			GlStateManager.translate(width / 2, height / 2, 0F);
			
			float rot = Math.min(89, Math.abs(90 - (ticks * 3 % 180)));
			
			if (ticks >= 210)
			{
				rot = 0;
			}
	
			GlStateManager.rotate(rot, 0F, 1F, 0F);
			GlStateManager.translate(-(width / 2), -(height / 2), 0F);
			
			float bri = ticks > (30 + 30 + biggest) ? 1F : .7F;
			
			GlStateManager.color(bri, bri, bri, 0.6F);
			tablet.drawTexturedModalRect(width / 2 - 27, height / 2 - 32, 52, 180, 54, progress);
			GlStateManager.translate(0F, 0F, 1F);
			GlStateManager.color(bri, bri, bri, 0.15F);
			tablet.drawTexturedModalRect(width / 2 - 26, height / 2 - 32, 52, 180, 54, progress + 3);
			GlStateManager.translate(0F, 0F, -2F);
			GlStateManager.color(bri, bri, bri, 0.15F);
			tablet.drawTexturedModalRect(width / 2 - 28, height / 2 - 32, 52, 180, 54, progress + 1);

			GlStateManager.popMatrix();
			
			if (ticks > 30)
			{
				int count = (int) ((ticks - 30));
				int max = Math.min(30, count);
				if ((biggest - count + max) > 0)
				{
					
					GlStateManager.pushMatrix();
					GlStateManager.translate(0, 0, 1000F);
					String[] s = new String[100];
					
					for (int i = 0; i < max && i < (biggest - count + max); i++)
					{
						tablet.drawStringSmall(I18n.format("cyberware.gui.tabletBoot." + ((i + count - max) % biggest)), 5, 5 + 5 * i, 0x188EA2);
					}
					GlStateManager.popMatrix();
				}
			}
			
			if (ticks > 210)
			{
				String s = I18n.format("cyberware.gui.tablet.welcome");
				int sw = tablet.getStringWidth(s);
				tablet.drawString(s, (width - sw) / 2, (height / 2) + 35, 0x188EA2);
				

			}
		}
		
		if (ticks >= 240)
		{
			tablet.setPage(TabletContent.mainMenu);
		}
		
		if (ticks >= 200)
		{
			//tablet.setPage(new TabletBootLoading());
		}
		
		
		GlStateManager.disableBlend();
		
	}

	@Override
	public int getWidth(int defaultWidth, int ticks, float partialTicks)
	{
		if (ticks < 20)
		{
			return 2 * (int) (90 * Math.sin((ticks) * Math.PI / 40F));
		}
		return defaultWidth;
	}

	@Override
	public ITabletPage getParent()
	{
		return null;
	}

	@Override
	public boolean leftButtonOn(int ticks, float partialTicks)
	{
		return ticks > 22 && (ticks > 60 || (ticks / 2) % 4 == 0);
	}
	
	@Override
	public boolean rightButtonOn(int ticks, float partialTicks)
	{
		return ticks > 45 && (ticks > 72 || (ticks / 3) % 3 == 0);
	}
}
