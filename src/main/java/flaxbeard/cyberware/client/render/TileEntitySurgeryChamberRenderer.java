package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgeryChamber;

public class TileEntitySurgeryChamberRenderer extends TileEntitySpecialRenderer<TileEntitySurgeryChamber>
{
	private static ModelSurgeryChamber model = new ModelSurgeryChamber();
	private static String texture = "cyberware:textures/models/surgeryChamberDoor.png";

	@Override
	public void renderTileEntityAt(TileEntitySurgeryChamber te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if (te != null)
		{
			float ticks = Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
			
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x+.5, y+.5, z+.5);
			
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (state.getBlock() == CyberwareContent.surgeryChamber)
			{
				
				EnumFacing facing = state.getValue(BlockSurgeryChamber.FACING);
				
				switch (facing)
				{
					case EAST:
						GlStateManager.rotate(90F, 0F, 1F, 0F);
						break;
					case NORTH:
						GlStateManager.rotate(180F, 0F, 1F, 0F);
						break;
					case SOUTH:
						break;
					case WEST:
						GlStateManager.rotate(270F, 0F, 1F, 0F);
						break;
					default:
						break;
				}
				ClientUtils.bindTexture(texture);
	
				boolean isOpen = state.getValue(BlockSurgeryChamber.OPEN);
				if (isOpen != te.lastOpen)
				{
					te.lastOpen = isOpen;
					te.openTicks = ticks;
				}
				
				float ticksPassed = Math.min(10, ticks - te.openTicks);
				float rotate = (float) (Math.sin(ticksPassed * ((Math.PI / 2) / 10F)) * 90F);
						
				if (!isOpen)
				{
					rotate = 90F - (float) (Math.sin(ticksPassed * ((Math.PI / 2) / 10F)) * 90F);
				}
	
				GlStateManager.pushMatrix();
				GlStateManager.translate(-6F / 16F, 0F, -6F / 16F);
				GlStateManager.rotate(-rotate, 0F, 1F, 0F);
				model.render(null, 0, 0, 0, 0, 0, .0625f);
				GlStateManager.popMatrix();
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(6F / 16F, 0F, -6F / 16F);
				GlStateManager.rotate(rotate, 0F, 1F, 0F);
				model.renderRight(null, 0, 0, 0, 0, 0, .0625f);
				GlStateManager.popMatrix();
					
				GlStateManager.popMatrix();
			}
		}
	}

}
