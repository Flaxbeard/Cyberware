package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityRFIDContainer;

public class TileEntityRFIDContainerRenderer extends TileEntitySpecialRenderer<TileEntityRFIDContainer>
{
	private static ModelCrate model = new ModelCrate();
	private static String texture = "cyberware:textures/models/crate.png";

	@Override
	public void renderTileEntityAt(TileEntityRFIDContainer te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if (te != null)
		{
			float ticks = Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
			
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x+.5, y+.5, z+.5);
			
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (state.getBlock() == CyberwareContent.rfidContainer)
			{
				ClientUtils.bindTexture(texture);

				GlStateManager.rotate(180, 1, 0, 0);
				model.render(null, 0, 0, 0, 0, 0, .0625f);
				
				GlStateManager.translate(0, 0, -(7/16F));
				float coefficient = Math.max(0, Math.min(1F, (te.ticksExisted - te.lastOpened + partialTicks - 20) / 20F));
				coefficient = 0;
				float rotate = 90F * (float) Math.sin(coefficient * Math.PI / 2);
				GlStateManager.rotate(rotate, 1, 0, 0);
				GlStateManager.translate(0, 0, (7/16F));
				model.renderUpper(null, 0, 0, 0, 0, 0, .0625f);
			}
			
			GlStateManager.popMatrix();

		}
	}

}
