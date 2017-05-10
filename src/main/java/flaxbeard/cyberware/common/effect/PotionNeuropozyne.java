package flaxbeard.cyberware.common.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;

public class PotionNeuropozyne extends Potion
{
	private static final ResourceLocation resource = new ResourceLocation(Cyberware.MODID + ":textures/gui/potions.png");
	private int iconIndex = 0;
	
	public PotionNeuropozyne(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndex)
	{
		super(isBadEffectIn, liquidColorIn);
		GameRegistry.register(this, new ResourceLocation(Cyberware.MODID, name));
		setPotionName("cyberware.potion." + name);
		this.iconIndex = iconIndex;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		render(x + 6, y + 7, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		render(x + 3, y + 3, alpha);
	}

	@SideOnly(Side.CLIENT)
	private void render(int x, int y, float alpha)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(resource);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buf = tessellator.getBuffer();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.color(1, 1, 1, alpha);

		int textureX = iconIndex % 8 * 18;
		int textureY = 198 + iconIndex / 8 * 18;

		buf.pos(x, y + 18, 0).tex(textureX * 0.00390625, (textureY + 18) * 0.00390625).endVertex();
		buf.pos(x + 18, y + 18, 0).tex((textureX + 18) * 0.00390625, (textureY + 18) * 0.00390625).endVertex();
		buf.pos(x + 18, y, 0).tex((textureX + 18) * 0.00390625, textureY * 0.00390625).endVertex();
		buf.pos(x, y, 0).tex(textureX * 0.00390625, textureY * 0.00390625).endVertex();

		tessellator.draw();
	}

}
