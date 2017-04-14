package flaxbeard.cyberware.common.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import org.lwjgl.input.Mouse;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.client.ClientUtils;

public class CreativeMenuHandler
{
	private static class CEXButton extends GuiButton
	{
		private Minecraft mc = Minecraft.getMinecraft();
		public final int offset;
		public final int baseX;
		public final int baseY;
		
		public CEXButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3, int offset)
		{
			super(p_i46316_1_, p_i46316_2_, p_i46316_3, 21, 21, "");
			this.offset = offset;
			this.baseX = this.xPosition;
			this.baseY = this.yPosition;
		}
	

		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				float trans = 0.4F;
				boolean down = Mouse.isButtonDown(0);
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			
				
				mc.getTextureManager().bindTexture(CEX_GUI_TEXTURES);

				boolean isDown = (down && flag) || pageSelected == offset;
	
				int i = 4;
				int j = 8;
				if (isDown)
				{
					i = 29;
					j = 0;
				}
				
				j += offset * (isDown ? 18 : 23);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 18, 18);
			}
		}
	}
	
	public static CreativeMenuHandler INSTANCE = new CreativeMenuHandler();
	
	private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/gui/creativeExpansion.png");
	private Minecraft mc = Minecraft.getMinecraft();
	public static int pageSelected = 1;
	private static CEXButton salvaged;
	private static CEXButton manufactured;

	
	@SubscribeEvent
	public void handleButtons(InitGuiEvent event)
	{
		if (event.getGui() instanceof GuiContainerCreative)
		{
			GuiContainerCreative gui = (GuiContainerCreative) event.getGui();

			int i = (gui.width - 136) / 2;
			int j = (gui.height - 195) / 2;
			
			List<GuiButton> buttons = event.getButtonList();
			buttons.add(salvaged = new CEXButton(355, i + 166 + 4, j + 29 + 8, 0));
			buttons.add(manufactured = new CEXButton(356, i + 166 + 4, j + 29 + 31, 1));
			
			// private static int selectedTabIndex
			int selectedTabIndex = ReflectionHelper.getPrivateValue(GuiContainerCreative.class, (GuiContainerCreative) gui, 2);
			if (selectedTabIndex != Cyberware.creativeTab.getTabIndex())
			{
				salvaged.visible = false;
				manufactured.visible = false;
			}
			event.setButtonList(buttons);
		}
	}
	
	@SubscribeEvent
	public void handleTooltips(DrawScreenEvent.Post event)
	{
		if (isCorrectGui(event.getGui()))
		{
			int mouseX = event.getMouseX();
			int mouseY = event.getMouseY();
			GuiContainerCreative gui = (GuiContainerCreative) event.getGui();
			int i = (gui.width - 136) / 2;
			int j = (gui.height - 195) / 2;
			if (isPointInRegion(i, j, salvaged.xPosition - i, 29 + 8, 18, 18, mouseX, mouseY))
			{
				ClientUtils.drawHoveringText(gui, Arrays.asList(new String[] { I18n.format(CyberwareAPI.QUALITY_SCAVENGED.getUnlocalizedName()) } ), mouseX, mouseY, mc.getRenderManager().getFontRenderer());
			}
			
			if (isPointInRegion(i, j, manufactured.xPosition - i, 29 + 8 + 23, 18, 18, mouseX, mouseY))
			{
				ClientUtils.drawHoveringText(gui, Arrays.asList(new String[] { I18n.format(CyberwareAPI.QUALITY_MANUFACTURED.getUnlocalizedName()) } ), mouseX, mouseY, mc.getRenderManager().getFontRenderer());
			}
		}
	}
	
	@SubscribeEvent
	public void handleCreativeInventory(BackgroundDrawnEvent event)
	{
		if (event.getGui() instanceof GuiContainerCreative)
		{
			int selectedTabIndex = ReflectionHelper.getPrivateValue(GuiContainerCreative.class, (GuiContainerCreative) event.getGui(), 2);

			if (selectedTabIndex == Cyberware.creativeTab.getTabIndex())
			{
				GuiContainerCreative gui = (GuiContainerCreative) event.getGui();
				int i = (gui.width - 136) / 2;
				int j = (gui.height - 195) / 2;
				
				int xSize = 29;
				int ySize = 129;
				
				int xOffset = 0;
				boolean hasVisibleEffect = false;
				for(PotionEffect potioneffect : mc.thePlayer.getActivePotionEffects())
				{
					Potion potion = potioneffect.getPotion();
					if(potion.shouldRender(potioneffect)) {
						hasVisibleEffect = true; break;
					}
				}
				if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && hasVisibleEffect)
				{
					xOffset = 59;
				}
				salvaged.xPosition = salvaged.baseX + xOffset;
				manufactured.xPosition = manufactured.baseX + xOffset;

				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.mc.getTextureManager().bindTexture(CEX_GUI_TEXTURES);
				gui.drawTexturedModalRect(i + 166 + xOffset, j + 29, 0, 0, xSize, ySize);
				
				salvaged.visible = true;
				manufactured.visible = true;
			}
			else
			{
				salvaged.visible = false;
				manufactured.visible = false;
			}
		}
	}
	
	@SubscribeEvent
	public void handleButtonClick(ActionPerformedEvent event)
	{
		if (isCorrectGui(event.getGui()))
		{
			GuiContainerCreative gui = (GuiContainerCreative) event.getGui();

			if (event.getButton().id == salvaged.id)
			{
				pageSelected = salvaged.offset;
			}
			else if (event.getButton().id == manufactured.id)
			{
				pageSelected = manufactured.offset;
			}
			Method tab = ReflectionHelper.findMethod(GuiContainerCreative.class, gui, new String[] { "setCurrentCreativeTab", "func_147050_b" }, CreativeTabs.class);
			try
			{
				tab.invoke(gui, Cyberware.creativeTab);
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean isCorrectGui(GuiScreen gui)
	{
		if (gui instanceof GuiContainerCreative)
		{
			// private static int selectedTabIndex
			int selectedTabIndex = ReflectionHelper.getPrivateValue(GuiContainerCreative.class, (GuiContainerCreative) gui, 2);
			if (selectedTabIndex == Cyberware.creativeTab.getTabIndex())
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isPointInRegion(int i, int j, int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
	{
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}
}
