package flaxbeard.cyberware.client.gui.tablet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.CyberwareTag;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.progression.ProgressionHelper;
import flaxbeard.cyberware.client.ShaderUtil;
import flaxbeard.cyberware.client.gui.GuiTablet;

public interface CatalogSort
{
	public class Category
	{
		private List<TabletCatalogItem> items;
		private String unlocalizedName;
		private boolean opened = true;
		
		public Category(String unlocalizedName)
		{
			this.unlocalizedName = unlocalizedName;
			items = new ArrayList<TabletCatalogItem>();
		}
		
		public void add(TabletCatalogItem item)
		{
			items.add(item);
		}
		
		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}
		
		public int size()
		{
			return items.size();
		}
		
		public List<TabletCatalogItem> getItems()
		{
			return items;
		}
		
		public boolean isOpened()
		{
			return opened;
		}
		
		public void setOpened(boolean opened)
		{
			this.opened = opened;
		}
	}
	
	public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden);
	public int getHeight(boolean showHidden);
	public void addItem(TabletCatalogItem item);
	public String getUnlocalizedName();
	
	public static void renderCategories(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, Category category, boolean leftDown, boolean showHidden)
	{
		renderCategories(tablet, x, y, width, mouseX, mouseY, Lists.newArrayList(category), leftDown, showHidden);
	}
	
	public static void renderCategories(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, List<Category> categories, boolean leftDown, boolean showHidden)
	{
		for (int z = 0; z < categories.size(); z++)
		{
			Category category = categories.get(z);
			if (category.size() > 0)
			{
				int iY = y;
				if (category.getUnlocalizedName().length() > 0)
				{
					String s = I18n.format(category.getUnlocalizedName());
					tablet.drawStringSmall(s, x, y + 3, 0x34B1C7, 0, 0);					
					
					y += 8;

				}
				
				if (category.isOpened())
				{
					List<TabletCatalogItem> items = category.getItems();
					
					int yO = y;
					
					for (int i = 0; i < items.size(); i++)
					{
						TabletCatalogItem item = items.get(i);
						boolean unlocked = ProgressionHelper.isUnlocked(tablet.mc.thePlayer, item.getItem());
						if (unlocked || showHidden)
						{
							GlStateManager.pushMatrix();
							boolean hovered = mouseX > 0 && mouseX < width - 55 && mouseY >= y && mouseY < y + 10;
							
							if (unlocked && hovered && leftDown)
							{
								tablet.setPage(item);
							}
							
							String s = item.getItem().getDisplayName();
	
							if (!unlocked)
							{
								s = ChatFormatting.OBFUSCATED + s;
								tablet.drawStringSmall(s, x + 9, y + 3, 0x2D6873, 0, 0);
							}
							else
							{
								tablet.drawStringSmall(s, x + 9, y + 3, hovered ? 0x34B1C7 : 0x188EA2, 0, 0);
							}
							
							GlStateManager.popMatrix();
							
							y += 10;
						}
					}
					
					y = yO;
					
					Random r = new Random(tablet.mc.thePlayer.ticksExisted + 999 * z);
		
					
					for (int i = 0; i < items.size(); i++)
					{
						ItemStack item = items.get(i).getItem();
						boolean unlocked = ProgressionHelper.isUnlocked(tablet.mc.thePlayer, item);
						if (unlocked || showHidden)
						{
							GlStateManager.pushMatrix();
							boolean hovered = mouseX > 0 && mouseX < width - 55 && mouseY >= y && mouseY < y + 10;
			
							RenderHelper.enableGUIStandardItemLighting();
			
							GlStateManager.pushMatrix();
							GlStateManager.translate(x, y + 1, 0);
							GlStateManager.scale(.5F, .5F, .5F);
													
							ShaderUtil.greytint(0.0F, 0.85F, 1.0F, unlocked ? hovered ? .8F : .6F : .3F);
	
							if (!unlocked)
							{
								item = new ItemStack(Item.getItemById(r.nextInt(10000)));
								while (item.getItem() == null)
								{
									item = new ItemStack(Item.getItemById(r.nextInt(10000)));
								}
							}
							
							tablet.getItemRenderer().renderItemAndEffectIntoGUI(tablet.mc.thePlayer, item, 0, 0);
												
							ShaderUtil.releaseShader();
							GlStateManager.popMatrix();
			
							RenderHelper.disableStandardItemLighting();
							
							GlStateManager.popMatrix();
							
							y += 10;
						}
					}
				}
				
				if (category.getUnlocalizedName().length() > 0)
				{
					Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
					
					GlStateManager.enableBlend();
					boolean hovered = mouseX > 0 && mouseX < width - 55 && mouseY >= iY && mouseY < iY + 10;
					GlStateManager.color(1F, 1F, 1F, hovered ? 0.8F : 0.6F);
					tablet.drawTexturedModalRect(x - 6, iY + 3, category.isOpened() ? 150 : 145, 245, 3, 3);
					
					if (hovered && leftDown)
					{
						category.setOpened(!category.isOpened());
					}
				}
			}
		}
	}
	
	public static int getHeightGeneral(Category category, boolean showHidden)
	{
		return getHeightGeneral(Lists.newArrayList(category), showHidden);
	}
	
	public static int getHeightGeneral(List<Category> categories, boolean showHidden)
	{
		int h = 0;
		for (int z = 0; z < categories.size(); z++)
		{
			Category category = categories.get(z);
			if (category.size() > 0)
			{
				if (category.getUnlocalizedName().length() > 0)
				{	
					h += 8;
				}
				
				if (category.isOpened())
				{
					List<TabletCatalogItem> items = category.getItems();
					for (int i = 0; i < items.size(); i++)
					{
						ItemStack item = items.get(i).getItem();
						boolean unlocked = ProgressionHelper.isUnlocked(Minecraft.getMinecraft().thePlayer, item);
						if (unlocked || showHidden) h += 10;
					}
				}
			}
		}
		return h;
	}
	
	public class SlotSort implements CatalogSort
	{
		private List<Category> categories;
		
		public SlotSort()
		{
			categories = new ArrayList<Category>();
			for (EnumSlot slot : EnumSlot.values())
			{
				categories.add(new Category(slot.getUnlocalizedName()));
			}
		}
		
		@Override
		public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden)
		{
			renderCategories(tablet, x, y, width, mouseX, mouseY, categories, leftDown, showHidden);
		}

		@Override
		public int getHeight(boolean showHidden)
		{
			return getHeightGeneral(categories, showHidden);
		}

		@Override
		public void addItem(TabletCatalogItem item)
		{
			EnumSlot slot = CyberwareAPI.getCyberware(item.getItem()).getSlot(item.getItem());
			Category category = categories.get(slot.ordinal());
			category.add(item);
		}

		@Override
		public String getUnlocalizedName()
		{
			return "cyberware.gui.tablet.catalog.sort.slot";
		}
	}
	
	public class AlphaSort implements CatalogSort
	{
		private Category alpha;
		
		public AlphaSort()
		{
			alpha = new Category("");
		}
		
		@Override
		public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden)
		{
			renderCategories(tablet, x, y, width, mouseX, mouseY, alpha, leftDown, showHidden);
		}

		@Override
		public int getHeight(boolean showHidden)
		{
			return getHeightGeneral(alpha, showHidden);
		}

		@Override
		public void addItem(TabletCatalogItem item)
		{			
			int i = 0;
			while (i < alpha.size())
			{
				if (alpha.getItems().get(i).getItem().getDisplayName().compareTo(item.getItem().getDisplayName()) >= 0)
				{
					break;
				}
				i++;
			}
			alpha.getItems().add(i, item);
		}
		
		@Override
		public String getUnlocalizedName()
		{
			return "cyberware.gui.tablet.catalog.sort.alpha";
		}
	}
	
	public class TagSort implements CatalogSort
	{
		private List<Category> categories;
		
		public TagSort()
		{
			categories = new ArrayList<Category>();
			for (CyberwareTag tag : CyberwareTag.getTags())
			{
				categories.add(new Category(tag.getUnlocalizedName()));
			}
			categories.add(new Category("cyberware.gui.tablet.catalog.sort.other"));
		}
		
		@Override
		public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden)
		{
			renderCategories(tablet, x, y, width, mouseX, mouseY, categories, leftDown, showHidden);
		}

		@Override
		public int getHeight(boolean showHidden)
		{
			return getHeightGeneral(categories, showHidden);
		}

		@Override
		public void addItem(TabletCatalogItem item)
		{
			List<CyberwareTag> tags = item.getTags();
			
			for (CyberwareTag tag : tags)
			{
				categories.get(tag.ordinal()).add(item);
			}
			
			if (tags.size() == 0)
			{
				categories.get(categories.size() - 1).add(item);
			}
		}

		@Override
		public String getUnlocalizedName()
		{
			return "cyberware.gui.tablet.catalog.sort.tag";
		}
	}
	
	public class CorpSort implements CatalogSort
	{
		private List<Category> categories;
		
		public CorpSort()
		{
			categories = new ArrayList<Category>();
			categories.add(new Category("cyberware.gui.tablet.catalog.sort.other"));
		}
		
		@Override
		public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden)
		{
			renderCategories(tablet, x, y, width, mouseX, mouseY, categories, leftDown, showHidden);
		}

		@Override
		public int getHeight(boolean showHidden)
		{
			return getHeightGeneral(categories, showHidden);
		}

		@Override
		public void addItem(TabletCatalogItem item)
		{
			String manufacturer = item.getManufacturer();
			
			if (manufacturer == null || manufacturer.length() == 0)
			{
				categories.get(categories.size() - 1).add(item);
			}
			else
			{
				for (Category category : categories)
				{
					if (category.getUnlocalizedName().equals(manufacturer))
					{
						category.add(item);
						return;
					}
				}
				
				Category cat = new Category(manufacturer);
				categories.add(0, cat);
				cat.add(item);
				
			}
		}

		@Override
		public String getUnlocalizedName()
		{
			return "cyberware.gui.tablet.catalog.sort.corp";
		}
	}
	
	public class ModSort implements CatalogSort
	{
		private List<Category> categories;
		
		public ModSort()
		{
			categories = new ArrayList<Category>();
		}
		
		@Override
		public void render(GuiTablet tablet, int x, int y, int width, int mouseX, int mouseY, boolean leftDown, boolean showHidden)
		{
			renderCategories(tablet, x, y, width, mouseX, mouseY, categories, leftDown, showHidden);
		}

		@Override
		public int getHeight(boolean showHidden)
		{
			return getHeightGeneral(categories, showHidden);
		}

		@Override
		public void addItem(TabletCatalogItem item)
		{
			String name = CyberwareAPI.getCyberware(item.getItem()).getUnlocalizedOrigin(item.getItem());
			for (Category category : categories)
			{
				if (category.getUnlocalizedName().equals(name))
				{
					category.add(item);
					return;
				}
			}
			Category category = new Category(name);
			categories.add(category);
			category.add(item);
		}

		@Override
		public String getUnlocalizedName()
		{
			return "cyberware.gui.tablet.catalog.sort.mod";
		}
	}
}
