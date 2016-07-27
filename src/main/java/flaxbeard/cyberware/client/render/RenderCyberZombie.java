package flaxbeard.cyberware.client.render;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderCyberZombie extends RenderZombie
{

	public RenderCyberZombie(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
	}
	
	private static final ResourceLocation robo = new ResourceLocation(Cyberware.MODID + ":textures/models/cyberzombie.png");
	
	@Override
	protected ResourceLocation getEntityTexture(EntityZombie entity)
	{
		return robo;
	}

}
