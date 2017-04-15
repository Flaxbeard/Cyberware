package flaxbeard.cyberware.client.render;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import flaxbeard.cyberware.common.handler.EssentialsMissingHandlerClient;

public class RenderCyberlimbHand
{
	private Minecraft mc = Minecraft.getMinecraft();
	private ItemRenderer ir = mc.getItemRenderer();
	private RenderManager renderManager = mc.getRenderManager();
	private RenderItem itemRenderer = mc.getRenderItem();
	public ItemStack itemStackMainHand;
	public ItemStack itemStackOffHand;
	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

	public static RenderCyberlimbHand INSTANCE = new RenderCyberlimbHand();
	
	public void renderItemInFirstPerson(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, @Nullable ItemStack p_187457_6_, float p_187457_7_)
	{
		this.renderItemInFirstPerson(p_187457_1_, p_187457_2_, p_187457_3_, p_187457_4_, p_187457_5_, p_187457_6_, p_187457_7_, p_187457_4_ == EnumHand.MAIN_HAND);
	}
	
	public void renderItemInFirstPerson(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, @Nullable ItemStack p_187457_6_, float p_187457_7_, boolean forceShow)
	{
		boolean flag = forceShow;
		EnumHandSide enumhandside = p_187457_4_ == EnumHand.MAIN_HAND ? p_187457_1_.getPrimaryHand() : p_187457_1_.getPrimaryHand().opposite();
		GlStateManager.pushMatrix();

		if (p_187457_6_ == null)
		{
			if (flag && !p_187457_1_.isInvisible())
			{
				this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
			}
		}
		else if (p_187457_6_.getItem() instanceof net.minecraft.item.ItemMap)
		{
			if (flag && itemStackOffHand == null)
			{
				this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
			}
			else
			{
				this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
			}
		}
		else
		{
			boolean flag1 = enumhandside == EnumHandSide.RIGHT;

			if (p_187457_1_.isHandActive() && p_187457_1_.getItemInUseCount() > 0 && p_187457_1_.getActiveHand() == p_187457_4_)
			{
				int j = flag1 ? 1 : -1;

				switch (p_187457_6_.getItemUseAction())
				{
					case NONE:
						this.transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case EAT:
					case DRINK:
						this.transformEatFirstPerson(p_187457_2_, enumhandside, p_187457_6_);
						this.transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case BLOCK:
						this.transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case BOW:
						this.transformSideFirstPerson(enumhandside, p_187457_7_);
						GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
						float f5 = (float)p_187457_6_.getMaxItemUseDuration() - ((float)this.mc.thePlayer.getItemInUseCount() - p_187457_2_ + 1.0F);
						float f6 = f5 / 20.0F;
						f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

						if (f6 > 1.0F)
						{
							f6 = 1.0F;
						}

						if (f6 > 0.1F)
						{
							float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
							float f3 = f6 - 0.1F;
							float f4 = f7 * f3;
							GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
						}

						GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
						GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
						GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			}
			else
			{
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_187457_5_) * (float)Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_187457_5_) * ((float)Math.PI * 2F));
				float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float)Math.PI);
				int i = flag1 ? 1 : -1;
				GlStateManager.translate((float)i * f, f1, f2);
				this.transformSideFirstPerson(enumhandside, p_187457_7_);
				this.transformFirstPerson(enumhandside, p_187457_5_);
			}

			this.renderItemSide(p_187457_1_, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
		}

		GlStateManager.popMatrix();
	}
	
	private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_)
	{
		boolean flag = p_187456_3_ != EnumHandSide.LEFT;
		float f = flag ? 1.0F : -1.0F;
		float f1 = MathHelper.sqrt_float(p_187456_2_);
		float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
		float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
		float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float)Math.PI);
		GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
		GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
		float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float)Math.PI);
		float f6 = MathHelper.sin(f1 * (float)Math.PI);
		GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
		AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
		this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
		GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
		RenderPlayer renderplayer = getEntityRenderObject(this.mc.thePlayer, p_187456_3_);
		GlStateManager.disableCull();

		if (flag)
		{
			renderplayer.renderRightArm(abstractclientplayer);
		}
		else
		{
			renderplayer.renderLeftArm(abstractclientplayer);
		}

		GlStateManager.enableCull();
	}
	
	private void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_)
	{
		int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate((float)i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
	}
	
	private void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_)
	{
		int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
		float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float)Math.PI);
		GlStateManager.rotate((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
		float f1 = MathHelper.sin(MathHelper.sqrt_float(p_187453_2_) * (float)Math.PI);
		GlStateManager.rotate((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}
	
	public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean p_187462_4_)
	{
		if (heldStack != null)
		{
			Item item = heldStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean flag = this.itemRenderer.shouldRenderItemIn3D(heldStack) && this.isBlockTranslucent(block);

			if (flag)
			{
				GlStateManager.depthMask(false);
			}

			this.itemRenderer.renderItem(heldStack, entitylivingbaseIn, transform, p_187462_4_);

			if (flag)
			{
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}
	
	private boolean isBlockTranslucent(@Nullable Block blockIn)
	{
		return blockIn != null && blockIn.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;
	}
	
	private void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_)
	{
		float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

		if (!this.mc.thePlayer.isInvisible())
		{
			GlStateManager.pushMatrix();
			GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
			this.renderArmFirstPerson(p_187465_1_, p_187465_3_, p_187465_2_);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
		float f1 = MathHelper.sqrt_float(p_187465_3_);
		float f2 = MathHelper.sin(f1 * (float)Math.PI);
		float f3 = -0.5F * f2;
		float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
		float f5 = -0.3F * MathHelper.sin(p_187465_3_ * (float)Math.PI);
		GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
		GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
		this.renderMapFirstPerson(p_187465_4_);
		GlStateManager.popMatrix();
	}
	
	private void transformEatFirstPerson(float p_187454_1_, EnumHandSide p_187454_2_, ItemStack p_187454_3_)
	{
		float f = (float)this.mc.thePlayer.getItemInUseCount() - p_187454_1_ + 1.0F;
		float f1 = f / (float)p_187454_3_.getMaxItemUseDuration();

		if (f1 < 0.8F)
		{
			float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
			GlStateManager.translate(0.0F, f2, 0.0F);
		}

		float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
		int i = p_187454_2_ == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
		GlStateManager.rotate((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_)
	{
		float f = MathHelper.sqrt_float(p_187463_3_);
		float f1 = -0.2F * MathHelper.sin(p_187463_3_ * (float)Math.PI);
		float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
		GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
		float f3 = this.getMapAngleFromPitch(p_187463_1_);
		GlStateManager.translate(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
		GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
		this.renderArms();
		float f4 = MathHelper.sin(f * (float)Math.PI);
		GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		this.renderMapFirstPerson(this.itemStackMainHand);
	}
	
	private void renderMapFirstPerson(ItemStack stack)
	{
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.translate(-0.5F, -0.5F, 0.0F);
		GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		
		if (stack != null)
		{
			MapData mapdata = Items.FILLED_MAP.getMapData(stack, Minecraft.getMinecraft().theWorld);
	
			if (mapdata != null)
			{
				this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
			}
		}

		GlStateManager.enableLighting();
	}
	
	private void renderArms()
	{
		if (!this.mc.thePlayer.isInvisible())
		{
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			this.renderArm(EnumHandSide.RIGHT);
			this.renderArm(EnumHandSide.LEFT);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}
	
	private void renderArm(EnumHandSide p_187455_1_)
	{
		this.mc.getTextureManager().bindTexture(this.mc.thePlayer.getLocationSkin());
		Render<AbstractClientPlayer> render = getEntityRenderObject(this.mc.thePlayer, p_187455_1_);
		RenderPlayer renderplayer = (RenderPlayer)render;
		GlStateManager.pushMatrix();
		float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);

		if (p_187455_1_ == EnumHandSide.RIGHT)
		{
			renderplayer.renderRightArm(this.mc.thePlayer);
		}
		else
		{
			renderplayer.renderLeftArm(this.mc.thePlayer);
		}

		GlStateManager.popMatrix();
	}
	
	public boolean leftReplacement = false;
	public boolean rightReplacement = false;
	
	private RenderPlayer getEntityRenderObject(AbstractClientPlayer p, EnumHandSide side)
	{
		if (side == EnumHandSide.RIGHT)
		{
			if (rightReplacement)
			{
				return EssentialsMissingHandlerClient.renderF;
			}
		}
		else
		{
			if (leftReplacement)
			{
				return EssentialsMissingHandlerClient.renderT;
			}
		}
		
		return (RenderPlayer) this.renderManager.<AbstractClientPlayer>getEntityRenderObject(p);

	}
	
	private float getMapAngleFromPitch(float pitch)
	{
		float f = 1.0F - pitch / 45.0F + 0.1F;
		f = MathHelper.clamp_float(f, 0.0F, 1.0F);
		f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
		return f;
	}
}
