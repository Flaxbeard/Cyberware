package flaxbeard.cyberware.client.gui.tablet;

import java.util.Random;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.ShaderHelper;
import flaxbeard.cyberware.client.gui.GuiTablet;
import flaxbeard.cyberware.common.CyberwareContent;

public class TabletCatalogItem implements ITabletPage, IScrollWheel
{
	
	private int scroll = 0;
	private boolean defaultVisible = false;
	
	private ItemStack item;
	private String unlocalizedName;
	
	public TabletCatalogItem(ItemStack itemStack, String unlocalizedName)
	{
		this.item = itemStack;
		this.unlocalizedName = unlocalizedName;
	}

	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks)
	{
		String title = item.getDisplayName();
		tablet.drawString(title, 20, 15, 0x34B1C7);
		
		String s = I18n.format("cyberware.slot." + CyberwareAPI.getCyberware(item).getSlot(item).getName());
		tablet.drawStringSmall(s, 15, 9, 0x188EA2);
		
		s = "9,982,123";
		tablet.drawString(s, 20, 30, 0x34B1C7);
		
		String y = "\u00a5";
		tablet.drawString(y, 21 + tablet.getStringWidth(s), 28, 0x34B1C7);
		s = ChatFormatting.OBFUSCATED + "?220" + ChatFormatting.RESET + " in stock";
		tablet.drawStringSmall(s, 20, 40, 0x188EA2);
		
		s = "Product Code:";
		tablet.drawStringSmall(s, 20, 47, 0x34B1C7);
		s = Integer.toString(new Random(item.getItemDamage() << 2 + item.getItem().getIdFromItem(item.getItem())).nextInt(100000000));
		while (s.length() < 8)
		{
			s = "0" + s;
		}
		s = s.substring(0, 2) + "-" + s.substring(2);
		tablet.drawStringSmall(s, 25, 52, 0x188EA2);
		
		s = "Manufacturer:";
		tablet.drawStringSmall(s, 20, 59, 0x34B1C7);
		s = "Touch Medical";
		tablet.drawStringSmall(s, 25, 64, 0x188EA2);
		
		s = ChatFormatting.ITALIC + "\"Touch Medical's Cardiovascular Coupler makes me feel like my body is my own. "
				+ "I can gain all the benefits of my augmentations without the hassle of a battery or a bulky generator.\"";
		int i = tablet.drawSplitSpringSmall(s, 20, 80, width - 40, 0x34B1C7);
		s = "- Neogrammy winning actress Veronica Hughes";
		tablet.drawStringSmall(s, width - tablet.getStringWidthSmall(s) - 20, 80 + i * 5, 0x188EA2);
		
		s = "From the leader in human augmentation technology, the Cardiovascular Coupler is the forefront of "
				+ "minimally invasive power generation. Designed with precision and custom-ordered "
				+ "to fit each customer, the Cardiovascular Coupler attaches to the heart of the user and utilizes the "
				+ "body's natural electrical pulses to power installed augmentations.";
		tablet.drawSplitSpringSmall(s, 20, 97 + i * 5, width - 40, 0x34B1C7);
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
				
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, tablet.getStringWidth(title) + 5, 1);
		tablet.drawTexturedModalRect(20, 90 + i * 5, 29, 254, width - 40, 1);
		
		RenderHelper.enableGUIStandardItemLighting();

		GlStateManager.pushMatrix();
		
		int renderX = width - (16 * 3) - 15;
		int renderY = 30;
		int scale = 3;
		
		ShaderHelper.greyscale(.6F);
	
		GlStateManager.scale(scale, scale, 1F);
		width /= scale;
		height /= scale;
		renderX /= scale;
		renderY /= scale;
		GlStateManager.translate(renderX + 8, renderY + 8, tablet.getItemRenderer().zLevel + 100 + 50);
		
		float rot = 40F * (float) Math.sin(ticks * Math.PI / 100);
		
		GlStateManager.rotate(rot, 0F, 1F, 0F);
		GlStateManager.translate(-(renderX + 8), -(renderY + 8), -(tablet.getItemRenderer().zLevel + 100 + 50));
		
		tablet.getItemRenderer().renderItemAndEffectIntoGUI(tablet.mc.thePlayer, item, renderX, renderY);
		ShaderHelper.releaseShader();
		
		ShaderHelper.greyscale(.15F);
		GlStateManager.translate(0F, 0F, 1F);
		tablet.getItemRenderer().renderItemAndEffectIntoGUI(tablet.mc.thePlayer, item, renderX + 1, renderY);

		GlStateManager.translate(0F, 0F, -2F);
		tablet.getItemRenderer().renderItemAndEffectIntoGUI(tablet.mc.thePlayer, item, renderX - 1, renderY);
		ShaderHelper.releaseShader();
		
		GlStateManager.popMatrix();
		
		
		
		
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
		return height;
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

}
