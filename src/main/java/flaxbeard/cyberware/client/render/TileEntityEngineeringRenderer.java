package flaxbeard.cyberware.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import org.lwjgl.opengl.GL11;

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
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glTranslated(x+.5, y+.5, z+.5);
				GL11.glPushMatrix();
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
				GL11.glTranslatef(0F, amount * (-6F / 16F), 0F);
				ClientUtils.bindTexture(texture);
				model.render(null, 0, 0, 0, 0, 0, 0.0625F);
				GL11.glPopMatrix();
				
				ItemStack stack = te.slots.getStackInSlot(0);
				if (stack != null && showIcon)
				{
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GL11.glPushMatrix();
					GlStateManager.color(1F, 1F, 1F, 1F);
					
					
					EnumFacing facing = state.getValue(BlockSurgeryChamber.FACING);
					
					switch (facing)
					{
						case EAST:
							GL11.glRotatef(90F, 0F, 1F, 0F);
							break;
						case NORTH:
							GL11.glRotatef(180F, 0F, 1F, 0F);
							break;
						case SOUTH:
							break;
						case WEST:
							GL11.glRotatef(270F, 0F, 1F, 0F);
							break;
						default:
							break;
					}
					GL11.glTranslatef(0F, 0F, -1.8F / 16F);

					GL11.glTranslatef(0F, -7.6F / 16F, 0F);
					GL11.glScalef(.8F, .8F, .8F);
					GL11.glRotatef(90F, 1F, 0F, 0F);
					

					
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
					GL11.glPopMatrix();
				}
			

					
				GL11.glPopMatrix();
			}
		}
	}

}
