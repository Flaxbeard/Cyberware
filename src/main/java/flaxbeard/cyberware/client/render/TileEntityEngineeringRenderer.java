package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.block.tile.TileEntityEngineeringTable;

public class TileEntityEngineeringRenderer extends TileEntitySpecialRenderer<TileEntityEngineeringTable>
{
	private static ModelEngineering model = new ModelEngineering();
	private static String texture = "cyberware:textures/models/engineering.png";

	@Override
	public void renderTileEntityAt(TileEntityEngineeringTable te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if (te != null)
		{
			float ticks = Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;

			
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (state.getBlock() == CyberwareContent.engineering)
			{
				boolean showIcon = true;
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.translate(x+.5, y+.5, z+.5);
				GlStateManager.pushMatrix();
				float timeElapsed = Math.max(Math.min(22, Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks - te.clickedTime), 0);
				float amount;
				if (timeElapsed < 2)
				{
					amount = (timeElapsed / 2F);
				}
				else
				{
					timeElapsed -= 2;
					
					if (timeElapsed < 15)
					{
						showIcon = false;
					}
					amount = 1F - (timeElapsed / 20F);
				}
				GlStateManager.translate(0F, amount * (-6F / 16F), 0F);
				ClientUtils.bindTexture(texture);
				model.render(null, 0, 0, 0, 0, 0, 0.0625F);
				GlStateManager.popMatrix();
				
				ItemStack stack = te.slots.getStackInSlot(0);
				if (stack != null && showIcon)
				{
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.color(1F, 1F, 1F, 1F);
					
					
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
					GlStateManager.translate(0F, 0F, -1.8F / 16F);

					GlStateManager.translate(0F, -7.6F / 16F, 0F);
					GlStateManager.scale(.8F, .8F, .8F);
					GlStateManager.rotate(90F, 1F, 0F, 0F);
					

					
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
					GlStateManager.popMatrix();
				}
			

					
				GlStateManager.popMatrix();
			}
		}
	}

}
