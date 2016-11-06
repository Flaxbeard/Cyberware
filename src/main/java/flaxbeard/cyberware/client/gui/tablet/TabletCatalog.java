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
	public static class CatalogMenuItem implements IListMenuItem
	{
		private final TabletCatalogItem page;
		
		public CatalogMenuItem(TabletCatalogItem page)
		{
			this.page = page;
		}

		@Override
		public void renderText(GuiTablet tablet, int x, int y, boolean hovered)
		{
			String s = page.getItem().getDisplayName();
			
			tablet.drawStringSmall(s, x + 9, y + 3, hovered ? 0x34B1C7 : 0x188EA2, 0, 0);

			
		}

		@Override
		public void render(GuiTablet tablet, int x, int y, boolean hovered)
		{
			RenderHelper.enableGUIStandardItemLighting();

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1, 0);
			GlStateManager.scale(.5F, .5F, .5F);
			ShaderHelper.greyscale(hovered ? .8F : .6F);
			
			tablet.getItemRenderer().renderItemAndEffectIntoGUI(tablet.mc.thePlayer, page.getItem(), 0, 0);
			
			ShaderHelper.releaseShader();
			GlStateManager.popMatrix();

			RenderHelper.disableStandardItemLighting();
		}
		
	}
	
	private int scroll = 0;
	private List<TabletCatalogItem> items;
	private List<CatalogSort> sorts;
	private int currentSort = 0;
	private boolean sortOpen = false;
	
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
		sort.render(tablet, 20, 31, width, mouseX, mouseY, leftDown);
		
		int y = 20;
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

		s = "Sorted ";
		tablet.drawStringSmall(s, width - tablet.getStringWidthSmall(s) - 20 - maxWidth, y, 0x188EA2);

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
			
			if (leftDown)
			{
				sortOpen = false;
			}
		}
		
		
		if (leftDown && mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1)
		{
			sortOpen = !sortOpen;
		}


		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, tablet.getStringWidth(I18n.format("Parts Catalog")) + 5, 1);



		y = 20;
		
		if (!sortOpen)
		{
			if (mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1)
			{
				GlStateManager.color(1F, 1F, 1F, 0.101F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 2, 256 - maxWidth - 4, 249, maxWidth + 4, 7);
				
				GlStateManager.color(1F, 1F, 1F, 0.3F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 2, 256 - maxWidth - 4, 245, maxWidth + 4, 3);
			}
			else
			{
				GlStateManager.color(1F, 1F, 1F, 0.15F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 2, 256 - maxWidth - 4, 245, maxWidth + 4, 3);
			}
		}
		else
		{
			GlStateManager.color(1F, 1F, 1F, 0.3F);
			tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 2, 256 - maxWidth - 4, 245, maxWidth + 4, 3);
			boolean hover = mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1;
			GlStateManager.color(1F, 1F, 1F, hover ? 0.2F : 0.101F);
			tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 2, 256 - maxWidth - 4, 249, maxWidth + 4, 7);
			for (CatalogSort sort2 : sorts)
			{
				y += 6;
				hover = mouseY >= y && mouseY < y + 6 && mouseX >= width - maxWidth - 20 - 1 && mouseX <= width - 20 + 1;
				GlStateManager.color(1F, 1F, 1F, hover ? 0.2F : 0.101F);
				tablet.drawTexturedModalRect(width - maxWidth - 20 - 1, y - 1, 256 - maxWidth - 4, 249, maxWidth + 4, 6);
			}
		}


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
		return sorts.get(currentSort).getHeight() + 62;
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
