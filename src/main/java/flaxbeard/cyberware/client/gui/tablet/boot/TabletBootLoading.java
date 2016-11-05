package flaxbeard.cyberware.client.gui.tablet.boot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.gui.GuiTablet;

public class TabletBootLoading implements ITabletPage
{
	
	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks, boolean leftDown)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
		
		if (ticks < 92 || ((ticks / 2) %2 == 0 && ticks < 110))
		{
			
			int move = (ticks) * 2;
			int move1 = Math.min(76, move);
			tablet.drawTexturedModalRect(width / 2 - 10, 85 + 76 - move1, 0, 256 - move1, 20, move1);
			
			int move2 = Math.min(76, Math.max(move - 76, 0));
			tablet.drawTexturedModalRect(width / 2 - 10, 85 - move2, 0, 256 - move2, 20, move2);

			int move3 = Math.min(10, Math.max(move - 76 - 76, 0));
			tablet.drawTexturedModalRect(width / 2 - 10, 85 - 76 - move3, 0, 256 - move3, 20, move3);

		}
		
		if (ticks >= 120)
		{
			tablet.setPage(new TabletBootLogo());
		}

		GlStateManager.disableBlend();
		
	}

	@Override
	public int getWidth(int defaultWidth, int ticks, float partialTicks)
	{
		return 0;
	}

	@Override
	public ITabletPage getParent()
	{
		return null;
	}
	
	@Override
	public boolean leftButtonOn(int ticks, float partialTicks)
	{
		return false;
	}
	
	@Override
	public boolean rightButtonOn(int ticks, float partialTicks)
	{
		return false;
	}

}
