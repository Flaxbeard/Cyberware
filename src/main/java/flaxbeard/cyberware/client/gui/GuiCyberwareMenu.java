package flaxbeard.cyberware.client.gui;

import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableSet;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.HotkeyHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.client.KeyBinds;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.SyncHotkeyPacket;

public class GuiCyberwareMenu extends GuiScreen
{
	Minecraft mc;
	boolean movedWheel = false;
	int selectedPart = -1;
	int lastMousedOverPart = -1;
	boolean editing = false;
	
	float[] defaultColor = new float[] { 76 / 255F, 255 / 255F, 0F };

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		int d = Mouse.getDWheel();
		if (!movedWheel && d != 0 && !editing)
		{
			movedWheel = true;
			if (selectedPart == -1 && d > 0)
			{
				d = 0;
			}
		}
		
		float radiusBase = 100F;
		float innerRadiusBase = 40F;
		
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((width / 2F) + radiusBase, (height / 2F) - radiusBase, 0F);
		GlStateManager.scale(2F, 2F, 2F);
		this.fontRendererObj.drawStringWithShadow("?", 0, 0, 0x4CFF00);
		GlStateManager.popMatrix();

		
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		int centerX = width / 2;
		int centerY = height  / 2;
		mc = Minecraft.getMinecraft();
		
		ICyberwareUserData data = CyberwareAPI.getCapability(mc.thePlayer);
		int piePieces = data.getNumActiveItems();
		
		if (movedWheel && !editing)
		{
			selectedPart = (selectedPart - Integer.signum(d));
			while (selectedPart < 0)
			{
				selectedPart += piePieces;
			}
			selectedPart = selectedPart % piePieces;
		}
		
		float degreesPerPiece = 360F / piePieces;
		
		int maxStepsPerTrial = 5;
		
		float mouseDist = (float) Math.sqrt((centerX - mouseX) * (centerX - mouseX) + (centerY - mouseY) * (centerY - mouseY));
		float mouseAngle = (float) ((Math.atan2(mouseY - centerY, mouseX - centerX) * 180F / Math.PI) + 360F) % 360F;
		
		for (int piece = 0; piece < piePieces; piece++)
		{
			
			float rotation = (degreesPerPiece * piece + 270) % 360;
			
			if (!editing)
			{
				if (mouseDist > innerRadiusBase)
				{
					movedWheel = false;
					if (piePieces == 1 || (mouseAngle > rotation && mouseAngle < rotation + degreesPerPiece && lastMousedOverPart != piece))
					{
						lastMousedOverPart = piece;
						selectedPart = piece;
					}
				}
				else
				{
					lastMousedOverPart = -1;
					if (!movedWheel)
					{
						selectedPart = -1;
					}
				}
			}
			
			boolean selected = piece == selectedPart;
			
			
			for (int deg = (int) (degreesPerPiece + .5F); deg > 0; deg -= maxStepsPerTrial)
			{
				float radius = radiusBase + (selected ? 10 : 0);
				float innerRadius = innerRadiusBase + (selected ? 10 : 0);
				
				int stepsPerTrial = Math.min(maxStepsPerTrial, deg);
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				
				float alpha = selected ? .8F : .5F;
				
				ItemStack stack = data.getActiveItems().get(piece);
				float[] itemColor = ((IMenuItem) stack.getItem()).getColor(stack);
				float[] color = (itemColor == null) ? defaultColor : itemColor;
				GL11.glColor4f(color[0], color[1], color[2], alpha);
				
				double radians = ((rotation + deg) / 180F) * Math.PI;

				float xS = centerX + ((float) Math.cos(radians) * innerRadius);
				float yS = centerY + ((float) Math.sin(radians) * innerRadius);
				GL11.glVertex2f(xS, yS);
				
				for (int i = 0; i <= stepsPerTrial; i++)
				{
					radians = ((rotation + deg - i) / 180F) * Math.PI;
					float x = centerX + ((float) Math.cos(radians) * radius);
					float y = centerY + ((float) Math.sin(radians) * radius);
					GL11.glVertex2f(x, y);
				}
				
				radians = ((rotation + deg - stepsPerTrial) / 180F) * Math.PI;

				xS = centerX + ((float) Math.cos(radians) * innerRadius);
				yS = centerY + ((float) Math.sin(radians) * innerRadius);
				GL11.glVertex2f(xS, yS);
				
				
				GL11.glEnd();
			}



				
		}
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
		float scale = piePieces > 8F ? (piePieces > 16F ? .5F : 1F) : 2F;
		boolean unicode = this.fontRendererObj.getUnicodeFlag();
		if (scale < 1.0F)
		{
			this.fontRendererObj.setUnicodeFlag(true);
		}
		float itemRadiusBase = innerRadiusBase + (radiusBase - innerRadiusBase) / 2F;
		
		for (int piece = 0; piece < piePieces; piece++)
		{
			ItemStack stack = data.getActiveItems().get(piece);

			float itemRadius = (piece == this.selectedPart ? itemRadiusBase + 10F : itemRadiusBase);
			float rotation = (degreesPerPiece * (piece + .5F) + 270) % 360;
			double radians = ((rotation) / 180F) * Math.PI;
			float offset = (16 / 2F) * scale;
			int boundKey = HotkeyHelper.getHotkey(stack);
			float yOffset = boundKey == -1 ? (this.fontRendererObj.FONT_HEIGHT / 2F) : 0;
			float xS = centerX + ((float) Math.cos(radians) * itemRadius);
			float yS = centerY + ((float) Math.sin(radians) * itemRadius);
			GlStateManager.pushMatrix();
			GlStateManager.translate(xS - offset, yS - offset + yOffset, 0);
			GlStateManager.scale(scale, scale, scale);
			this.itemRender.renderItemIntoGUI(data.getActiveItems().get(piece), 0, 0);
			GlStateManager.popMatrix();
			
			if (piece == selectedPart && editing)
			{
				if ((mc.thePlayer.ticksExisted / 4) % 2 == 0)
				{
					GlStateManager.pushMatrix();
					String str = "__";
					int i = this.fontRendererObj.getStringWidth(str);
		
					GlStateManager.translate(xS - i / 2F, yS + offset, 0);
					this.fontRendererObj.drawStringWithShadow(str, 0, 0, 0xFFFFFF);
					GlStateManager.popMatrix();
				}
			}
			else if (boundKey != -1)
			{
				GlStateManager.pushMatrix();
				
				String str = "";
				if (boundKey < 0)
				{
					boundKey = boundKey + 100;
					str = Mouse.getButtonName(boundKey);
				}
				else if (boundKey > 900)
				{
					str = "SHIFT + " + Keyboard.getKeyName(boundKey - 900);
				}
				else
				{
					str = Keyboard.getKeyName(boundKey);
				}
				int i = this.fontRendererObj.getStringWidth(str);
	
				GlStateManager.translate(xS - i / 2F, yS + offset, 0);
				this.fontRendererObj.drawStringWithShadow(str, 0, 0, 0xFFFFFF);
				GlStateManager.popMatrix();
			}
			
			GlStateManager.pushMatrix();
			String str = I18n.format(((IMenuItem) stack.getItem()).getUnlocalizedLabel(stack));
			int i = this.fontRendererObj.getStringWidth(str);

			GlStateManager.translate(xS - i / 2F, yS - offset + yOffset - this.fontRendererObj.FONT_HEIGHT, 0);
			this.fontRendererObj.drawStringWithShadow(str, 0, 0, 0xFFFFFF);
			GlStateManager.popMatrix();

		}
		
		this.fontRendererObj.setUnicodeFlag(unicode);

		
		
		GlStateManager.pushMatrix();

		int sx = (int) ((width / 2F) + radiusBase);
		int sy = (int) ((height / 2F) - radiusBase);
		int sh = this.fontRendererObj.FONT_HEIGHT * 2;
		int sw = this.fontRendererObj.getStringWidth("?") * 2;

		if (this.isPointInRegion(sx, sy, sw, sh, mouseX, mouseY))
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.keybind.0"), I18n.format("cyberware.gui.keybind.1"), I18n.format("cyberware.gui.keybind.2"), I18n.format("cyberware.gui.keybind.3") } ), mouseX, mouseY, fontRendererObj);
		}

		GlStateManager.popMatrix();

	}
	
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
	{
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if (mouseButton == 0 && !editing)
		{
			editing = true;
		}
		if (mouseButton == 1)
		{
			if (editing)
			{
				editing = false;
			}
			else if (this.selectedPart != -1)
			{
				ICyberwareUserData data = CyberwareAPI.getCapability(mc.thePlayer);

				HotkeyHelper.removeHotkey(data, data.getActiveItems().get(selectedPart));
				
				CyberwarePacketHandler.INSTANCE.sendToServer(new SyncHotkeyPacket(selectedPart, Integer.MAX_VALUE));
			}
		}
		
		if (mouseButton > 1 && editing && this.selectedPart != -1) 
		{
			assignHotkey(mouseButton - 100);
		}
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
	
		if (mc != null && mc.gameSettings != null)
		{
			if (KeyBinds.menu != null && (!mc.gameSettings.isKeyDown(KeyBinds.menu) && !editing) || CyberwareAPI.getCapability(mc.thePlayer).getNumActiveItems() < 1)
			{
				if (this.selectedPart != -1 && !editing)
				{
					ICyberwareUserData data = CyberwareAPI.getCapability(mc.thePlayer);
					ItemStack hki = data.getActiveItems().get(this.selectedPart);
					ClientUtils.useActiveItemClient(mc.thePlayer, hki);

				}
				mc.displayGuiScreen(null);
			}
			
			ImmutableSet<KeyBinding> set = ImmutableSet.of(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump);
			for(KeyBinding keybind : set)
			{
				boolean ee = false;
				int key = keybind.getKeyCode();

				if (!editing)
				{
					if (key < 0) {
						int button = 100 + key;
						ee = Mouse.isButtonDown(button);
					}
					else
					{
						ee = Keyboard.isKeyDown(key);
					}
				}
				KeyBinding.setKeyBindState(key, ee);
			}
			
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		//mc.thePlayer.setSprinting(MiscHandler.wasSprinting);
	}
	
	private void assignHotkey(int code)
	{
		ICyberwareUserData data = CyberwareAPI.getCapability(mc.thePlayer);

		
		HotkeyHelper.removeHotkey(data, code);
		HotkeyHelper.assignHotkey(data, data.getActiveItems().get(selectedPart), code);
		
		CyberwarePacketHandler.INSTANCE.sendToServer(new SyncHotkeyPacket(selectedPart, code));
		editing = false;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if (keyCode == Keyboard.KEY_ESCAPE)
		{
			this.editing = false;
		}
		else if (keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT)
		{
			
		}
		else if (keyCode == KeyBinds.menu.getKeyCode())
		{
			
		}
		else if (this.selectedPart != -1 && editing)
		{
			boolean shiftPressed = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
			assignHotkey(keyCode + (shiftPressed ? 900 : 0));
			/*for (int i = 0; i < mc.gameSettings.keyBindsHotbar.length; i++)
			{
				KeyBinding kb = mc.gameSettings.keyBindsHotbar[i];
				if (kb.getKeyCode() == keyCode)
				{
					ICyberwareUserData data = CyberwareAPI.getCapability(mc.thePlayer);
					
					if (HotkeyHelper.getHotkey(data.getActiveItems().get(selectedPart)) != i)
					{
						HotkeyHelper.removeHotkey(data, i);
						HotkeyHelper.assignHotkey(data, data.getActiveItems().get(selectedPart), i);
						CyberwarePacketHandler.INSTANCE.sendToServer(new SyncHotkeyPacket(selectedPart, i));
					}

					return;
				}
			}*/
		}
	}
}
