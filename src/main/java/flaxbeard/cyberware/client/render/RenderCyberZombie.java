package flaxbeard.cyberware.client.render;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;

public class RenderCyberZombie extends RenderZombie
{
	
	private static final ResourceLocation ZOMBIE = new ResourceLocation(Cyberware.MODID + ":textures/models/cyberzombie.png");
	private static final ResourceLocation HIGHLIGHT = new ResourceLocation(Cyberware.MODID + ":textures/models/cyberzombieHighlight.png");
	private static final ResourceLocation ZOMBIE_BRUTE = new ResourceLocation(Cyberware.MODID + ":textures/models/cyberzombieBrute.png");
	private static final ResourceLocation HIGHLIGHT_BRUTE = new ResourceLocation(Cyberware.MODID + ":textures/models/cyberzombieBruteHighlight.png");

	@SideOnly(Side.CLIENT)
	public static class LayerZombieHighlight<T extends EntityCyberZombie> implements LayerRenderer<T>
	{
		private final RenderCyberZombie czRenderer;

		public LayerZombieHighlight(RenderCyberZombie spiderRendererIn)
		{
			this.czRenderer = spiderRendererIn;
		}

		public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
		{
			if (entitylivingbaseIn.isBrute())
			{
				this.czRenderer.bindTexture(HIGHLIGHT_BRUTE);
			}
			else
			{
				this.czRenderer.bindTexture(HIGHLIGHT);
			}
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

			if (entitylivingbaseIn.isInvisible())
			{
				GlStateManager.depthMask(false);
			}
			else
			{
				GlStateManager.depthMask(true);
			}

			int i = 61680;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.czRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
			j = i % 65536;
			k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			this.czRenderer.setLightmap(entitylivingbaseIn, partialTicks);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
		}

		public boolean shouldCombineTextures()
		{
			return false;
		}
	}
	
	public RenderCyberZombie(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
        List<LayerRenderer<EntityZombie>> defaultLayers = ReflectionHelper.getPrivateValue(RenderZombie.class, this, 10);
        defaultLayers.add(new LayerZombieHighlight(this));
        ReflectionHelper.setPrivateValue(RenderZombie.class, this, defaultLayers, 10);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityZombie entity)
	{
		EntityCyberZombie cz = (EntityCyberZombie) entity;
		if (cz.isBrute())
		{
			return ZOMBIE_BRUTE;
		}
		return ZOMBIE;
	}
	
	@Override
	protected void preRenderCallback(EntityZombie zombie, float partialTickTime)
    {
		EntityCyberZombie cz = (EntityCyberZombie) zombie;
        if (cz.height == (1.95F * 1.2F))
        {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }
    }

}
