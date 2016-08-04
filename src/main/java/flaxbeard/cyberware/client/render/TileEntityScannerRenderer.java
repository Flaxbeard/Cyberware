package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityScanner;

public class TileEntityScannerRenderer extends TileEntitySpecialRenderer<TileEntityScanner>
{
	private static ModelScanner model = new ModelScanner();
	private static String texture = "cyberware:textures/models/scanner.png";

	@Override
	public void renderTileEntityAt(TileEntityScanner te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if (te != null)
		{
			float ticks = Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
			
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(x+.5, y+.5, z+.5);
			
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (state.getBlock() == CyberwareContent.scanner)
			{
				ItemStack stack = te.slots.getStackInSlot(0);
				if (stack != null)
				{
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GL11.glPushMatrix();
					GlStateManager.color(1F, 1F, 1F, 1F);
					GL11.glTranslatef(0F, -1.6F / 16F, 0F);
					GL11.glScalef(.8F, .8F, .8F);
					GL11.glRotatef(90F, 1F, 0F, 0F);
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
					GL11.glPopMatrix();
				}
				ClientUtils.bindTexture(texture);

				int difference = Math.abs(te.x - te.lastX);
				float timeToTake = difference * 3;
				float time = Math.min(timeToTake, te.ticks + partialTicks - te.ticksMove);
				float progress = (float) Math.cos((Math.PI / 2) * (1F - (time / timeToTake)));
				if (difference == 0)
				{
					progress = 1.0F;
				}
				GL11.glTranslatef(0F, 0F, ((te.lastX + (te.x - te.lastX) * progress) + 1.5F) * 1F / 16F);
				
				model.render(null, 0, 0, 0, 0, 0, .0625f);
				
				int difference2 = Math.abs(te.z - te.lastZ);
				float timeToTake2 = difference2 * 3;
				float time2 = Math.max(0, Math.min(timeToTake2, te.ticks + partialTicks - te.ticksMove/* - timeToTake*/));
				float progress2 = (float) Math.cos((Math.PI / 2) * (1F - (time2 / timeToTake2)));
				if (difference2 == 0)
				{
					progress2 = 1.0F;
				}
				GL11.glTranslatef(((te.lastZ + (te.z - te.lastZ) * progress2) + .5F) * 1F / 16F, 0F, 0F);
				
				model.renderScanner(null, 0, 0, 0, 0, 0, .0625f);

				if (te.ticks > 0 && (progress2 >= 1F && progress >= 1F) && (((int) (te.ticks + partialTicks)) % 2F == 0))
				{
					GL11.glEnable(GL11.GL_BLEND);
					model.renderBeam(null, 0, 0, 0, 0, 0, 0.0625F);
					GL11.glDisable(GL11.GL_BLEND);
				}
				
				
				GL11.glPopMatrix();
			}
		}
	}

}
