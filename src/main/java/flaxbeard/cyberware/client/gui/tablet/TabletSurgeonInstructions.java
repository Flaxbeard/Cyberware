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

	@Override
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticks, float partialTicks, boolean leftDown)
	{
		String title = "Robosurgeon Instructions";
		tablet.drawString(title, 20, 15, 0x34B1C7);
		
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
				
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, 25, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		
		int y = 30;
		
		String s = I18n.format("Thank you for your purchase of ");
		s = "WARNING: Operation of this device without proper training can result in serious injury and death! Please read the entirety of this manual before using the device.";
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;

		y += 3;
		
		s = I18n.format("");
		s = "Thank you for your purchase of a Touch Medical Robosurgeon! The Robosurgeon enables rapid installation of human augmentations without the risk of error that comes with traditional human-provided surgery."
				+ " This guide will provide information on installation and usage of your device.";
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		y += 5;
		title = "Components";
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("");
		s = "Below is a list of components used to construct the items in your Robosurgeon package. Refer to this list for repair purposes.";
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		
		y += 5;
		drawCraftingRecipe(tablet, width / 2 - 30, y, new ItemStack(CyberwareContent.surgeryChamber.ib), .5F);
		y += drawCraftingRecipe(tablet, width / 2 + 30, y, new ItemStack(CyberwareContent.componentBox.ib), .5F);
		
		y += 5;
		title = "Installation";
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("");
		s = "To install your Touch Medical Robosurgeon, place down the included Surgery Chamber. Place the Robosurgeon directly on top of the Surgery Chamber.";
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		y += 5;
		title = "Operation";
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("To set up a surgery, tap on the screen of the Robosurgeon to boot into the surgery window.");
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

		s = I18n.format("The meter on the left side of the display shows your body's Tolerance. Installation of augmentations will deplete the meter. Refer to the section labeled"
				+ " Tolerance for more information.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("The button on the top right of the display will take you to an index page that will display a list of all currently installed augmentations and all augmentations"
				+ " that will be installed after the surgery.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL2);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("The center of the screen displays your body. Tap on your arms, legs, chest, head, or flesh to zoom in and install augmentations.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;

		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL3);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("Once zoomed in, icons will appear showing each area where augmentations can be installed. Tap one to proceed.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL4);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("The top red slots show your currently installed organs and augmentations. The bottom blue slots show the organs and augmentations that will be installed after the surgery.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("You may place augmentations in the bottom blue slots. A red flashing effect indicates that the augment you are trying to install is not compatible with one already set to be installed.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("You may click on an augmentation in the bottom blue slot to set it to be removed. Clicking the slot again will set the augmentation to not be removed.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		
		
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((width) / 2 - (176 / 4), y + 5, 0);
		GlStateManager.scale(.5F, .5F, 0);
		tablet.mc.getTextureManager().bindTexture(MANUAL6);
		tablet.drawTexturedModalRect(0, 0, 0, 0, 176, 131);
		y += (131 / 2) + 10;
		GlStateManager.popMatrix();
		
		s = I18n.format("WARNING: If the above icon is displayed, hover over it. This symbol alerts the user that they are set to remove a critical organ during surgery or will fall into dangerous Tolerance levels.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;
		y += 2;
		
		s = I18n.format("Once you are ready to perform the surgery, simply step into the Surgery Chamber and close the doors.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("Make sure that you are healthy before undergoing surgery, as it may result in damage.");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0xC61700) * 5;
		
		y += 5;
		title = "Tolerance";
		tablet.drawString(title, 20, y, 0x34B1C7);
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tablet.TABLETHD);
		GlStateManager.color(1F, 1F, 1F, 0.6F);
		tablet.drawTexturedModalRect(20, y + 9, 29, 254, tablet.getStringWidth(title) + 5, 1);
		GlStateManager.disableBlend();
		y += 14;
		
		s = I18n.format("The human body will only tolerate a certain amount of augmentation before its rejection process becomes dangerous. Most augmentations available on the market have a tolerance impact rating"
				+ " measured in the standard unit tols. A standard human can handle %d tols of augmentation.", CyberwareConfig.ESSENCE);
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("If the amount of tolerance displayed on the meter falls below %d tols, the user will suffer from Augmentation Rejection Syndrome (ARS). Utilizing Neuropozene will prevent ARS, with each dose lasting for about one day",
				CyberwareConfig.CRITICAL_ESSENCE);
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;
		
		s = I18n.format("If the meter falls to 0 tols, the user will suffer from Lethal Augmentation Rejection Syndrome (LARS) which will result in a swift death.",
				CyberwareConfig.ESSENCE, CyberwareConfig.CRITICAL_ESSENCE);
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		y += 2;

		
		//tablet.renderToolTipAtMouse(new ItemStack(CyberwareContent.surgeryApparatus));

		
		s = I18n.format("[19:51:51] [Client thread/INFO] [STDOUT]: [flaxbeard.cyberware.client.ClientUtils:bindTexture:53]: [Cyberware] Registering new ResourceLocation: cyberware:textures/models/surgeryChamberDoor.png");
		y += tablet.drawSplitStringSmall(s, 20, y, width - 40, 0x34B1C7) * 5;
		

		
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
		return height * 6;
	}


}
