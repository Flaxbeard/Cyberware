package flaxbeard.cyberware.client.gui.tablet;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import flaxbeard.cyberware.api.tablet.IListMenu.IListMenuItem;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.ShaderHelper;
import flaxbeard.cyberware.client.gui.GuiTablet;
import flaxbeard.cyberware.client.gui.tablet.CatalogSort.AlphaSort;
import flaxbeard.cyberware.client.gui.tablet.CatalogSort.ModSort;
import flaxbeard.cyberware.client.gui.tablet.CatalogSort.SlotSort;
import flaxbeard.cyberware.client.gui.tablet.CatalogSort.TagSort;

public class TabletCatalog implements ITabletPage, IScrollWheel
{

	private int scroll = 0;
	private List<TabletCatalogItem> items;
	private List<CatalogSort> sorts;
	private int currentSort = 0;
	private boolean sortOpen = false;
	private boolean showHidden = true;
	
	public TabletCatalog()
	{
		items = new ArrayList<TabletCatalogItem>();
		sorts = new ArrayList<CatalogSort>();
		sorts.add(new SlotSort());
		sorts.add(new AlphaSort());
		sorts.add(new TagSort());
		sorts.add(new ModSort());
	}
	
	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks, boolean leftDown)
	{

		
		String s = I18n.format("Parts Catalog");
		tablet.drawString(s, 20, 15, 0x34B1C7);
				
		if (currentSort > sorts.size())
		{
			currentSort = 0;
		}
		
		CatalogSort sort = sorts.get(currentSort);
		sort.render(tablet, 20, 31, width, mouseX, mouseY, leftDown, showHidden);
		
		int y = 28;
		int maxWidth = 0;
		
		for (CatalogSort sort2 : sorts)
		{
			s = I18n.format(sort2.getUnlocalizedName());
			int tw = tablet.getStringWidthSmall(s);
			if (tw > maxWidth)
			{
				maxWidth = tw;
			}
		}

		s = I18n.format("cyberware.gui.tablet.catalog.sort") + " ";
		tablet.drawStringSmall(s, width - tablet.getStringWidthSmall(s) - 20 - maxWidth, y, 0x188EA2);
		
		String showu = I18n.format("cyberware.gui.tablet.catalog.showUnknown") + " ";
		tablet.drawStringSmall(showu, width - tablet.getStringWidthSmall(showu) - 20 - 3, y - 8, 0x188EA2);
		


		s = I18n.format(sort.getUnlocalizedName());
		tablet.drawStringSmall(s, width - maxWidth - 20, y, 0x188EA2);
		
		if (sortOpen)
		{			
			int i = 0;
			for (CatalogSort sort2 : sorts)
			{
				y += 6;
				s = I18n.format(sort2.getUnlocalizedName());
				tablet.drawStringSmall(s, width - maxWidth - 20, y, 0x188EA2);
				
				if (leftDown && mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1)
				{
					sortOpen = false;
					currentSort = i;
				}
				i++;
			}
		}
		
		y = 28;

		if (!sortOpen && leftDown && mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1)
		{
			sortOpen = true;
		}
		else if (leftDown)
		{
			sortOpen = false;
		}


		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, tablet.getStringWidth(I18n.format("Parts Catalog")) + 5, 1);



		y = 28;
		
		if (!sortOpen)
		{
			if (mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1)
			{
				GlStateManager.color(1F, 1F, 1F, 0.101F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 1, 256 - maxWidth - 4, 249, maxWidth + 4, 6);

			}
		}
		else
		{
			boolean hover = mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1;
			GlStateManager.color(1F, 1F, 1F, hover ? 0.2F : 0.101F);
			tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 1, 256 - maxWidth - 4, 249, maxWidth + 4, 6);
			for (CatalogSort sort2 : sorts)
			{
				y += 6;
				hover = mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1;
				GlStateManager.color(1F, 1F, 1F, hover ? 0.2F : 0.101F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 1, 256 - maxWidth - 4, 249, maxWidth + 4, 6);
			}
		}
		
		
		boolean hover = false;
		if (mouseX >= width - 23 - tablet.getStringWidthSmall(showu) && mouseX <= width - 23 + 5 && mouseY >= 20 && mouseY <= 20 + 5)
		{
			hover = true;
		}
		
		if (hover && leftDown)
		{
			showHidden = !showHidden;
		}
			
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, hover ? 0.8F : 0.6F);
		GlStateManager.translate(width - 23, 20 - .5F, 0);
		GlStateManager.scale(.5F, .5F, 1F);
		tablet.drawTexturedModalRect(0, 0, showHidden ? 125 : 135, 245, 9, 9);
		GlStateManager.popMatrix();



	}

	@Override
	public int getWidth(int defaultWidth, int ticksOpen, float partialTicks)
	{
		return defaultWidth;
	}

	@Override
	public ITabletPage getParent()
	{
		return TabletContent.mainMenu;
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
	public int getHeight(GuiTablet tablet, int width, int height, int ticksOpen, float partialTicks)
	{
		return sorts.get(currentSort).getHeight(showHidden) + 62;
	}

	public void addItem(TabletCatalogItem tabletCatalogItem)
	{
		this.items.add(tabletCatalogItem);
		for (CatalogSort category : sorts)
		{
			category.addItem(tabletCatalogItem);
		}
	}

}
