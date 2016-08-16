package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityBeaconPost.TileEntityBeaconPostMaster;

public class TileEntityBeaconLargeRenderer extends TileEntitySpecialRenderer<TileEntityBeaconPostMaster>
{
	private static ModelBeaconLarge model = new ModelBeaconLarge();
	private static String texture = "cyberware:textures/models/radio.png";

	@Override
	public void renderTileEntityAt(TileEntityBeaconPostMaster te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if (te != null)
		{
			float ticks = Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;

			
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (state.getBlock() == CyberwareContent.radioPost)
			{
				boolean showIcon = true;
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glTranslated(x+.5, y+10.5, z+.5);

				ClientUtils.bindTexture(texture);
				model.render(null, 0, 0, 0, 0, 0, 0.0625F);
				if (Keyboard.isKeyDown(Keyboard.KEY_SEMICOLON))
				{
					model = new ModelBeaconLarge();
				}
				GL11.glPopMatrix();
			}
		}
	}

}
