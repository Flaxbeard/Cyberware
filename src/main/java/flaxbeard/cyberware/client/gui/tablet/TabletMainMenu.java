package flaxbeard.cyberware.client.gui.tablet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.tablet.IListMenu;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.gui.GuiTablet;

public class TabletMainMenu implements ITabletPage, IScrollWheel, IListMenu
{
	public static class MenuElement implements IListMenuItem
	{
		private final String unlocalizedName;
		private final int iconIndex;
		
		public MenuElement(String unlocalizedName, int iconIndex)
		{
			this.unlocalizedName = unlocalizedName;
			this.iconIndex = iconIndex;
		}
		
		@Override
		public void renderText(GuiTablet tablet, int x, int y, boolean hovered)
		{
			String s = I18n.format(unlocalizedName);
			
			tablet.drawStringSmall(s, 10, 2, hovered ? 0x34B1C7 : 0x188EA2);

			
		}

		@Override
		public void render(GuiTablet tablet, int x, int y, boolean hovered)
		{
		
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTablet.TABLETHD);
			
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.color(1F, 1F, 1F, hovered ? 0.8F : 0.6F);
			tablet.drawTexturedModalRect(x, y, 128, 180 + iconIndex * 8, 8, 8);
			
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
		
	}
	
	private int scroll = 0;
	private List<IListMenuItem> items;
	
	public TabletMainMenu()
	{
		items = new ArrayList<IListMenuItem>();
	}
	
	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks)
	{
		
		String version = Cyberware.VERSION.equals("@VERSION@") ? "Developer Build" : Cyberware.VERSION;
		String s = I18n.format("cyberware.gui.tablet.menutitle", version);
		tablet.drawString(s, 20, 15, 0x34B1C7);
		
		for (int i = 0; i < items.size(); i++)
		{
			GlStateManager.pushMatrix();
			IListMenuItem item = items.get(i);
			boolean hovered = mouseX > 0 && mouseX < width && mouseY >= 31 + 14 * i && mouseY < 31 + 14 * (i + 1);
			item.renderText(tablet, 20, 31 + 13 * i, hovered);
			GlStateManager.popMatrix();
		}
		
		for (int i = 0; i < items.size(); i++)
		{
			GlStateManager.pushMatrix();
			IListMenuItem item = items.get(i);
			boolean hovered = mouseX > 0 && mouseX < width && mouseY >= 31 + 14 * i && mouseY < 31 + 14 * (i + 1);
			item.render(tablet, 20, 31 + 13 * i, hovered);
			GlStateManager.popMatrix();
		}
		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, 100, 1);





	}

	@Override
	public int getWidth(int defaultWidth, int ticksOpen, float partialTicks)
	{
		return defaultWidth;
	}

	@Override
	public ITabletPage getParent()
	{
		return null;
	}

	@Override
	public boolean leftButtonOn(int ticksOpen, float partialTicks)
	{
		return true;
	}

	@Override
	public boolean rightButtonOn(int ticksOpen, float partialTicks)
	{
		return true;
	}

	@Override
	public void setScrollAmount(int amount)
	{
		scroll = amount;
	}

	@Override
	public int getScrollAmount()
	{
		return scroll;
	}

	@Override
	public int getHeight(int height, int ticksOpen, float partialTicks)
	{
		return 13 * items.size() + 62;
	}

	@Override
	public List<IListMenuItem> getItems()
	{
		return items;
	}

	@Override
	public void addItem(IListMenuItem item)
	{
		items.add(item);
	}

	@Override
	public void removeItem(IListMenuItem item)
	{
		items.remove(item);
	}

}
