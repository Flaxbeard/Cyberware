package flaxbeard.cyberware.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.HudElementBase;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.handler.HudHandler;

public class MissingPowerDisplay extends HudElementBase
{
	private static final List<ItemStack> exampleStacks = new ArrayList<ItemStack>();
	static
	{
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
		exampleStacks.add(new ItemStack(CyberwareContent.cybereyes));
	}
	
	public MissingPowerDisplay()
	{
		super("cyberware:missingPower");
		setDefaultX(-15);
		setDefaultY(35);
		setWidth(16 + 20);
		setHeight(18 * 8);
	}
	
	@Override
	public void renderElement(int x, int y, EntityPlayer p, ScaledResolution resolution, boolean hudjackAvailable, boolean isConfigOpen, float partialTicks)
	{
		if (!isHidden() && hudjackAvailable)
		{
			boolean flipHoriz = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;
			float currTime = p.ticksExisted + partialTicks;
			
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			ICyberwareUserData data = CyberwareAPI.getCapability(p);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(HudHandler.HUD_TEXTURE);
	
			
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		
			RenderItem ir = Minecraft.getMinecraft().getRenderItem();
			List<ItemStack> stacks = isConfigOpen ? exampleStacks : data.getPowerOutages();
			List<Integer> stackTimes = data.getPowerOutageTimes();
			List<Integer> toRemove = new ArrayList<Integer>();
			float zL = ir.zLevel;
			ir.zLevel = -300;
			int left = x - 1 + (flipHoriz ? 0 : 20);
			int top = y;
			for (int i = stacks.size() - 1; i >= 0; i--)
			{
				ItemStack stack = stacks.get(i);
				if (stack != null)
				{
					int time = (int) currTime;
					if (isConfigOpen)
					{
						if (i == 0)
						{
							time = (int) (currTime - 20 - (p.ticksExisted % 40));
						}
					}
					else
					{
						time = stackTimes.get(i);
					}
					boolean keep = p.ticksExisted - time < 50;
					double pct = Math.max(0F, ((currTime - time - 20) / 30F));

					float move = (float) ((20 * Math.sin(pct * (Math.PI / 2F))));
					if (keep)
					{
						
						GlStateManager.pushMatrix();
						GlStateManager.translate(flipHoriz ? move : -move, 0F, 0F);
						
						fr.drawStringWithShadow("!", left + 14, top + 8, 0xFF0000);
						
						RenderHelper.enableStandardItemLighting();
						ir.renderItemAndEffectIntoGUI(stack, left, top);
						RenderHelper.disableStandardItemLighting();

						GlStateManager.popMatrix();
						top += 18;
					}
					else if (!isConfigOpen)
					{
						toRemove.add(i);
					}
				}
			}
			ir.zLevel = zL;
			
			for (int i : toRemove)
			{
				stacks.remove(i);
				stackTimes.remove(i);
			}
			
			GlStateManager.popMatrix();
		}
	}
}
