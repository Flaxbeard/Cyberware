package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.entity.EntityBytebug;

@SideOnly(Side.CLIENT)
public class RenderBytebug extends RenderLiving<EntityBytebug>
{
	private static final ResourceLocation BYTEBUG_TEXTURES = new ResourceLocation(Cyberware.MODID + ":textures/models/drmbug.png");

	public RenderBytebug(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelSilverfish(), 0.3F);
	}

	protected float getDeathMaxRotation(EntityBytebug entityLivingBaseIn)
	{
		return 180.0F;
	}

	protected ResourceLocation getEntityTexture(EntityBytebug entity)
	{
		return BYTEBUG_TEXTURES;
	}
}