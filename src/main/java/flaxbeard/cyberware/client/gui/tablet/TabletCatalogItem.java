package flaxbeard.cyberware.client.gui.tablet;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.CyberwareTag;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.ShaderUtil;
import flaxbeard.cyberware.client.gui.GuiSurgery;
import flaxbeard.cyberware.client.gui.GuiTablet;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class TabletCatalogItem implements ITabletPage, IScrollWheel
{
	
	private int scroll = 0;
	private boolean defaultVisible = false;
	
	private ItemStack item;
	private ICyberware ware;
	private String unlocalizedName;
	
	private EntityItem entity;
	
	private List<CyberwareTag> tags;
	
	private int production[];
	private int consumption[];
	private int storage[];
	private int tolerance[];
	
	private int maxStackSize;
	private int currentStackSize = 0;
	
	private boolean constPowerUse = true;
	
	private String manufacturer;

	public TabletCatalogItem(ItemStack itemStack, String unlocalizedName, CyberwareTag... tags)
	{
		this(itemStack, "", unlocalizedName, tags);
	}
	
	public TabletCatalogItem(ItemStack itemStack, String manufacturer, String unlocalizedName, CyberwareTag... tags)
	{
		this.tags = Lists.newArrayList(tags);
		this.item = itemStack;
		this.ware = CyberwareAPI.getCyberware(item);
		this.unlocalizedName = unlocalizedName;
		maxStackSize = getMaxStackSize();
		production = getPowerProduction();
		consumption = getPowerConsumption();
		storage = getPowerCapacity();
		tolerance = getToleranceCost();
		this.manufacturer = manufacturer;
		TabletContent.catalog.addItem(this);

	}


	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks, boolean leftDown)
	{
		String title = item.getDisplayName();
		tablet.drawString(title, 20, 15, 0x34B1C7);
		
		String title2 = I18n.format(unlocalizedName + ".name");
		title2 = (title2.equals(unlocalizedName + ".name")) ? title : title2;
		tablet.drawString(ChatFormatting.ITALIC + title2, 21, 25, 0x188EA2);
		
		int titleLen = Math.max(tablet.getStringWidth(title), tablet.getStringWidth(title2));
		
		String s = I18n.format(ware.getSlot(item).getUnlocalizedName());
		tablet.drawStringSmall(s, 15, 9, 0x188EA2);
		
		s = "9,982,123";
		tablet.drawString(s, 20, 40, 0x34B1C7);
		
		String z = "\u00a5";
		tablet.drawString(z, 22 + tablet.getStringWidth(s), 37, 0x34B1C7);
		s = ChatFormatting.OBFUSCATED + "?220" + ChatFormatting.RESET + " in stock";
		tablet.drawStringSmall(s, 20, 50, 0x188EA2);
		
		s = I18n.format("cyberware.gui.tablet.catalog.item.code");
		tablet.drawStringSmall(s, 20, 57, 0x34B1C7);
		s = Integer.toString(new Random(item.getItemDamage() << 2 + item.getItem().getIdFromItem(item.getItem())).nextInt(100000000));
		while (s.length() < 8)
		{
			s = "0" + s;
		}
		s = s.substring(0, 2) + "-" + s.substring(2);
		tablet.drawStringSmall(s, 25, 62, 0x188EA2);
		
		s = I18n.format("cyberware.gui.tablet.catalog.item.manufacturer");
		tablet.drawStringSmall(s, 20, 69, 0x34B1C7);
		s = I18n.format(manufacturer);
		tablet.drawStringSmall(s, 25, 74, 0x188EA2);
		
		
		s = ChatFormatting.ITALIC + "\"" + I18n.format(unlocalizedName + ".quote") + "\"";
		int i = tablet.drawSplitStringSmall(s, 20, 90, width - 40, 0x34B1C7);
		s = "- " + I18n.format(unlocalizedName + ".author");
		//tablet.drawStringSmall(s, width - tablet.getStringWidthSmall(s) - 20, 90 + ((int) (i * 5F)), 0x188EA2);
		i += tablet.drawSplitStringSmall(s, width - Math.min(width - 60, tablet.getStringWidthSmall(s)) - 20, 90 + ((int) (i * 5F)), width - 60, 0x188EA2);

		int y = 102 + ((int) (i * 5F));
		
		if (maxStackSize > 1)
		{
			s = I18n.format("cyberware.gui.tablet.catalog.item.numInstalled");
			tablet.drawStringSmall(s, 20, y, 0x34B1C7);
			
			int offset = tablet.getStringWidthSmall(s) + 5;
			
			for (int d = 0; d < maxStackSize; d++)
			{
				s = Integer.toString(d + 1);
				if (d == currentStackSize)
				{
					tablet.drawStringSmall("[" + s + "]", offset + 20 + d * 10 - tablet.getStringWidthSmall("["), y, 0x34B1C7);
				}
				else
				{
					boolean hovered = mouseX >= offset + 20 + d * 10 - 1 && mouseX <= offset + 20 + d * 10 + tablet.getStringWidthSmall(s) + 1 && mouseY >= y - 1 && mouseY <= y + 4;
					tablet.drawStringSmall(s, offset + 20 + d * 10, y, hovered ? 0x34B1C7 : 0x188EA2);
					if (hovered && leftDown)
					{
						currentStackSize = d;
					}
				}
			}
			y += 10;
		}
		
		int indent = 0;
		if (tolerance[currentStackSize] != 0)
		{
			if (tolerance[currentStackSize] > 0)
			{
				s = I18n.format("cyberware.gui.tablet.catalog.item.rejection");
				tablet.drawStringSmall(s, 20 + indent, y, 0x188EA2);
				
				tablet.drawString(Integer.toString(tolerance[currentStackSize]), 20 + indent, y + 6, 0x34B1C7);
				
				s = I18n.format("cyberware.gui.tablet.catalog.item.tol");
				tablet.drawStringSmall(s, 20 + indent + tablet.getStringWidth(Integer.toString(tolerance[currentStackSize])) + 1, y + 6 + 3, 0x34B1C7, 0, 1);
				
				indent += 52;
			}
			if (tolerance[currentStackSize] < 0)
			{
				s = I18n.format("cyberware.gui.tablet.catalog.item.boost");
				tablet.drawStringSmall(s, 20 + indent, y, 0x188EA2);
				
				tablet.drawString(Integer.toString(-tolerance[currentStackSize]), 20 + indent, y + 6, 0x34B1C7);
				
				s = I18n.format("cyberware.gui.tablet.catalog.item.tol");
				tablet.drawStringSmall(s, 20 + indent + tablet.getStringWidth(Integer.toString(-tolerance[currentStackSize])) + 1, y + 6 + 3, 0x34B1C7, 0, 1);
				
				indent += 52;
			}
		}
		if (storage[currentStackSize] > 0)
		{
			s = I18n.format("cyberware.gui.tablet.catalog.item.storage");
			tablet.drawStringSmall(s, 20 + indent, y, 0x188EA2);
			
			String ss = Integer.toString(storage[currentStackSize]);
			s = I18n.format("cyberware.gui.tablet.catalog.item.ij");
			if (storage[currentStackSize] > 1000)
			{
				ss = ss.substring(0, ss.length() - 3) + "." + ss.substring(ss.length() - 3);
				while (ss.charAt(ss.length() - 1) == '0')
				{
					ss = ss.substring(0, ss.length() - 1);
				}
				
				if (ss.charAt(ss.length() - 1) == '.')
				{
					ss = ss.substring(0, ss.length() - 1);
				}
				
				s = I18n.format("cyberware.gui.tablet.catalog.item.kij");
			}
			
			tablet.drawString(ss, 20 + indent, y + 6, 0x34B1C7);
		
			tablet.drawStringSmall(s, 20 + indent + tablet.getStringWidth(ss) + 1, y + 6 + 3, 0x34B1C7, 0, 1);
			
			indent += 52;
		}
		if (production[currentStackSize] > 0)
		{
			s = I18n.format("cyberware.gui.tablet.catalog.item.produce");
			tablet.drawStringSmall(s, 20 + indent, y, 0x188EA2);
			
			tablet.drawString(Integer.toString(production[currentStackSize]), 20 + indent, y + 6, 0x34B1C7);
			
			s = I18n.format("cyberware.gui.tablet.catalog.item.ijs");
			tablet.drawStringSmall(s, 20 + indent + tablet.getStringWidth(Integer.toString(production[currentStackSize])) + 1, y + 6 + 3, 0x34B1C7, 0, 1);
			
			indent += 52;
		}
		if (consumption[currentStackSize] > 0)
		{
			s = I18n.format("cyberware.gui.tablet.catalog.item.consume");
			tablet.drawStringSmall(s, 20 + indent, y, 0x188EA2);
			
			tablet.drawString(Integer.toString(consumption[currentStackSize]), 20 + indent, y + 6, 0x34B1C7);
			
			s = constPowerUse ? I18n.format("cyberware.gui.tablet.catalog.item.ijs") : I18n.format("cyberware.gui.tablet.catalog.item.onUse");

			tablet.drawStringSmall(s, 20 + indent + tablet.getStringWidth(Integer.toString(consumption[currentStackSize])) + 1, y + 6 + 3, 0x34B1C7, 0, 1);
			
			indent += 52;
		}
		
		int statsBar = 0;
		
		if (indent != 0)
		{
			statsBar = y;
			y += 27;
		}
		
		s = I18n.format(unlocalizedName + ".text");
		tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7);
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
				
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 35, 29, 254, titleLen + 5, 1);
		
		tablet.drawTexturedModalRect(20, 95 + i * 5, 29, 254, width - 40, 1);
		if (statsBar != 0)
		{
			tablet.drawTexturedModalRect(20, statsBar + 19, 29, 254, width - 40, 1);
		}
		
		ShaderUtil.greytint(0.0F, 0.85F, 1.0F, .55F);
		GlStateManager.pushMatrix();
		World world = Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().theWorld : null;
		if (entity == null || entity.isDead)
		{
			entity = new EntityItem(world, 0, 0, 0, item);
		}
		else
		{
			entity.worldObj = world;
		}
		entity.hoverStart = 0F;
		GuiSurgery.renderEntity(entity, 148, 111, 100, ticks * 3);
				
		ShaderUtil.releaseShader();

		GlStateManager.popMatrix();
	
		y += 35;
		

		
		for (int j = 0; j < 3; j++)
		{
			for (int k = 0; k < 3; k++)
			{
				//tablet.renderItemWithTooltip(new ItemStack(CyberwareContent.component, 1, j * 3 + k), width / 2 + (25 * (j - 1)), y + 48 + (25 * (k - 1)), 1, 0.9F);
			}
		}
		
		//tablet.renderItem(new ItemStack(CyberwareContent.surgeryApparatus), width / 2, y + 48, 6, 0.2F);

		
		//tablet.renderToolTipAtMouse(new ItemStack(CyberwareContent.surgeryApparatus));

		
		RenderHelper.disableStandardItemLighting();

	}

	@Override
	public int getWidth(int defaultWidth, int ticksOpen, float partialTicks)
	{
		return defaultWidth;
	}

	@Override
	public ITabletPage getParent()
	{
		return TabletContent.catalog;
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
		FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
		String s = ChatFormatting.ITALIC + "\"" + I18n.format(unlocalizedName + ".quote") + "\"";
		int i = tablet.getSplitStringSmallLines(s, width - 40);
		int y = 97 + ((int) (i * 5F));
		
		if (maxStackSize > 1)
		{
			y += 10;
		}
		
		if (tolerance[currentStackSize] != 0 || storage[currentStackSize] > 0 || production[currentStackSize] > 0 || consumption[currentStackSize] > 0)
		{
			y += 27;
		}

		s = "- " + I18n.format(unlocalizedName + ".author");
		y += (int) (tablet.getSplitStringSmallLines(s, width - 60) * 5F);
		
		s = I18n.format(unlocalizedName + ".text");
		y += (int) (tablet.getSplitStringSmallLines(s, width - 40) * 5F);
			
		return y + 40;
	}

	public TabletCatalogItem setDefaultVisible()
	{
		this.defaultVisible = true;
		return this;
	}
	
	public ItemStack getItem()
	{
		return this.item;
	}

	public String getUnlocalizedName()
	{
		return this.unlocalizedName;
	}
	
	public List<CyberwareTag> getTags()
	{
		return tags;
	}
	
	public int getMaxStackSize()
	{
		return ware.installedStackSize(item);
	}
	
	public int[] getPowerProduction()
	{
		int ret[] = new int[maxStackSize];
		if (ware instanceof ItemCyberware)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = ((ItemCyberware) ware).getPowerProduction(new ItemStack(item.getItem(), i + 1, item.getItemDamage()));
			}
		}
		return ret;
	}
	
	public int[] getPowerConsumption()
	{
		int ret[] = new int[maxStackSize];
		if (ware instanceof ItemCyberware)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = ((ItemCyberware) ware).getPowerConsumption(new ItemStack(item.getItem(), i + 1, item.getItemDamage()));
			}
		}
		return ret;
	}
	
	public int[] getPowerCapacity()
	{
		int ret[] = new int[maxStackSize];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = ware.getCapacity(new ItemStack(item.getItem(), i + 1, item.getItemDamage()));
		}
		return ret;
	}
	
	public int[] getToleranceCost()
	{
		int ret[] = new int[maxStackSize];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = ware.getEssenceCost(new ItemStack(item.getItem(), i + 1, item.getItemDamage()));
		}
		return ret;
	}
	
	public void setPowerUseConstant(boolean constant)
	{
		constPowerUse = constant;
	}
	
	public String getManufacturer()
	{
		return this.manufacturer;
	}
}
