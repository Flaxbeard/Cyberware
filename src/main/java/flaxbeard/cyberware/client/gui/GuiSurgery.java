package flaxbeard.cyberware.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.client.gui.ContainerSurgery.SlotSurgery;
import flaxbeard.cyberware.client.render.ModelBox;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.SurgeryRemovePacket;

@SideOnly(Side.CLIENT)
public class GuiSurgery extends GuiContainer
{
	private static class GuiButtonSurgeryLocation extends GuiButton
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

				
				mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
				GL11.glTranslatef(xPos, yPos, 0);
				this.drawTexturedModalRect(0, 0, 194, 0, this.width, this.height);

				GL11.glPopMatrix();
			}
		}
	}
	
	private static class GuiButtonSurgery extends GuiButton
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
			{
			}
		}
	}
	
	private enum Type
	{
		BACK(176, 111, 18, 10),
		INDEX(176, 122, 12, 11);
		
		private int left;
		private int top;
		private int width;
		private int height;
		
		private Type(int left, int top, int width, int height)
		{
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}
	
	private static class InterfaceButton extends GuiButton
	{

		private final Type type;
		
		public InterfaceButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, Type type)
		{
			super(p_i46316_1_, p_i46316_2_, p_i46316_3_, type.width, type.height, "");
			this.type = type;
		}
	
		/**
		 * Draws this button to the screen.
		 */
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				float trans = 0.4F;
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				if (flag) trans = 0.6F;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, trans);
				mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);

	
				this.drawTexturedModalRect(this.xPosition, this.yPosition, type.left + type.width, type.top, type.width, type.height);
				
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, trans / 2F);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, type.left, type.top, type.width, type.height);

				GL11.glPopMatrix();
			}
		}
	}
	
	private static class PageConfiguration
	{
		private float rotation;
		private float x;
		private float y;
		private float scale;
		private float boxWidth;
		private float boxHeight;
		private float boxX;
		private float boxY;
		
		private PageConfiguration(float rotation, float x, float y, float scale)
		{
			this(rotation, x, y, scale, 0, 0, 0, 0);
		}
		
		private PageConfiguration(float rotation, float x, float y, float scale, float boxWidth, float boxHeight, float boxX, float boxY)
		{
			this.rotation = rotation;
			this.x = x;
			this.y = y;
			this.scale = scale;
			this.boxHeight = boxHeight;
			this.boxWidth = boxWidth;
			this.boxX = boxX;
			this.boxY = boxY;
		}

		public PageConfiguration copy()
		{
			return new PageConfiguration(rotation, x, y, scale, boxWidth, boxHeight, boxX, boxY);
		}

	}
	
	private static final ResourceLocation SURGERY_GUI_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/gui/surgery.png");
	private static final ResourceLocation GREY_TEXTURE = new ResourceLocation(Cyberware.MODID + ":textures/gui/greypx.png");
	private static final ResourceLocation BLUE_TEXTURE = new ResourceLocation(Cyberware.MODID + ":textures/gui/bluepx.png");
	public static final ResourceLocation BONE_TEXTURE = new ResourceLocation(Cyberware.MODID + ":textures/models/skin.png");

	private final InventoryPlayer playerInventory;
	private final TileEntitySurgery surgery;
	
	private Entity skeleton;
	private ModelBox box;

	private float partialTicks;
	
	private GuiButtonSurgery[] bodyIcons = new GuiButtonSurgery[7];
	private InterfaceButton back;
	private InterfaceButton index;

	private GuiButtonSurgeryLocation[] headIcons = new GuiButtonSurgeryLocation[3];
	private GuiButtonSurgeryLocation[] torsoIcons = new GuiButtonSurgeryLocation[4];
	private GuiButtonSurgeryLocation[] crossSectionIcons = new GuiButtonSurgeryLocation[3];
	private GuiButtonSurgeryLocation[] armIcons = new GuiButtonSurgeryLocation[2];
	private GuiButtonSurgeryLocation[] legIcons = new GuiButtonSurgeryLocation[2];

	private PageConfiguration current;
	private PageConfiguration target;
	private PageConfiguration ease;
	
	private ItemStack[] indexStacks;
	private int[] indexPages;
	private int[] indexNews;

	private int indexCount;
	
	private float lastTicks;
	private float addedRotate;
	//private float rotation;
	//private float targetRotation;
	//private float easeRotate;
	private float oldRotate;
	
	//private float scale;
	//private float targetScale;
	//private float easeScale;
	
	//private float xOffset;
	//private float targetX;
	//private float easeX;
	
	//private float yOffset;
	//private float targetY;
	//private float easeY;
	
	private float transitionStart = 0;
	private float operationTime = 0;
	private float amountDone = 0;
	
	private float openTime = 0;
	
	private int page;
	private boolean mouseDown;
	private int mouseDownX;
	private float[] lastDownX = new float[5];
	private float rotateVelocity = 0;
	
	private PageConfiguration[] configs = new PageConfiguration[25];
	List<SlotSurgery> visibleSlots = new ArrayList<SlotSurgery>();
	private int parent;
	
	public GuiSurgery(InventoryPlayer playerInv, TileEntitySurgery surgery)
	{
		super(new ContainerSurgery(playerInv, surgery));
		((ContainerSurgery) this.inventorySlots).gui = this;
		
		this.playerInventory = playerInv;
		this.surgery = surgery;
		this.ySize = 222;
		page = 0;
		amountDone = 1;
		
		configs[0] = new PageConfiguration(0, 0, 0, 50, 35, 35, -50, 10);
		configs[1] = new PageConfiguration(50, 0, 210, 150, 0, 0, -150, 0);
		configs[2] = new PageConfiguration(15, 0, 100, 130, 0, 0, -150, 0);
		configs[3] = new PageConfiguration(-50, 0, 100, 130, 0, 0, -150, 0);
		configs[4] = new PageConfiguration(50, 0, 100, 130, 0, 0, -150, 0);
		configs[5] = new PageConfiguration(-70, 0, 10, 130, 0, 0, -150, 0);
		configs[6] = new PageConfiguration(70, 0, 10, 130, 0, 0, -150, 0);
		configs[7] = new PageConfiguration(0, 0, 0, 50, 170, 125, 0, 0);

		configs[11] = new PageConfiguration(160, 0, 300, 200);
		configs[12] = new PageConfiguration(5, 0, 330, 220);
		configs[13] = new PageConfiguration(5, 0, 330, 220);
		configs[14] = new PageConfiguration(-20, 0, 220, 210);
		configs[15] = new PageConfiguration(0, 0, 180, 180);
		configs[16] = new PageConfiguration(0, 0, 180, 180);
		configs[17] = new PageConfiguration(0, 0, 125, 180);
		configs[18] = new PageConfiguration(0, 0, 0, 50, 190, 180, 0, 0);
		configs[19] = new PageConfiguration(0, 0, 0, 50, 170, 180, 0, 0);
		configs[20] = new PageConfiguration(0, 0, 0, 50, 170, 180, 0, 0);

		configs[21] = new PageConfiguration(-70, 0, 180, 200);
		configs[22] = new PageConfiguration(-70, 0, 120, 220);
		
		configs[23] = new PageConfiguration(10, 0, 20, 200);
		configs[24] = new PageConfiguration(10, 0, -30, 220);

		current = ease = target = configs[0].copy();
		


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
		this.buttonList.add(back = new InterfaceButton(8, i + this.xSize - 25, j + 5, Type.BACK));
		this.buttonList.add(index = new InterfaceButton(9, i + this.xSize - 22, j + 5, Type.INDEX));
		back.visible = false;
		
		this.buttonList.add(bodyIcons[6] = new GuiButtonSurgery(7, 
				i + (int) (xSize / 2 + configs[0].boxX - (configs[0].boxWidth / 2)),
				j + (int) ((125F / 2F) + 3F + configs[0].boxY - (configs[0].boxHeight / 2)),
				(int) configs[0].boxWidth, (int) configs[0].boxHeight)); // CAW

		this.buttonList.add(headIcons[0] = new GuiButtonSurgeryLocation(11, -2F, 19, 0));
		this.buttonList.add(headIcons[1] = new GuiButtonSurgeryLocation(12, 4F, 21, 2.F));
		this.buttonList.add(headIcons[2] = new GuiButtonSurgeryLocation(13, 4F, 21, -2F));
		this.buttonList.add(torsoIcons[0] = new GuiButtonSurgeryLocation(14, 1F, 8, -1F));
		this.buttonList.add(torsoIcons[1] = new GuiButtonSurgeryLocation(15, 0F, 9, -2F));
		this.buttonList.add(torsoIcons[2] = new GuiButtonSurgeryLocation(16, 0F, 9, 2F));
		this.buttonList.add(torsoIcons[3] = new GuiButtonSurgeryLocation(17, 0F, 13, 0F));
		this.buttonList.add(crossSectionIcons[0] = new GuiButtonSurgeryLocation(18, -12F, -8, -1F));
		this.buttonList.add(crossSectionIcons[1] = new GuiButtonSurgeryLocation(19, 12F, -1, 2F));
		this.buttonList.add(crossSectionIcons[2] = new GuiButtonSurgeryLocation(20, 3F, 5, 12F));
		this.buttonList.add(armIcons[0] = new GuiButtonSurgeryLocation(21, 0F, 10, -5.3F));
		this.buttonList.add(armIcons[1] = new GuiButtonSurgeryLocation(22, 0F, 16, -6.0F));
		this.buttonList.add(legIcons[0] = new GuiButtonSurgeryLocation(23, 0F, 1, -2.2F));
		this.buttonList.add(legIcons[1] = new GuiButtonSurgeryLocation(24, 0F, 6.4F, -2.2F));
		//this.buttonList.add(headIcons[0] = new GuiButtonSurgeryLocation(6, 4F, 19, 0));
		updateSlots(true);
	}
	
	private void prepTransition(int time, int targetPage)
	{
		
		if (page == index.id)
		{					
			if (targetPage == 0)
			{
				back.visible = false;
				
				page = 0;
				showHideRelevantButtons(true);
				this.ease = this.current = configs[0].copy();

				return;
			}
			else
			{
				if (targetPage >= 18 && targetPage <= 20)
				{
					this.ease = this.current = configs[targetPage].copy();
					this.page = targetPage;
					showHideRelevantButtons(true);
					return;
				}
				else
				{
					if (time == 0)
					{
						this.ease = this.current = configs[targetPage].copy();
						this.page = targetPage;
						showHideRelevantButtons(true);
						return;
					}
					this.ease = this.current = configs[0].copy();
				}
				//page = targetPage;
				//showHideRelevantButtons(true);
			}


			
		}
		
		// INDEX
		if (targetPage == index.id)
		{
			showHideRelevantButtons(false);

			page = 9;
			parent = 0;
			
			back.visible = true;
			index.visible = false;
			
			indexStacks = new ItemStack[5 * 8];
			indexPages = new int[5 * 8];
			indexNews = new int[5 * 8];

			indexCount = 0;
			for (int d = 0; d < surgery.slots.getSlots() && indexCount < indexStacks.length; d++)
			{
				ItemStack playerStack = surgery.slotsPlayer.getStackInSlot(d);
				ItemStack surgeryStack = surgery.slots.getStackInSlot(d);
				
				int nu = 0;
				ItemStack draw = null;
				if (surgeryStack != null)
				{
					draw = surgeryStack.copy();
					
					if (playerStack != null)
					{
						if (playerStack.getItem() == surgeryStack.getItem() && playerStack.getItemDamage() == surgeryStack.getItemDamage())
						{
							draw.stackSize += playerStack.stackSize;
						}
						else
						{
							indexStacks[indexCount] = playerStack.copy();
							EnumSlot slot = EnumSlot.values()[d / LibConstants.WARE_PER_SLOT];
							indexPages[indexCount] = slot.getSlotNumber();
							indexNews[indexCount] = 2;
							indexCount++;
							
							if (indexCount >= indexStacks.length)
							{
								break;
							}
						}
					}
					nu = 1;
				}
				else if (playerStack != null && surgery.discardSlots[d] == false)
				{
					draw = playerStack.copy();
				}
				else if (playerStack != null && surgery.discardSlots[d] == true)
				{
					draw = playerStack.copy();
					nu = 2;
				}
				
				if (draw != null)
				{
					indexStacks[indexCount] = draw;
					EnumSlot slot = EnumSlot.values()[d / LibConstants.WARE_PER_SLOT];
					indexPages[indexCount] = slot.getSlotNumber();
					indexNews[indexCount] = nu;
					indexCount++;
				}
				
			}
			
			return;
		}
		
		transitionStart = ticksExisted() + partialTicks;

		current = ease;
		operationTime = amountDone * time;
		
		showHideRelevantButtons(false);
		page = targetPage;
		target = configs[page].copy();
		if (page == 0)
		{
			back.visible = false;
			//index.visible = true;
		}
		else
		{
			back.visible = true;
			index.visible = false;
		}
	}
	
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			// BACK
			if (button.id == back.id)
			{
				
				if (page != 0 || ease.rotation != 0)
				{
					int pageToGoTo = page <= 10 ? 0 : parent;
					prepTransition(20, pageToGoTo);
				}
				return;
			}

			
			openTime = 1;
			float er = (ease.rotation + 360 * 10) % 360;
			if (button.id > 10)
			{
				parent = page;
			}
			
			if (button.id == 4)
			{
				prepTransition(20, 3);
			}
			else if (button.id == 6)
			{
				prepTransition(20, 5);
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
			case 7:
				list = crossSectionIcons;
				break;
			case 5:
				list = legIcons;
				break;
			case 3:
				list = armIcons;
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
		
		//SPECIAL CASE FOR GOING BACK TO MENU
		if (page == 0)
		{
			index.visible = true;
		}
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
			case 7:
				list = crossSectionIcons;
				break;
			case 5:
				list = legIcons;
				break;
			case 3:
				list = armIcons;
				break;
		}
		
		if (page == 7)
		{
			rot += addedRotate;
		}
		
		float radRot = (float) Math.toRadians(rot);
		float sin = (float) Math.sin(radRot);
		float cos = -(float) Math.cos(radRot);
		float upDown = page == 7 ? (float) Math.sin(Math.toRadians(10)) : 0;
		
		for (int n = 0; n < list.length; n++)
		{
			list[n].xPos = (i + (sin * scale * list[n].x3 * 0.065F) + (cos * scale * (list[n].z3) * 0.065F) + (this.xSize / 2) - 2.0F) - (list[n].buttonSize / 2F);
			list[n].yPos = -upDown * (cos * scale * list[n].x3 * 0.065F) + upDown * (sin * scale * (list[n].z3) * 0.065F) +
					(j + 2 - yOffset  + 0.065F * scale * list[n].y3  + (130 / 2)) - (list[n].buttonSize / 2F);
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
		
		int essence = (int) ((surgery.essence * 1F / surgery.maxEssence) * 49);
		int criticalEssence = (int) ((CyberwareConfig.CRITICAL_ESSENCE * 1F  / surgery.maxEssence) * 49);
		// TODO int warningEssence = (int) ((LibConstants.WARNING_ESSENCE * 1F  / surgery.maxEssence) * 49);
		int warningEssence = criticalEssence;
		this.zLevel = 200;


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
		
		if (page != index.id)
		{
			// Draw the solid part of the essence bar
			this.drawTexturedModalRect(i + 5, j + 5 + (49 - essence), 176, 61 + (49 - essence), 9, Math.max(0, essence - warningEssence));
			
			this.drawTexturedModalRect(i + 5, j + 5 + (49 - Math.min(warningEssence, essence)), 229, 61 + (49 - Math.min(warningEssence, essence)), 9, Math.max(0, Math.min(warningEssence, essence) - criticalEssence));
	
			this.drawTexturedModalRect(i + 5, j + 5 + (49 - Math.min(criticalEssence, essence)), 220, 61 + (49 - Math.min(criticalEssence, essence)), 9, Math.max(0, Math.min(criticalEssence, essence)));

			// Draw the grey, emptied essence
			this.drawTexturedModalRect(i + 5, j + 5, 211, 61, 9, 49 - essence);
		}
		else
		{
			this.zLevel = 50;
			for (int w = 0; w < 8; w++)
			{
				for (int h = 0; h < 5; h++)
				{
					this.drawTexturedModalRect(i + (20 * w + 9) - 1, j + (20 * h + 50) - 1 - 26, 176, indexNews[w + h * 8] == 2 ? 18 : 43, 18, 18);
				}
			}
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
			for (int w = 0; w < 8; w++)
			{
				for (int h = 0; h < 5; h++)
				{
					this.drawTexturedModalRect(i + (20 * w + 9) - 1, j + (20 * h + 50) - 1 - 26, 176 + 18, indexNews[w + h * 8] == 2 ? 18 : 43, 18, 18);
				}
			}
			this.zLevel = 500;
		}

		
		// Draw the more-transparent slot backs
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
		for (SlotSurgery pos : visibleSlots)
		{
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1, 176 + 18, 43, 18, 18);		// Blue slot
			this.drawTexturedModalRect(i + pos.xDisplayPosition - 1, j + pos.yDisplayPosition - 1 - 26, 176 + 18, 18, 18, 18);	// Red 'slot'
		}
		
		List<String> missingSlots = new ArrayList<String>();

		if (page != index.id)
		{
			if (surgery.essence < 0)
			{
				missingSlots.add(I18n.format("cyberware.gui.noEssence"));
			}
			else if (surgery.essence < CyberwareConfig.CRITICAL_ESSENCE)
			{
				missingSlots.add(I18n.format("cyberware.gui.criticalEssence"));
			}
			
			if (surgery.missingPower)
			{
				missingSlots.add(I18n.format("cyberware.gui.noPower"));
			}
			
			
			for (int k = 0; k < surgery.isEssentialMissing.length; k++)
			{
				EnumSlot slot = EnumSlot.values()[k / 2];
				
				if (slot.isSided())
				{
					if (k % 2 ==0)
					{
						if (surgery.isEssentialMissing[k])
						{
							missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName() + ".left"));
						}
					}
					else
					{
						if (surgery.isEssentialMissing[k])
						{
							missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName() + ".right"));
						}
					}
				}
				else if (k % 2 ==0)
				{
					if (surgery.isEssentialMissing[k])
					{
						missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName()));
					}
				}
	
			}
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);

			if (missingSlots.size() > 0)
			{
				this.drawTexturedModalRect(this.xSize - 23 + i, 20 + j, 212, 43, 16, 16);
			}


		}
		

		
		this.zLevel = 0;

		GL11.glPopMatrix();
	}
	
	private static ItemStackHandler lastLastInv = new ItemStackHandler(120);
	private static ItemStackHandler lastInv = new ItemStackHandler(120);

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.partialTicks = partialTicks;
		float time = ticksExisted() + partialTicks;
		
		// Only play animation if the player's body has changed since last opening
		if (this.openTime == 0)
		{
			boolean isEqual = true;
			ItemStackHandler newSlots = new ItemStackHandler(surgery.slotsPlayer.getSlots());
			for (int i = 0; i < surgery.slotsPlayer.getSlots(); i++)
			{
				ItemStack surgeryItem = surgery.slotsPlayer.getStackInSlot(i);
				ItemStack thisItem = lastLastInv.getStackInSlot(i);
				
				if (!ItemStack.areItemsEqual(surgeryItem, thisItem))
				{
					isEqual = false;
				}
				
				newSlots.setStackInSlot(i, surgeryItem);
			}
			if (!isEqual)
			{
				openTime = time;
				lastLastInv = lastInv;
				lastInv = newSlots;
			}
			else
			{
				openTime = 1;
			}
		}
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		int i2 = this.width / 2;
		int j2 = this.height;

		this.zLevel = 0;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		this.zLevel = 0;
		
		World world = Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().theWorld : null;
		if (skeleton == null || skeleton.isDead)
		{
			skeleton = new EntitySkeleton(world);
		}
		else
		{
			skeleton.worldObj = world;
		}
		
		if (box == null)
		{
			box = new ModelBox();
		}
		
				
		GL11.glPushMatrix();
		

		// If doing a transition
		if (transitionStart != 0)
		{
			// Ensure we rotate the right way
			current.rotation = current.rotation % 360;
			
			if (Math.abs(current.rotation + 360 - target.rotation) < Math.abs(current.rotation - target.rotation))
			{
				current.rotation = current.rotation + 360;
			}
			else if (Math.abs(current.rotation - (target.rotation + 360)) < Math.abs(current.rotation - target.rotation))
			{
				current.rotation = current.rotation - 360;
			}
			
			amountDone = (time - transitionStart) / operationTime;
			ease = interpolate(amountDone, current, target);
			// If we're done, mark that we're done
			if (amountDone >= 1.0F)
			{
				transitionStart = 0;
				ease = target;
				
				showHideRelevantButtons(true);
			}
		}
		
		// Rotate the screen if the player drags (as long as we're not viewing slots)
		if (mouseDown && page <= 10)
		{
			ease.rotation = oldRotate + (mouseX - mouseDownX)  % 360;
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
			ease.rotation += rotateVelocity % 360;
			rotateVelocity *= 0.8F;
		}

		
		

		
		float endRotate = ease.rotation;// + (float) (5F * Math.sin((time) / 25F)))
		
		if (page != index.id)
		{
			mc.getTextureManager().bindTexture(SURGERY_GUI_TEXTURES);


			float percentageSkele = Math.min(1.0F, (time - openTime) / 40F);
			if (percentageSkele < 1.0F)
			{
				ease.rotation = (float) (Math.sin(Math.PI * (percentageSkele) / 2F) * 360F);
			}
			
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			scissor(i + 3, j + 3, 170, 125);
	


			// Scan line
			if (percentageSkele < 0.9F)
			{
				this.zLevel = 90;
				GL11.glPushMatrix();
	
				GL11.glTranslatef(i + xSize / 2 - 40, j + (int) (percentageSkele * 125F) + 2, 0F);
				this.drawTexturedModalRect(0, 0, 176, 110, 80, 1);
				GL11.glPopMatrix();
				this.zLevel = 0;
			}
			
	
			if (ease.boxHeight >= 35)
			{
				
				// Draw the skin cross section and box
				GL11.glPushMatrix();
				
				float height = Math.min(125, ease.boxHeight);
				float width = Math.min(170, ease.boxWidth);
				this.mc.getTextureManager().bindTexture(GREY_TEXTURE);
		
				this.zLevel = page == 0 ? 90 : 70;
				GL11.glTranslatef(i + xSize / 2 + ease.boxX - (width / 2F), j + (125F / 2F) + 3F + ease.boxY - (height / 2F), 0F);
				
				GL11.glPushMatrix();
				GL11.glScalef(width,height, 1F);
				this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
				GL11.glPopMatrix();
				
				this.mc.getTextureManager().bindTexture(BLUE_TEXTURE);
				
				GL11.glPushMatrix();
				GL11.glScalef(width, 1F, 1F);
				this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
				GL11.glTranslatef(0F, height - 1F, 0F);
				this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
				GL11.glScalef(1F, height, 1F);
				this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
				GL11.glTranslatef(width - 1F, 0F, 0F);
				this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
				GL11.glPopMatrix();
				
				GL11.glPopMatrix();
				
				if (ease.boxHeight == 35)
				{
					GL11.glPushMatrix();
					
					// Draw the connectors for the box
					GL11.glTranslatef(i + xSize / 2 + ease.boxX - (ease.boxWidth / 2F), j + (125F / 2F) + 3F + ease.boxY - (ease.boxHeight / 2F), 0F);
		
					GL11.glPushMatrix();
					this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
					GL11.glTranslatef((configs[0].boxWidth / 2F), -12F, 0F);
					GL11.glScalef(1F, 12F, 1F);
					this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
					GL11.glPopMatrix();
					
					GL11.glPushMatrix();
					this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
					GL11.glTranslatef((configs[0].boxWidth / 2F) + 1, -12F, 0F);
					GL11.glScalef(25F, 1F, 1F);
					this.drawTexturedModalRect(0, 0, 0, 0, 1, 1);
					GL11.glPopMatrix();
					
					GL11.glPopMatrix();
				}
				
				this.zLevel = 0;
				
				ClientUtils.bindTexture( "cyberware:textures/models/skin" + ".png");
				
				if (!mouseDown && page < 10)
				{
					this.addedRotate = (addedRotate + (ticksExisted() + partialTicks - lastTicks) * 2f) % 360;
				}
				this.lastTicks = ticksExisted() + partialTicks;
				renderModel(box, 
						i + xSize / 2 + ease.boxX,
						j + (125F / 2F) + 3F + ease.boxY,
						(ease.boxHeight / 50F) * 40F, endRotate + addedRotate);
			
			}
			
			scissor(i + 3, j + 3, 170, (int) (percentageSkele * 125));
			
			renderEntity(skeleton, i + (this.xSize / 2) + ease.x, j + 110 + ease.y, ease.scale, endRotate);
	
	
			scissor(i + 3, j + 3 + (int) (percentageSkele * 125), 170, 125 - (int) (percentageSkele * 125));
					
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			
			float f = player.renderYawOffset;
			float f1 = player.rotationYaw;
			float f2 = player.rotationPitch;
			float f3 = player.prevRotationYawHead;
			float f4 = player.rotationYawHead;
			
			player.renderYawOffset = player.rotationYaw = player.rotationPitch = player.prevRotationYawHead = 0;
			player.rotationYaw = skeleton.rotationYaw;
			player.rotationYawHead = skeleton.getRotationYawHead();
			float sp = player.swingProgress;
			player.swingProgress = 0F;
		  
			renderEntity(player, i + (this.xSize / 2) + ease.x, j + 115 + (ease.y) * (60F / 63F), ease.scale * (57F / 50F), ease.rotation  + (float) (5F * Math.sin((time) / 25F)));
			
			player.swingProgress = sp;
			player.renderYawOffset = f;
			player.rotationYaw = f1;
			player.rotationPitch = f2;
			player.prevRotationYawHead = f3;
			player.rotationYawHead = f4;
			
			
			
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		
		updateLocationButtons(endRotate, ease.scale, ease.y);
		
		

		drawSlots(mouseX, mouseY);
		
		
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
			oldRotate = ease.rotation;
			mouseDown = true;
			mouseDownX = mouseX;
			for (int n = 0; n < 5; n++)
			{
				lastDownX[n] = mouseDownX;
			}
		}
		
		// Right click to go back
		if (mouseButton == 1 && (page != 0 || ease.rotation != 0) && this.getSlotAtPosition(mouseX, mouseY) == null && mouseY < j + 130)
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
	
	
	private static PageConfiguration interpolate(float amountDone, PageConfiguration start, PageConfiguration end)
	{
		return new PageConfiguration(
				ease(Math.min(1.0F, amountDone), start.rotation, end.rotation),
				ease(Math.min(1.0F, amountDone), start.x, end.x),
				ease(Math.min(1.0F, amountDone), start.y, end.y),
				ease(Math.min(1.0F, amountDone), start.scale, end.scale),
				ease(Math.min(1.0F, amountDone), start.boxWidth, end.boxWidth),
				ease(Math.min(1.0F, amountDone), start.boxHeight, end.boxHeight),
				ease(Math.min(1.0F, amountDone), start.boxX, end.boxX),
				ease(Math.min(1.0F, amountDone), start.boxY, end.boxY)
				);
	}
	
	// http://stackoverflow.com/a/8317722/1754640
	private static float ease(float percent, float startValue, float endValue)
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
		GlStateManager.disableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
	}
	
	public void renderModel(ModelBase model, float x, float y, float scale, float rotation)
	{
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 120F);
		GlStateManager.scale(-scale, scale, scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		float f1 = 0.7F;
		GlStateManager.glLightModel(2899, RenderHelper.setColorBuffer(f1, f1, f1, 1.0F));
		model.render(null, 0, 0, 0, 0, 0, .0625f);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();

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
		
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		Iterator<Slot> iterator = inventorySlots.inventorySlots.iterator();
		
		RenderHelper.enableGUIStandardItemLighting();

		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 900F);
		if (page == 0 && this.transitionStart == 0)
		{
			String s = "_" + Minecraft.getMinecraft().thePlayer.getName().toUpperCase();
			this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 115, 0x1DA9C1);
		}
		
		if (page == 9)
		{
			String s = I18n.format("cyberware.gui.installed");
			this.fontRendererObj.drawString(s, 8, 9, 0x1DA9C1);
		}
		
		if (page != index.id)
		{
			String s = surgery.essence + " / " + surgery.maxEssence;
			this.fontRendererObj.drawString(s, 18, 6, 0x1DA9C1);
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

			ItemStack stack = pos.getPlayerStack();
			this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, stack, pos.xDisplayPosition, pos.yDisplayPosition - 26);
			
			if (stack != null && stack.stackSize > 1)
			{
				FontRenderer font = stack.getItem().getFontRenderer(stack);
				if (font == null) font = fontRendererObj;
				
				this.itemRender.renderItemOverlayIntoGUI(font, stack, pos.xDisplayPosition, pos.yDisplayPosition - 26, Integer.toString(stack.stackSize));
			}


			if (pos.getStack() == null && !pos.slotDiscarded())
			{
				this.itemRender.zLevel = 50;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glColorMask(true, true, true, false);
				
				this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, pos.getPlayerStack(), pos.xDisplayPosition, pos.yDisplayPosition);
				
				if (stack != null && stack.stackSize > 1)
				{
					FontRenderer font = stack.getItem().getFontRenderer(stack);
					if (font == null) font = fontRendererObj;
					
					this.itemRender.renderItemOverlayIntoGUI(font, stack, pos.xDisplayPosition, pos.yDisplayPosition, Integer.toString(stack.stackSize));
				}
				
				GL11.glColorMask(true, true, true, true);
				this.itemRender.zLevel = 500;

			}
			else if (stack != null && pos.getStack() != null && pos.getStack().getItem() == stack.getItem() && pos.getStack().getItemDamage() == stack.getItemDamage())
			{
				FontRenderer font = stack.getItem().getFontRenderer(stack);
				if (font == null) font = fontRendererObj;
				String str = pos.getStack().stackSize == 1 ? "+1" : "+";
				int width = pos.getStack().stackSize == 1 ? 0 : font.getStringWidth(Integer.toString(pos.getStack().stackSize));
				this.itemRender.renderItemOverlayIntoGUI(font, stack, pos.xDisplayPosition - width, pos.yDisplayPosition, str);
			}
			
			
			GL11.glPopMatrix();
		}
		
		
		
		if (page == index.id)
		{
			for (int zee = 0; zee < indexCount; zee++)
			{
				
				ItemStack draw = indexStacks[zee];


				int x = (zee % 8) * 20 + 9;
				int y = (zee / 8) * 20 + 24;
				this.itemRender.zLevel = 0;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glColorMask(true, true, true, false);
				
				this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, draw, x, y);
				
				if (draw != null && draw.stackSize > 1)
				{
					FontRenderer font = draw.getItem().getFontRenderer(draw);
					if (font == null) font = fontRendererObj;
					
					this.itemRender.renderItemOverlayIntoGUI(font, draw, x, y, Integer.toString(draw.stackSize));

				}
				
				FontRenderer font = draw.getItem().getFontRenderer(draw);
				if (font == null) font = fontRendererObj;
				int nu = indexNews[zee];
				if (nu == 1)
				{
					this.itemRender.renderItemOverlayIntoGUI(font, draw, x - 10, y - 10, "+");
				}
				else if (nu == 2)
				{
					this.itemRender.renderItemOverlayIntoGUI(font, draw, x - 10, y - 10, "-");
				}
				
				GL11.glColorMask(true, true, true, true);
				this.itemRender.zLevel = 500;
								
			}
		}
		
		
		List<String> missingSlots = new ArrayList<String>();

		if (page != index.id)
		{
			if (surgery.essence < 0)
			{
				missingSlots.add(I18n.format("cyberware.gui.noEssence"));
			}
			else if (surgery.essence < CyberwareConfig.CRITICAL_ESSENCE)
			{
				missingSlots.add(I18n.format("cyberware.gui.criticalEssence"));
			}
			
			if (surgery.missingPower)
			{
				missingSlots.add(I18n.format("cyberware.gui.noPower"));
			}
			
			
			for (int k = 0; k < surgery.isEssentialMissing.length; k++)
			{
				EnumSlot slot = EnumSlot.values()[k / 2];
				
				if (slot.isSided())
				{
					if (k % 2 ==0)
					{
						if (surgery.isEssentialMissing[k])
						{
							missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName() + ".left"));
						}
					}
					else
					{
						if (surgery.isEssentialMissing[k])
						{
							missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName() + ".right"));
						}
					}
				}
				else if (k % 2 ==0)
				{
					if (surgery.isEssentialMissing[k])
					{
						missingSlots.add(I18n.format("cyberware.gui.missingEssential." + slot.getName()));
					}
				}
	
			}

			
			GL11.glDisable(GL11.GL_BLEND);

			boolean ghost = false;
			boolean add = false;
			
			// See if a red 'slot' is hovered
			Slot slot = getSlotAtPosition(mouseX, mouseY + 26);
			if (slot == null || !(slot instanceof SlotSurgery))
			{
				// Otherwise, see if a blue slot is hovered and a ghost item carries over
				ghost = true;
				slot = getSlotAtPosition(mouseX, mouseY);
				if (slot != null && (slot.getStack() != null))
				{
					slot = null;
				}
				
				if ((slot instanceof SlotSurgery && ((SlotSurgery) slot).slotDiscarded()))
				{
					if (((SlotSurgery) slot).getPlayerStack() != null)
					{
						if (this.mc.thePlayer.inventory.getItemStack() == null)
						{
							add = true;
						}
						else
						{
							slot = null;
						}
					}
					
				}
			}
				
			// Draw the tooltip if there is a red slot item or ghost item that needs one drawn
			if (slot != null && slot instanceof SlotSurgery)
			{

				ItemStack stack = ((SlotSurgery) slot).getPlayerStack();
				if (add)
				{
					List<String> l = new ArrayList<String>();
					l.add(I18n.format("cyberware.gui.add", I18n.format(stack.getUnlocalizedName() + ".name")));
					this.drawHoveringText(l, mouseX - i, mouseY - j, fontRendererObj);
				}
				else
				{
					if (stack != null)
					{
						this.renderToolTip(stack, mouseX - i, mouseY - j, ghost ? 1 : 0);
					}
				}
			}
			
			if (missingSlots.size() > 0)
			{			
				if (this.isPointInRegion(this.xSize - 23, 20, 16, 16, mouseX, mouseY))
				{
					this.drawHoveringText(missingSlots, mouseX - i, mouseY - j, fontRendererObj);
				}
			}
		}
		else
		{
			for (int n = 0; n < indexCount; n++)
			{
				int x = (n % 8) * 20 + 9;
				int y = (n / 8) * 20 + 24;
				
				if (this.isPointInRegion(x - 1, y - 1, 18, 18, mouseX, mouseY))
				{
					this.renderToolTip(indexStacks[n], mouseX - i, mouseY - j, 2 + indexNews[n]);
					if (this.mouseDown)
					{
						this.parent = index.id;
						int time = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak) ? 0 : 30;
						this.prepTransition(time, indexPages[n]);
					}
				}
			}
		
			
		}
		
		
		if (page != 0 && this.isPointInRegion(this.xSize - 25, 5, back.width, back.height, mouseX, mouseY))
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.back") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		else if (page == 0 && this.isPointInRegion(this.xSize - 22, 5, index.width, index.height, mouseX, mouseY))
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.index") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		
		GL11.glDisable(GL11.GL_BLEND);

		this.zLevel = 0;
		this.itemRender.zLevel = 0;


		
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
			if (surgerySlot.getStack() == null && surgerySlot.getPlayerStack() != null && this.mc.thePlayer.inventory.getItemStack() == null)
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

	protected void renderToolTip(ItemStack stack, int x, int y, int extras)
	{
		List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i)
		{
			if (i == 0)
			{
				list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
			}
			else
			{
				list.set(i, TextFormatting.GRAY + (String)list.get(i));
			}
		}
		
		if (extras == 1)
		{
			list.add(1, I18n.format("cyberware.gui.remove"));
		}
		else if (extras >= 2)
		{
			list.add(1, I18n.format("cyberware.gui.click"));
			
			if (extras == 3)
			{
				list.set(0, list.get(0) + " " + I18n.format("cyberware.gui.added"));
			}
			else if (extras == 4)
			{
				list.set(0, list.get(0) + " " + I18n.format("cyberware.gui.removed"));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		this.drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
	}
	
}
