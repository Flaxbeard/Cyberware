package flaxbeard.cyberware.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.client.gui.ContainerSurgery.SlotSurgery;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.SurgeryRemovePacket;

@SideOnly(Side.CLIENT)
public class GuiSurgery extends GuiContainer
{
	private class GuiButtonSurgeryLocation extends GuiButton
	{
		private static final int buttonSize = 16;
		private float x3;
		private float y3;
		private float z3;
		private float xPos;
		private float yPos;
		
		public GuiButtonSurgeryLocation(int buttonId, float x3, float y3, float z3)
		{
			super(buttonId, 0, 0, buttonSize, buttonSize, "");
			this.x3 = x3;
			this.y3 = y3;
			this.z3 = z3;
			this.visible = false;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				
				float trans = 0.4F;
				if (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + buttonSize && mouseY < this.yPosition + buttonSize)
				{
					trans = 0.6F;
				}
				GL11.glColor4f(1.0F, 1.0F, 1.0F, trans);
				
				OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
				GlStateManager.disableTexture2D();
				OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
				
				mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
				GL11.glTranslatef(xPos, yPos, 0);
				this.drawTexturedModalRect(0, 0, 194, 0, this.width, this.height);
				
				OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
				GlStateManager.enableTexture2D();
				OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
				
				GL11.glPopMatrix();
			}
		}
	}
	
	private class GuiButtonSurgery extends GuiButton
	{
		private float lastHover = 0;
		
		public GuiButtonSurgery(int buttonId, int x, int y, int xSize, int ySize)
		{
			super(buttonId, x, y, xSize, ySize, "");
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{/*
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				if (flag && lastHover == 0)
				{
					lastHover = ticksExisted() + partialTicks;
				}
				else if (!flag)
				{
					lastHover = 0;
				}
				
				float elapsed = ticksExisted() + partialTicks - lastHover;
				for (int i = 0; i < (elapsed > 15 ? 2 : 1); i++)
				{
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
		
					
		
					float timeSince = flag ? (elapsed + (15F * i)) % 30F : 0F;
					float scale = 0.8F + timeSince * 0.04F;
					float trans = .5F - timeSince * 0.02F;
				
					
					float move = ((1 - scale) * this.drawSize) / 2F;
					GL11.glTranslatef(xPosition, yPosition, 0.0F);
					GL11.glTranslatef((this.width - this.drawSize) / 2, (this.width - this.drawSize) / 2, 0.0F);
					GL11.glTranslatef(move, move, 0.0F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, trans);
					GL11.glScalef(scale, scale, scale);
					mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
		
		
					this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}*/

			}
		}
	}
	
	private class PageConfiguration
	{
		private float targetRotation;
		private float targetX;
		private float targetY;
		private float targetScale;
		
		private PageConfiguration(float targetRotation, float targetX, float targetY, float targetScale)
		{
			this.targetRotation = targetRotation;
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetScale = targetScale;
		}
	}
	
	private static final ResourceLocation SURGERY_GUI_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/gui/surgery.png");
	
	private final InventoryPlayer playerInventory;
	private final TileEntitySurgery surgery;
	
	private Entity dummy;
	private float partialTicks;
	
	private GuiButtonSurgery[] bodyIcons = new GuiButtonSurgery[6];
	private GuiButtonSurgeryLocation[] headIcons = new GuiButtonSurgeryLocation[3];
	private GuiButtonSurgeryLocation[] torsoIcons = new GuiButtonSurgeryLocation[3];

	private float rotation;
	private float targetRotation;
	private float easeRotate;
	private float oldRotate;
	
	private float scale;
	private float targetScale;
	private float easeScale;
	
	private float xOffset;
	private float targetX;
	private float easeX;
	
	private float yOffset;
	private float targetY;
	private float easeY;
	
	private float transitionStart = 0;
	private float operationTime = 0;
	private float amountDone = 0;
	
	private int page;
	private boolean mouseDown;
	private int mouseDownX;
	private float[] lastDownX = new float[5];
	private float rotateVelocity = 0;
	
	private PageConfiguration[] configs = new PageConfiguration[17];
	List<SlotSurgery> visibleSlots = new ArrayList<SlotSurgery>();
	private int parent;
	
	public GuiSurgery(InventoryPlayer playerInv, TileEntitySurgery surgery)
	{
		super(new ContainerSurgery(playerInv, surgery));
		((ContainerSurgery) this.inventorySlots).gui = this;
		
		this.playerInventory = playerInv;
		this.surgery = surgery;
		this.ySize = 222;
		rotation = targetRotation = 0;
		scale = targetScale = 50;
		xOffset = targetX = 0;
		yOffset = targetY = 0;
		page = 0;
		amountDone = 1;
		
		configs[0] = new PageConfiguration(0, 0, 0, 50);
		configs[1] = new PageConfiguration(50, 0, 210, 150);
		configs[2] = new PageConfiguration(15, 0, 100, 130);
		configs[3] = new PageConfiguration(-50, 0, 100, 130);
		configs[4] = new PageConfiguration(50, 0, 100, 130);
		configs[5] = new PageConfiguration(-70, 0, 10, 130);
		configs[6] = new PageConfiguration(70, 0, 10, 130);
		configs[11] = new PageConfiguration(160, 0, 300, 200);
		configs[12] = new PageConfiguration(5, 0, 330, 220);
		configs[13] = new PageConfiguration(5, 0, 330, 220);
		configs[14] = new PageConfiguration(-20, 0, 220, 210);
		configs[15] = new PageConfiguration(0, 0, 180, 180);
		configs[16] = new PageConfiguration(0, 0, 180, 180);

	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.buttonList.add(bodyIcons[0] = new GuiButtonSurgery(1, i + (this.xSize / 2) - 18, j + 8, 36, 27));
		this.buttonList.add(bodyIcons[1] = new GuiButtonSurgery(2, i + (this.xSize / 2) - 13, j + 35, 26, 38));
		this.buttonList.add(bodyIcons[2] = new GuiButtonSurgery(3, i + (this.xSize / 2) - 8 + 21, j + 35, 16, 38));
		this.buttonList.add(bodyIcons[3] = new GuiButtonSurgery(4, i + (this.xSize / 2) - 8 - 21, j + 35, 16, 38));
		this.buttonList.add(bodyIcons[4] = new GuiButtonSurgery(5, i + (this.xSize / 2) - 6 + 7, j + 73, 12, 39));
		this.buttonList.add(bodyIcons[5] = new GuiButtonSurgery(6, i + (this.xSize / 2) - 6 - 7, j + 73, 12, 39));
		this.buttonList.add(headIcons[0] = new GuiButtonSurgeryLocation(11, -2F, 19, 0));
		this.buttonList.add(headIcons[1] = new GuiButtonSurgeryLocation(12, 4F, 21, 2.F));
		this.buttonList.add(headIcons[2] = new GuiButtonSurgeryLocation(13, 4F, 21, -2F));
		this.buttonList.add(torsoIcons[0] = new GuiButtonSurgeryLocation(14, 1F, 8, -1F));
		this.buttonList.add(torsoIcons[1] = new GuiButtonSurgeryLocation(15, 0F, 9, -2F));
		this.buttonList.add(torsoIcons[2] = new GuiButtonSurgeryLocation(16, 0F, 9, 2F));

		//this.buttonList.add(headIcons[0] = new GuiButtonSurgeryLocation(6, 4F, 19, 0));
		updateSlots(true);
	}
	
	private void prepTransition(int time, int targetPage)
	{
		transitionStart = ticksExisted() + partialTicks;

		scale = easeScale;
		yOffset = easeY;
		xOffset = easeX;
		rotation = easeRotate;
		operationTime = amountDone * time;
		
		showHideRelevantButtons(false);
		page = targetPage;
		targetRotation = configs[page].targetRotation;
		targetScale = configs[page].targetScale;
		targetY = configs[page].targetY;
		targetX = configs[page].targetX;
	}
	
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			float er = (easeRotate + 360 * 10) % 360;
			if (button.id > 10)
			{
				parent = page;
			}
			if (button.id == 3 && er > 180)
			{
				prepTransition(20, 4);
			}
			else if (button.id == 4 && er > 180)
			{
				prepTransition(20, 3);
			}
			else if (button.id == 6 &&  er > 180)
			{
				prepTransition(20, 5);
			}
			else if (button.id == 5 && er  > 180)
			{
				prepTransition(20, 6);
			}
			else if (button.id == 13)
			{
				prepTransition(20, 12);
			}
			else if (button.id == 16)
			{
				prepTransition(20, 15);
			}
			else
			{
				prepTransition(20, button.id);
			}


			
		}
	}
	
	private void showHideRelevantButtons(boolean show)
	{
		GuiButton[] list = new GuiButton[0];
		
		switch(page)
		{
			case 0:
				list = bodyIcons;
				break;
			case 1:
				list = headIcons;
				break;
			case 2:
				list = torsoIcons;
				break;
		}
		
		for (int i = 0; i < list.length; i++)
		{
			list[i].visible = show;
		}
		
		updateSlots(show);
	}
	
	private void updateLocationButtons(float rot, float scale, float yOffset)
	{
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		GuiButtonSurgeryLocation[] list = new GuiButtonSurgeryLocation[0];
		
		switch(page)
		{
			case 1:
				list = headIcons;
				break;
			case 2:
				list = torsoIcons;
				break;
		}
		
		float radRot = (float) Math.toRadians(rot);
		float sin = (float) Math.sin(radRot);
		float cos = -(float) Math.cos(radRot);
		
		for (int n = 0; n < list.length; n++)
		{
			list[n].xPos = (i + (sin * scale * list[n].x3 * 0.065F) + (cos * scale * (list[n].z3) * 0.065F) + (this.xSize / 2) - 2.0F) - (list[n].buttonSize / 2F);
			list[n].yPos = (j + 2 - yOffset  + 0.065F * scale * list[n].y3  + (130 / 2)) - (list[n].buttonSize / 2F);
			list[n].xPosition = Math.round(list[n].xPos);
			list[n].yPosition = Math.round(list[n].yPos);

		}
	}
	
	private void drawSlots(int mouseX, int mouseY)
	{
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		
		int essence = (int) (1.0F * 50);
		this.zLevel = 200;
		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		

		mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
		
		
		if (surgery.wrongSlot != -1)
		{
			float trans = 1.0F - ((this.ticksExisted() + partialTicks) - surgery.ticksWrong) / 10F;
			if (trans > 0)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, trans);
	
				Slot slot = inventorySlots.inventorySlots.get(surgery.wrongSlot);
				this.drawTexturedModalRect(i + slot.xDisplayPosition - 5, j + slot.yDisplayPosition - 5, 185, 61, 26, 26);		// Blue slot
				
			}
			else
			{
				surgery.wrongSlot = -1;
			}
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		
		// Draw the less-transparent slot borders
		for (SlotSurgery pos : visibleSlots)
		{
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1, 176, 43, 18, 18);		// Blue slot
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1 - 26, 176, 18, 18, 25);	// Red 'slot'
		}
		
		// Draw the solid part of the essence bar
		this.drawTexturedModalRect(i + 5, j + 5, 176, 61, 9, essence);
		
		
		List<String> missingSlots = new ArrayList<String>();
		for (int k = 0; k < surgery.isEssentialMissing.length; k++)
		{
			if (surgery.isEssentialMissing[k])
			{
				missingSlots.add(I18n.format("cyberware.gui.missingEssential." + EnumSlot.values()[k].getName()));
			}
		}
		
		if (missingSlots.size() > 0)
		{
			this.drawTexturedModalRect(i + this.xSize - 21, j + 5, 212, 43, 16, 16);
		}
		
		// Draw the more-transparent slot backs
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
		for (SlotSurgery pos : visibleSlots)
		{
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1, 176 + 18, 43, 18, 18);		// Blue slot
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1 - 26, 176 + 18, 18, 18, 18);	// Red 'slot'
		}
		
		// Draw the more transparent, emptied essence
		this.drawTexturedModalRect(i + 5, j + 5 + essence, 176, 61 + essence, 9, 50 - essence);
		
		GL11.glDisable(GL11.GL_BLEND);

		// See if a red 'slot' is hovered
		Slot slot = getSlotAtPosition(mouseX, mouseY + 26);
		if (slot == null || !(slot instanceof SlotSurgery))
		{
			// Otherwise, see if a blue slot is hovered and a ghost item carries over
			slot = getSlotAtPosition(mouseX, mouseY);
			if (slot != null && (slot.getStack() != null || (slot instanceof SlotSurgery && ((SlotSurgery) slot).slotDiscarded())))
			{
				slot = null;
			}
		}
		
		// Draw the tooltip if there is a red slot item or ghost item that needs one drawn
		if (slot != null && slot instanceof SlotSurgery)
		{
			ItemStack stack = ((SlotSurgery) slot).getPlayerStack();
			if (stack != null)
			{
				this.renderToolTip(stack, mouseX, mouseY);
			}
		}
		
		if (missingSlots.size() > 0)
		{			
			if (this.isPointInRegion(this.xSize - 21, 5, 16, 16, mouseX, mouseY))
			{
				this.drawHoveringText(missingSlots, mouseX, mouseY, fontRendererObj);
			}
		}
		

		
		this.zLevel = 0;

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.partialTicks = partialTicks;
		float time = ticksExisted() + partialTicks;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		int i2 = this.width / 2;
		int j2 = this.height;

		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		
		World world = Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().theWorld : null;
		if (dummy == null || dummy.isDead)
		{
			dummy = new EntitySkeleton(world);
		}
		else
		{
			dummy.worldObj = world;
		}
				
		GL11.glPushMatrix();
		
		easeScale = targetScale;
		easeY = targetY;
		easeX = targetX;

		// If doing a transition
		if (transitionStart != 0)
		{
			// Ensure we rotate the right way
			rotation = rotation % 360;
			if (Math.abs(rotation + 360 - targetRotation) < Math.abs(rotation - targetRotation))
			{
				rotation = rotation + 360;
			}
			else if (Math.abs(rotation - (targetRotation + 360)) < Math.abs(rotation - targetRotation))
			{
				rotation = rotation - 360;
			}
			
			amountDone = (time - transitionStart) / operationTime;
			easeScale = ease(Math.min(1.0F, amountDone), scale, targetScale);
			easeY = ease(Math.min(1.0F, amountDone), yOffset, targetY);
			easeX = ease(Math.min(1.0F, amountDone), xOffset, targetX);
			easeRotate = ease(Math.min(1.0F, amountDone), rotation, targetRotation);

			// If we're done, mark that we're done
			if (amountDone >= 1.0F)
			{
				transitionStart = 0;
				easeRotate = targetRotation;

				showHideRelevantButtons(true);
			}
		}
		
		// Rotate the screen if the player drags (as long as we're not viewing slots)
		if (mouseDown && page <= 10)
		{
			easeRotate = oldRotate + (mouseX - mouseDownX)  % 360;
			for (int n = 1; n < 5; n++)
			{
				lastDownX[n] = lastDownX[n - 1];
			}
			lastDownX[0] = mouseX;
		}
		else
		{
			if (page > 10)
			{
				rotateVelocity = 0;
			}
			easeRotate += rotateVelocity % 360;
			rotateVelocity *= 0.8F;
		}

		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissor(i + 3, j + 3, 170, 125);

		float endRotate = easeRotate;// + (float) (5F * Math.sin((time) / 25F)))
		renderEntity(dummy, i + (this.xSize / 2) + easeX, j + 110 + easeY, easeScale, endRotate);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		updateLocationButtons(endRotate, easeScale, easeY);

		drawSlots(mouseX, mouseY);

		/*
		
		float transparent = Math.abs(1F - ((Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks) % 40) / 20F);
		transparent = .5F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, transparent);
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		float f = player.renderYawOffset;
		float f1 = player.rotationYaw;
		float f2 = player.rotationPitch;
		float f3 = player.prevRotationYawHead;
		float f4 = player.rotationYawHead;
		
		player.renderYawOffset = player.rotationYaw = player.rotationPitch = player.prevRotationYawHead = 0;
		player.rotationYaw = dummy.rotationYaw;
		player.rotationYawHead = dummy.getRotationYawHead();
		float sp = player.swingProgress;
		player.swingProgress = 0F;
	  
		renderEntity(player, i + (this.xSize / 2) + easeX, j + 115 + (easeY) * (60F / 63F), easeScale * (57F / 50F), easeRotate  + (float) (5F * Math.sin((time) / 25F)));
		
		player.swingProgress = sp;
		player.renderYawOffset = f;
		player.rotationYaw = f1;
		player.rotationPitch = f2;
		player.prevRotationYawHead = f3;
		player.rotationYawHead = f4;*/
		
		
		GL11.glPopMatrix();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		// Rotation
		if (mouseButton == 0 && mouseX >= i && mouseX < i + this.xSize && mouseY >= j && mouseY < j + 130)
		{
			oldRotate = easeRotate;
			mouseDown = true;
			mouseDownX = mouseX;
			for (int n = 0; n < 5; n++)
			{
				lastDownX[n] = mouseDownX;
			}
		}
		
		// Right click to go back
		if (mouseButton == 1 && (page != 0 || easeRotate != 0) && this.getSlotAtPosition(mouseX, mouseY) == null && mouseY < j + 130)
		{
			int pageToGoTo = page <= 10 ? 0 : parent;
			prepTransition(20, pageToGoTo);
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	// Taken from GuiContainer
	private Slot getSlotAtPosition(int x, int y)
	{
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);

			if (this.isMouseOverSlot(slot, x, y))
			{
				return slot;
			}
		}

		return null;
	}

	// Taken from GuiContainer
	private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
	{
		return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		// Make it spin! :D
		if (mouseButton == 0)
		{
			if (mouseDown)
			{
				mouseDown = false;
				rotateVelocity = (mouseX - lastDownX[4]);
				if (Math.abs(rotateVelocity) < 5)
				{
					rotateVelocity = 0;
				}
			}
		}
		super.mouseReleased(mouseX, mouseY, mouseButton);
	}
	
	private float ticksExisted()
	{
		return Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().thePlayer != null ? Minecraft.getMinecraft().thePlayer.ticksExisted : 0 : 0;
	}
	
	// http://stackoverflow.com/a/8317722/1754640
	private float ease(float percent, float startValue, float endValue)
	{
		endValue -= startValue;
		float total = 100;
		float elapsed = percent * total;
		
		if ((elapsed /= total / 2) < 1)
			return endValue / 2 * elapsed * elapsed + startValue;
		return -endValue / 2 * ((--elapsed) * (elapsed - 2) - 1) + startValue;
	}
	
	public void renderEntity(Entity entity, float x, float y, float scale, float rotation)
	{
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 50.0F);
		GlStateManager.scale(-scale, scale, scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
		Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	private void scissor(int x, int y, int xSize, int ySize)
	{
		ScaledResolution res = new ScaledResolution(mc);
		x = x * res.getScaleFactor();
		ySize = ySize * res.getScaleFactor();
		y = mc.displayHeight - (y * res.getScaleFactor()) - ySize;
		xSize = xSize * res.getScaleFactor();
		GL11.glScissor(x, y, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		Iterator<Slot> iterator = inventorySlots.inventorySlots.iterator();
		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 900F);
		if (page == 0 && this.transitionStart == 0)
		{
			String s = "_" + Minecraft.getMinecraft().thePlayer.getName().toUpperCase();
			this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 115, 0x1C7C8D);
		}

		
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_BLEND);

		this.zLevel = 500;
		this.itemRender.zLevel = 500;
		
		this.mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);

		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		// Draw red 'slot' items and ghost items
		for (SlotSurgery pos : visibleSlots)
		{
			GL11.glPushMatrix();

			this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, pos.getPlayerStack(), pos.xDisplayPosition, pos.yDisplayPosition - 26);
			
			if (pos.getStack() == null && !pos.slotDiscarded())
			{
				this.itemRender.zLevel = 50;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glColorMask(true, true, true, false);
				
				this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, pos.getPlayerStack(), pos.xDisplayPosition, pos.yDisplayPosition);
				GL11.glColorMask(true, true, true, true);
				this.itemRender.zLevel = 500;

			}
			GL11.glPopMatrix();
		}
		
		
		GL11.glDisable(GL11.GL_BLEND);

		this.zLevel = 0;
		this.itemRender.zLevel = 0;

		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		
	}


	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
	{
		if (slotIn != null && (slotIn instanceof SlotSurgery) && !isSlotAccessible((SlotSurgery) slotIn))
		{
			return;
		}
		
		if (slotIn instanceof SlotSurgery)
		{
			SlotSurgery surgerySlot = (SlotSurgery) slotIn;
			if (surgerySlot.getStack() == null && surgerySlot.getPlayerStack() != null)
			{
				int number = surgerySlot.slotNumber;
				
				ItemStack playerSlotItem = surgerySlot.getPlayerStack();
				if (surgerySlot.slotDiscarded())
				{
					if (!surgery.doesItemConflict(playerSlotItem, ((SlotSurgery) slotIn).slot, number % LibConstants.WARE_PER_SLOT))
					{
						surgerySlot.setDiscarded(false);
						surgery.enableDependsOn(playerSlotItem, ((SlotSurgery) slotIn).slot, number % LibConstants.WARE_PER_SLOT);
						CyberwarePacketHandler.INSTANCE.sendToServer(new SurgeryRemovePacket(surgery.getPos(), surgery.getWorld().provider.getDimension(), number, false));
					}
				}
				else
				{
					if (surgery.canDisableItem(playerSlotItem, ((SlotSurgery) slotIn).slot, number % LibConstants.WARE_PER_SLOT))
					{
						surgerySlot.setDiscarded(true);
						surgery.disableDependants(playerSlotItem, ((SlotSurgery) slotIn).slot, number % LibConstants.WARE_PER_SLOT);
						CyberwarePacketHandler.INSTANCE.sendToServer(new SurgeryRemovePacket(surgery.getPos(), surgery.getWorld().provider.getDimension(), number, true));
					}
				}

			}
		}
		
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
	}

	public boolean isSlotAccessible(SlotSurgery slot)
	{
		return page == slot.slot.getSlotNumber();
	}
	
	public void updateSlots(boolean show)
	{
		visibleSlots.clear();
		Iterator<Slot> i = inventorySlots.inventorySlots.iterator();
		
		Slot slot = i.next();
		while (slot != null && (slot instanceof SlotSurgery))
		{
			SlotSurgery slotSurgery = (SlotSurgery) slot;
			
			if (isSlotAccessible(slotSurgery) && show)
			{
				slotSurgery.xDisplayPosition = slotSurgery.savedXPosition;
				slotSurgery.yDisplayPosition = slotSurgery.savedYPosition;
				visibleSlots.add(slotSurgery);
			}
			else
			{
				slotSurgery.xDisplayPosition = Integer.MIN_VALUE;
				slotSurgery.yDisplayPosition = Integer.MIN_VALUE;
			}
			
			slot = i.next();
		}
	}

}
