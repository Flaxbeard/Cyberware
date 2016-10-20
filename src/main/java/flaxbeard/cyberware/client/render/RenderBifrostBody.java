package flaxbeard.cyberware.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import flaxbeard.cyberware.Cyberware;
	
public class RenderBifrostBody extends RenderBiped<EntityLiving>
{

	public RenderBifrostBody(RenderManager renderManager)
	{
		super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
	}

	private static final ResourceLocation muscles = new ResourceLocation(Cyberware.MODID + ":textures/models/bifrostMech.png");

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity)
	{
		return muscles;
	}
	
	public static class RenderBifrostBodyPlayer extends RenderPlayer
	{

		public RenderBifrostBodyPlayer(RenderManager renderManager)
		{
			super(renderManager);
		}
		
		@Override
		protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
		{
			return muscles;
		}
		
	}
	
	
}
