package flaxbeard.cyberware.client.gui;

import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.tile.TileEntityBlueprintArchive;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.EngineeringDestroyPacket;
import flaxbeard.cyberware.common.network.EngineeringSwitchArchivePacket;

@SideOnly(Side.CLIENT)
public class GuiEngineeringTable extends GuiContainer
{
	
	private static class SmashButton extends GuiButton
	{
		public SmashButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3)
		{
			super(p_i46316_1_, p_i46316_2_, p_i46316_3, 21, 21, "");
		}
	

		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				float trans = 0.4F;
				boolean down = Mouse.isButtonDown(0);
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			
				
				mc.getTextureManager().bindTexture(ENGINEERING_GUI_TEXTURES);

	
				int i = 39;
				int j = 34;
				if (down && flag)
				{
					i = 0;
					j = 166;
				}
				this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 21, 21);
			}
		}
	}
	
	private static class NextPageButton extends GuiButton
	{
		private final boolean isForward;
	
		public NextPageButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, boolean p_i46316_4_)
		{
			super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
			this.isForward = p_i46316_4_;
		}
	
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(ENGINEERING_GUI_TEXTURES);
				int i = 21;
				int j = 166;
	
				if (flag)
				{
					i += 23;
				}
	
				if (!this.isForward)
				{
					j += 13;
				}
	
				this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 23, 13);
			}
		}
	}
	
	private static final ResourceLocation ENGINEERING_GUI_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/gui/engineering.png");

	private InventoryPlayer playerInventory;

	private TileEntityEngineeringTable engineering;

	private SmashButton smash;
	private GuiButton next;
	private GuiButton prev;
	private GuiButton nextC;
	private GuiButton prevC;
	private final int offset;

	public GuiEngineeringTable(InventoryPlayer playerInv, TileEntityEngineeringTable engineering)
	{
		super(new ContainerEngineeringTable(Minecraft.getMinecraft().thePlayer.getCachedUniqueIdString(), playerInv, engineering));
		this.playerInventory = playerInv;
		this.engineering = engineering;
		
		BlockPos pos = engineering.getPos().add(0, -1, 0);
		
		if (archive() != null)
		{
			this.xSize += 65;
		}
		if (componentBox() != null)
		{
			this.xSize += 65;
			offset = 65;
		}
		else
		{
			offset = 0;
		}
	}


	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		String s = this.engineering.getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, offset + 8, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), offset + 8, this.ySize - 96 + 2, 4210752);
		
		next.visible = prev.visible = (archive() != null && ((ContainerEngineeringTable) this.inventorySlots).archiveList.size() > 1);
		nextC.visible = prevC.visible = (componentBox() != null && ((ContainerEngineeringTable) this.inventorySlots).componentBoxList.size() > 1);

		((ContainerEngineeringTable) this.inventorySlots).canInteractWith(mc.thePlayer);
		
		if (archive() != null)
		{
			String ogName = this.archive().getDisplayName().getUnformattedText();
			
			String name = ogName.substring(0, Math.min(9, ogName.length())).trim();
			if (ogName.length() > 9)
			{
				name += "...";
			}
			
			if (ogName.length() <= 11)
			{
				name = ogName.substring(0, Math.min(11, ogName.length())).trim();
			}
			
			this.fontRendererObj.drawString(name, offset + 180, 10, 4210752);
		}
		
		Object cb = componentBox();
		if (cb != null)
		{
			String ogName = "";
			if (cb instanceof TileEntityComponentBox)
			{
				ogName = ((TileEntityComponentBox) cb).getDisplayName().getUnformattedText();
			}
			else
			{
				ogName = ((ItemStack) cb).getDisplayName();
			}
			
			String name = ogName.substring(0, Math.min(9, ogName.length())).trim();
			if (ogName.length() > 9)
			{
				name += "...";
			}
			
			if (ogName.length() <= 11)
			{
				name = ogName.substring(0, Math.min(11, ogName.length())).trim();
			}
			
			this.fontRendererObj.drawString(name, 7, 10, 4210752);
		}
		
		if (this.isPointInRegion(offset + 39, 34, 21, 21, mouseX, mouseY))
		{
			String[] tooltip;
			if (engineering.slots.getStackInSlot(1) != null)
			{
				tooltip = new String[] { I18n.format("cyberware.gui.destroy"), I18n.format("cyberware.gui.destroyChance", Float.toString(Math.round(CyberwareConfig.ENGINEERING_CHANCE * 100F) / 100F) + "%") };
			}
			else
			{
				tooltip = new String[] { I18n.format("cyberware.gui.destroy") };
			}
			this.drawHoveringText(Arrays.asList(tooltip), mouseX - i, mouseY - j, fontRendererObj);
		}
		
		if (this.isPointInRegion(offset + 15, 20, 16, 16, mouseX, mouseY) && engineering.slots.getStackInSlot(0) == null)
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.toDestroy") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		if (this.isPointInRegion(offset + 15, 53, 16, 16, mouseX, mouseY) && engineering.slots.getStackInSlot(1) == null)
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.paper") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		if (this.isPointInRegion(offset + 115, 53, 16, 16, mouseX, mouseY) && engineering.slots.getStackInSlot(8) == null)
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.blueprint") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENGINEERING_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offset + i, j, 0, 0, archive() == null ? 176 : 241, this.ySize);
		
		if (this.componentBox() != null)
		{
			this.mc.getTextureManager().bindTexture(GuiComponentBox.BOX_GUI_TEXTURE);
			this.drawTexturedModalRect(i, j, 176, 0, 65, this.ySize);
		}
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.buttonList.add(smash = new SmashButton(0, offset + i + 39, j + 34));

		this.buttonList.add(next = new NextPageButton(1, offset + i + 180, j + 131, false));
		this.buttonList.add(prev = new NextPageButton(2, offset + i + 216, j + 131, true));
		this.buttonList.add(nextC = new NextPageButton(3, i + 7, j + 131, false));
		this.buttonList.add(prevC = new NextPageButton(4, i + 43, j + 131, true));

		next.visible = prev.visible = (archive() != null && ((ContainerEngineeringTable) this.inventorySlots).archiveList.size() > 1);
		nextC.visible = prevC.visible = (componentBox() != null && ((ContainerEngineeringTable) this.inventorySlots).componentBoxList.size() > 1);

	}
	
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.id == 0)
		{
			CyberwarePacketHandler.INSTANCE.sendToServer(new EngineeringDestroyPacket(engineering.getPos(), engineering.getWorld().provider.getDimension()));
		}
		
		if (button.id == 2)
		{
			nextArchive();
		}
		
		if (button.id == 1)
		{
			prevArchive();
		}
		
		if (button.id == 4)
		{
			nextComponentBox();
		}
		
		if (button.id == 3)
		{
			prevComponentBox();
		}
	}
	
	private TileEntityBlueprintArchive archive()
	{
		return ((ContainerEngineeringTable) this.inventorySlots).archive;
	}
	
	
	private Object componentBox()
	{
		return ((ContainerEngineeringTable) this.inventorySlots).componentBox;
	}
	
	private void nextComponentBox()
	{
		CyberwarePacketHandler.INSTANCE.sendToServer(new EngineeringSwitchArchivePacket(engineering.getPos(), mc.thePlayer, true, true));

		((ContainerEngineeringTable) this.inventorySlots).nextComponentBox();
		//engineering.lastPlayerArchive.put(mc.thePlayer.getCachedUniqueIdString(), archive().getPos());

	}
	
	private void prevComponentBox()
	{
		CyberwarePacketHandler.INSTANCE.sendToServer(new EngineeringSwitchArchivePacket(engineering.getPos(), mc.thePlayer, false, true));

		((ContainerEngineeringTable) this.inventorySlots).prevComponentBox();
		//engineering.lastPlayerArchive.put(mc.thePlayer.getCachedUniqueIdString(), archive().getPos());

	}
	
	private void nextArchive()
	{
		CyberwarePacketHandler.INSTANCE.sendToServer(new EngineeringSwitchArchivePacket(engineering.getPos(), mc.thePlayer, true, false));

		((ContainerEngineeringTable) this.inventorySlots).nextArchive();
		engineering.lastPlayerArchive.put(mc.thePlayer.getCachedUniqueIdString(), archive().getPos());

	}
	
	private void prevArchive()
	{
		CyberwarePacketHandler.INSTANCE.sendToServer(new EngineeringSwitchArchivePacket(engineering.getPos(), mc.thePlayer, false, false));

		((ContainerEngineeringTable) this.inventorySlots).prevArchive();
		engineering.lastPlayerArchive.put(mc.thePlayer.getCachedUniqueIdString(), archive().getPos());

	}
}
