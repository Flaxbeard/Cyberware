package flaxbeard.cyberware.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.block.tile.TileEntityComponentBox;

@SideOnly(Side.CLIENT)
public class GuiComponentBox extends GuiContainer
{
	/** The ResourceLocation containing the chest GUI texture. */
	public static final ResourceLocation BOX_GUI_TEXTURE = new ResourceLocation(Cyberware.MODID + ":textures/gui/componentBox.png");
	private IInventory playerInventory;
	private String name;
	/** window height is calculated with these values; the more rows, the heigher */
	private int inventoryRows;

	public GuiComponentBox(IInventory playerInventory, TileEntityComponentBox box)
	{
		super(new ContainerComponentBox(playerInventory, box));
		this.playerInventory = playerInventory;
		this.allowUserInput = false;
		int i = 222;
		int j = i - 108;
		this.inventoryRows = box.slots.getSlots() / 9;
		this.ySize = j + this.inventoryRows * 18;
		this.name = box.getDisplayName().getUnformattedText();
	}

	public GuiComponentBox(IInventory playerInventory, ItemStack itemStack)
	{
		super(new ContainerComponentBox(playerInventory, itemStack));
		this.playerInventory = playerInventory;
		this.allowUserInput = false;
		int i = 222;
		int j = i - 108;
		this.inventoryRows = 2;
		this.ySize = j + this.inventoryRows * 18;
		this.name = itemStack.getDisplayName();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRendererObj.drawString(this.name, 8, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BOX_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}
}