package flaxbeard.cyberware.client.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.tile.TileEntityScanner;

@SideOnly(Side.CLIENT)
public class GuiScanner extends GuiContainer
{
	
	
	private static final ResourceLocation SCANNER_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/gui/scanner.png");

	private InventoryPlayer playerInventory;

	private TileEntityScanner scanner;
	
	private static final String[] dots = { "", ".", "..", "...", "....", "....." };
	
	int messageNum = -1;
	boolean resetLast = false;
	
	public GuiScanner(InventoryPlayer playerInv, TileEntityScanner engineering)
	{
		super(new ContainerScanner(playerInv, engineering));
		this.playerInventory = playerInv;
		this.scanner = engineering;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		//GlStateManager.enableBlend();
		//GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		

		if (scanner.slots.getStackInSlot(0) != null && scanner.slots.getStackInSlot(0).stackSize > 0 && (scanner.slots.getStackInSlot(2) == null || scanner.slots.getStackInSlot(2).stackSize == 0))
		{
			int maxMessage = getMaxMessage(mc.gameSettings.language);
	
			int ticks = mc.getMinecraft().thePlayer.ticksExisted / 10;
			int dotsNum = ticks % 6;
			if ((dotsNum == 0 && !resetLast) || messageNum == -1 || messageNum >= maxMessage)
			{
				if (dotsNum == 0)
				{
					resetLast = true;
				}
				messageNum = mc.getMinecraft().theWorld.rand.nextInt(maxMessage);
			}
			if (dotsNum != 0)
			{
				resetLast = false;
			}
			String baseMessage = I18n.format("cyberware.gui.scannerSaying." + messageNum);
			String message = baseMessage + dots[dotsNum];
			this.fontRendererObj.drawString(message, 6, 20, 0x1F6D7C);

		}
		
		String s = this.scanner.getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, 6, 7, 0x1DA9C1);
		
		
		float chance = 0F;
		if (scanner.slots.getStackInSlot(0) != null)
		{
			chance = CyberwareConfig.SCANNER_CHANCE + (CyberwareConfig.SCANNER_CHANCE_ADDL * (scanner.slots.getStackInSlot(0).stackSize - 1));
			if (scanner.slots.getStackInSlot(0).isItemStackDamageable())
			{
				chance = 50F * (1F - (scanner.slots.getStackInSlot(0).getItemDamage() * 1F  / scanner.slots.getStackInSlot(0).getMaxDamage()));
			}
			chance = Math.min(chance, 50F);
		}
		String num = Float.toString(Math.round(chance * 100F) / 100F) + "%";
		s = I18n.format("cyberware.gui.percent", num);
		this.fontRendererObj.drawString(s, this.xSize - 6 - fontRendererObj.getStringWidth(s), 7, 0x1DA9C1);
		
		
		int progress = (int) Math.ceil(scanner.getProgress() * 162);
		
		this.mc.getTextureManager().bindTexture(SCANNER_TEXTURES);

		GlStateManager.enableBlend();
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
		this.drawTexturedModalRect(5, 32, 0, 175, progress, 9);

		this.drawTexturedModalRect(5 + progress, 32, 0 + progress, 166, 162 - progress, 9);
		
		GlStateManager.disableBlend();

		if (this.isPointInRegion(35, 53, 16, 16, mouseX, mouseY) && scanner.slots.getStackInSlot(0) == null)
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.toScan") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		if (this.isPointInRegion(15, 53, 16, 16, mouseX, mouseY) && scanner.slots.getStackInSlot(1) == null)
		{
			this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.paper") } ), mouseX - i, mouseY - j, fontRendererObj);
		}
		
		if (scanner.ticks > 0)
		{
			if (this.isPointInRegion(5, 32, 162, 9, mouseX, mouseY))
			{
				int ticksLeft = CyberwareConfig.SCANNER_TIME - scanner.ticks;
				int seconds = (ticksLeft % 1200) / 20;
				int minutes = (ticksLeft / 1200);
				this.drawHoveringText(Arrays.asList(new String[] { I18n.format("cyberware.gui.timeLeft", minutes, seconds) } ), mouseX - i, mouseY - j, fontRendererObj);
			}
		}
	}

	private static Map<String, Integer> langMax = new HashMap<String, Integer>();

	private int getMaxMessage(String language)
	{
		if (langMax.containsKey(language))
		{
			return langMax.get(language);
		}
		else
		{
			int count = Integer.parseInt(I18n.format("cyberware.gui.scannerSaying.count")) - 1;
			
			langMax.put(language, count);
			return count;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SCANNER_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
