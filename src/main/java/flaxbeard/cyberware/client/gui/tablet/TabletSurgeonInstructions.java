package flaxbeard.cyberware.client.gui.tablet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.tablet.IScrollWheel;
import flaxbeard.cyberware.api.tablet.ITabletPage;
import flaxbeard.cyberware.client.gui.GuiTablet;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;

public class TabletSurgeonInstructions implements ITabletPage, IScrollWheel
{
	
	public static final ResourceLocation MANUAL1 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual1.png");
	public static final ResourceLocation MANUAL2 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual2.png");
	public static final ResourceLocation MANUAL3 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual3.png");
	public static final ResourceLocation MANUAL4 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual4.png");
	public static final ResourceLocation MANUAL5 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual5.png");
	public static final ResourceLocation MANUAL6 = new ResourceLocation(Cyberware.MODID + ":textures/gui/tablet/manual6.png");

	private int scroll = 0;
	private int height = 840;

	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks, boolean leftDown)
	{
		String title = I18n.format("cyberware.gui.tablet.surgeonInstructions.title0");
		tablet.drawString(title, 20, 15, 0x34B1C7);
		
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
				
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		
		int y = 30;
		
		String s = I18n.format("cyberware.gui.tablet.surgeonInstructions.0");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;

		y += 3;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.1");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		y += 5;
		title = I18n.format("cyberware.gui.tablet.surgeonInstructions.title1");
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.2");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		
		y += 5;
		drawCraftingRecipe(tablet, width / 2 - 30, y, new ItemStack(CyberwareContent.surgeryChamber.ib), .5F);
		y += drawCraftingRecipe(tablet, width / 2 + 30, y, new ItemStack(CyberwareContent.componentBox.ib), .5F);
		
		y += 5;
		title = I18n.format("cyberware.gui.tablet.surgeonInstructions.title2");
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.3");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		y += 5;
		title = I18n.format("cyberware.gui.tablet.surgeonInstructions.title3");
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.4");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL1);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();

		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.5");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.6");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL2);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.7");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL3);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.8");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL4);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.9");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.10");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.11");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL6);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.12");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.13");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.14");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;
		
		y += 5;
		title = I18n.format("cyberware.gui.tablet.surgeonInstructions.title4");
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.15");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.16");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("cyberware.gui.tablet.surgeonInstructions.17");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		this.height = y;

		
		//tablet.renderToolTipAtMouse(new ItemStack(CyberwareContent.surgeryApparatus));
		
		RenderHelper.disableStandardItemLighting();

	}
	
	private static HashMap<ItemStack, List<IRecipe>> storedRecipes = new HashMap<ItemStack, List<IRecipe>>();
	
	public int drawCraftingRecipe(GuiTablet tablet, int x, int y, ItemStack toMake, float size)
	{
		if (!storedRecipes.containsKey(toMake))
		{
			List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
			List<IRecipe> recipesForThisItem = new ArrayList<IRecipe>();
			for (IRecipe recipe : recipes)
			{
				if (recipe.getRecipeOutput() != null && recipe.getRecipeOutput().areItemsEqual(recipe.getRecipeOutput(), toMake))
				{
					recipesForThisItem.add(recipe);
				}
			}
			storedRecipes.put(toMake, recipesForThisItem);
		}
		
		List<IRecipe> recipes = storedRecipes.get(toMake);
		if (recipes.size() > 0)
		{
			IRecipe recipe = recipes.get((tablet.mc.thePlayer.ticksExisted / 10) % recipes.size());
			if (recipe instanceof ShapedRecipes)
			{
				ShapedRecipes shaped = (ShapedRecipes) recipe;
				for (int j = 0; j < shaped.recipeWidth; j++)
				{
					for (int k = 0; k < shaped.recipeHeight; k++)
					{
						ItemStack stack = shaped.recipeItems[j * shaped.recipeWidth + k];
						if (stack != null) 
						{
							if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
							{
								ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
								stack.getItem().getSubItems(stack.getItem(), null, subItems);
								if (subItems.size() > 0)
								{
									stack = subItems.get((tablet.mc.thePlayer.ticksExisted / 10) % subItems.size());
								}
							}
							
							tablet.renderItemWithTooltip(stack, x + (size * (-4 + (19 * (j - 1)) - 10)), y + (size * (28 + (19 * (k - 1)))), size, 0.7F);
						}
					}
				}
				
				tablet.renderItemWithTooltip(toMake, x + size * 32, y + size * 28, size, 0.7F);
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, 0);
				GlStateManager.scale(size, size, 1F);
				GlStateManager.enableBlend();
				GlStateManager.color(1F, 1F, 1F, 0.6F);
				tablet.mc.getTextureManager().bindTexture(tablet.TABLET_RESOURCES);
				tablet.drawTexturedModalRect(-42, 0, 0, 32, 84, 56);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
	
			}
			else if (recipe instanceof ShapedOreRecipe)
			{
				ShapedOreRecipe shaped = (ShapedOreRecipe) recipe;
				for (int i = 0; i < shaped.getRecipeSize(); i++)
				{
					int j = i % 3;
					int k = i / 3;
					ItemStack stack = null;
					Object input = shaped.getInput()[i];
					if (input instanceof String)
					{
						List<ItemStack> items = OreDictionary.getOres((String) input);
						stack = items.get((tablet.mc.thePlayer.ticksExisted / 10) % items.size());
					}
					else if (input instanceof ItemStack)
					{
						stack = (ItemStack) input;
					}
					else if (input instanceof List)
					{
						List<ItemStack> items = (List<ItemStack>) input;
						stack = items.get((tablet.mc.thePlayer.ticksExisted / 10) % items.size());
					}
					
					if (stack != null) 
					{
						if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
						{
							ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
							stack.getItem().getSubItems(stack.getItem(), null, subItems);
							if (subItems.size() > 0)
							{
								stack = subItems.get((tablet.mc.thePlayer.ticksExisted / 10) % subItems.size());
							}
						}
						
						tablet.renderItemWithTooltip(stack, x + (size * (-4 + (19 * (j - 1)) - 10)), y + (size * (28 + (19 * (k - 1)))), size, 0.7F);
					}
				}
				
				tablet.renderItemWithTooltip(toMake, x + size * 32, y + size * 28, size, 0.7F);
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, 0);
				GlStateManager.scale(size, size, 1F);
				GlStateManager.enableBlend();
				GlStateManager.color(1F, 1F, 1F, 0.6F);
				tablet.mc.getTextureManager().bindTexture(tablet.TABLET_RESOURCES);
				tablet.drawTexturedModalRect(-42, 0, 0, 32, 84, 56);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
		
		return (int) Math.ceil(56 * size);
		
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
		return this.height + 45;
	}


}
