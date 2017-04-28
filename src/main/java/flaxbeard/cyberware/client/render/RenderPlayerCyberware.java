package flaxbeard.cyberware.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.render.ISpecialArmRenderer;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;

public class RenderPlayerCyberware extends RenderPlayer
{

	public boolean doMuscles = false;
	public boolean doCustom = false;
	public boolean canHoldItems = true;
	public ResourceLocation texture = robo;

	public RenderPlayerCyberware(RenderManager renderManager, boolean arms)
	{
		super(renderManager, arms);
	}

	private static final ResourceLocation muscles = new ResourceLocation(Cyberware.MODID + ":textures/models/playerMuscles.png");
	public static final ResourceLocation robo = new ResourceLocation(Cyberware.MODID + ":textures/models/playerRobot.png");
	public static final ResourceLocation roboRust = new ResourceLocation(Cyberware.MODID + ":textures/models/playerRustyRobot.png");

	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
	{
		return doCustom ? texture :
			doMuscles ? muscles : super.getEntityTexture(entity);
	}
	
	public void setMainModel(ModelPlayer m)
	{
		this.mainModel = m;
	}
	
	private static ModelClaws claws = new ModelClaws(0.0F);
	
	@Override
	public void renderRightArm(AbstractClientPlayer clientPlayer)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		super.renderRightArm(clientPlayer);
		
		if (mainModel instanceof ISpecialArmRenderer)
		{
			((ISpecialArmRenderer)mainModel).postRenderArm(EnumHandSide.RIGHT);
		}
		
		if (CyberwareAPI.isCyberwareInstalled(clientPlayer, new ItemStack(CyberwareContent.handUpgrades, 1, 1)) && CyberwareAPI.isCyberwareInstalled(clientPlayer, new ItemStack(CyberwareContent.cyberlimbs, 1, 1)) && Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.RIGHT && clientPlayer.getHeldItemMainhand() == null
				&& EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(clientPlayer, new ItemStack(CyberwareContent.handUpgrades, 1, 1))))
		{
			GlStateManager.pushMatrix();

			float percent = ((Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4F);
			percent = Math.min(1.0F, percent);
			percent = Math.max(0F, percent);
			percent = (float) Math.sin(percent * Math.PI / 2F);
			claws.claw1.rotateAngleY = 0.00F;
			claws.claw1.rotateAngleZ = 0.07F;
			claws.claw1.rotateAngleX = 0.00F;
			claws.claw1.setRotationPoint(-5.0F, -5.0F + (7F * percent), 0.0F);
			claws.claw1.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void renderLeftArm(AbstractClientPlayer clientPlayer)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		super.renderLeftArm(clientPlayer);

		if (mainModel instanceof ISpecialArmRenderer)
		{
			((ISpecialArmRenderer)mainModel).postRenderArm(EnumHandSide.LEFT);
		}
		
		if (CyberwareAPI.isCyberwareInstalled(clientPlayer, new ItemStack(CyberwareContent.handUpgrades, 1, 1))
			&& CyberwareAPI.isCyberwareInstalled(clientPlayer, new ItemStack(CyberwareContent.cyberlimbs, 1, 0))
			&& Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.LEFT && clientPlayer.getHeldItemMainhand() == null
			&& EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(clientPlayer, new ItemStack(CyberwareContent.handUpgrades, 1, 1))))
		{
			GlStateManager.pushMatrix();

			float percent = ((Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4F);
			percent = Math.min(1.0F, percent);
			percent = Math.max(0F, percent);
			percent = (float) Math.sin(percent * Math.PI / 2F);
			claws.claw1.rotateAngleY = 0.00F;
			claws.claw1.rotateAngleZ = 0.07F;
			claws.claw1.rotateAngleX = 0.00F;
			claws.claw1.setRotationPoint(-5.0F, -5.0F + (7F * percent), 0.0F);
			claws.claw1.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		boolean bla = this.getMainModel().bipedLeftArm.isHidden;
		boolean bra = this.getMainModel().bipedRightArm.isHidden;
		boolean bll = this.getMainModel().bipedLeftLeg.isHidden;
		boolean brl = this.getMainModel().bipedRightLeg.isHidden;
		
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(entity, this, partialTicks, x, y, z))) return;
		
		this.getMainModel().bipedLeftArm.isHidden |= bla;
		this.getMainModel().bipedRightArm.isHidden |= bra;
		this.getMainModel().bipedLeftLeg.isHidden |= bll;
		this.getMainModel().bipedRightLeg.isHidden |= brl;

		if (!entity.isUser() || this.renderManager.renderViewEntity == entity)
		{
			double d0 = y;

			if (entity.isSneaking() && !(entity instanceof EntityPlayerSP))
			{
				d0 = y - 0.125D;
			}

			this.setModelVisibilities(entity);
			GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
			doRenderELB(entity, x, d0, z, entityYaw, partialTicks);
			GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
		}
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(entity, this, partialTicks, x, y, z));
	}
	
	public void doRenderELB(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
		boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		this.mainModel.isRiding = shouldSit;
		this.mainModel.isChild = entity.isChild();
		
		ItemStack head = entity.inventory.armorInventory[3];
		ItemStack body = entity.inventory.armorInventory[2];
		ItemStack legs = entity.inventory.armorInventory[1];
		ItemStack shoes = entity.inventory.armorInventory[0];
		ItemStack heldItem = entity.getHeldItemMainhand();
		ItemStack offHand = entity.getHeldItemOffhand();

		if (doCustom)
		{
			entity.inventory.armorInventory[0] = null;	
			entity.inventory.armorInventory[1] = null;
		}
		if (entity.getPrimaryHand() == EnumHandSide.RIGHT)
		{
			if (getMainModel().bipedRightArm.isHidden || !canHoldItems) entity.inventory.mainInventory[entity.inventory.currentItem] = null;
			if (getMainModel().bipedLeftArm.isHidden || !canHoldItems) entity.inventory.offHandInventory[0] = null;
		}
		else
		{
			if (getMainModel().bipedLeftArm.isHidden || !canHoldItems) entity.inventory.mainInventory[entity.inventory.currentItem] = null;
			if (getMainModel().bipedRightArm.isHidden || !canHoldItems) entity.inventory.offHandInventory[0] = null;
		}
		
		try
		{
			float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
			float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
			float f2 = f1 - f;

			if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase)
			{
				EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
				f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
				f2 = f1 - f;
				float f3 = MathHelper.wrapDegrees(f2);

				if (f3 < -85.0F)
				{
					f3 = -85.0F;
				}

				if (f3 >= 85.0F)
				{
					f3 = 85.0F;
				}

				f = f1 - f3;

				if (f3 * f3 > 2500.0F)
				{
					f += f3 * 0.2F;
				}
			}

			float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
			this.renderLivingAt(entity, x, y, z);
			float f8 = this.handleRotationFloat(entity, partialTicks);
			this.rotateCorpse(entity, f8, f, partialTicks);
			float f4 = this.prepareScale(entity, partialTicks);
			float f5 = 0.0F;
			float f6 = 0.0F;

			if (!entity.isRiding())
			{
				f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
				f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

				if (entity.isChild())
				{
					f6 *= 3.0F;
				}

				if (f5 > 1.0F)
				{
					f5 = 1.0F;
				}
			}

			GlStateManager.enableAlpha();
			this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
			this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
			if (this.renderOutlines)
			{
				boolean flag1 = this.setScoreTeamColor(entity);
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(this.getTeamColor(entity));

				if (!this.renderMarker)
				{
					this.renderModel(entity, f6, f5, f8, f2, f7, f4);
				}

				if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
				{
					this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
				}

				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();

				if (flag1)
				{
					this.unsetScoreTeamColor();
				}
			}
			else
			{
				boolean flag = this.setDoRenderBrightness(entity, partialTicks);
				
				this.renderModel(entity, f6, f5, f8, f2, f7, f4);

				if (flag)
				{
					this.unsetBrightness();
				}

				GlStateManager.depthMask(true);

				if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
				{
					this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
				}
			}

			GlStateManager.disableRescaleNormal();
		}
		catch (Exception exception)
		{
		}

		entity.inventory.armorInventory[3] = head;
		entity.inventory.armorInventory[2] = body;
		entity.inventory.armorInventory[1] = legs;
		entity.inventory.armorInventory[0] = shoes;
		entity.inventory.mainInventory[entity.inventory.currentItem] = heldItem;
		entity.inventory.offHandInventory[0] = offHand;

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		
		// From Render.class
		if (!this.renderOutlines)
		{
			this.renderName(entity, x, y, z);
		}
	}

	private void setModelVisibilities(AbstractClientPlayer clientPlayer)
	{
		ModelPlayer modelplayer = this.getMainModel();

		if (clientPlayer.isSpectator())
		{
			modelplayer.setInvisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		}
		else
		{
			ItemStack itemstack = clientPlayer.getHeldItemMainhand();
			ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
			modelplayer.setInvisible(true);
			modelplayer.bipedHeadwear.showModel = modelplayer.bipedHead.isHidden ? false : clientPlayer.isWearing(EnumPlayerModelParts.HAT);
			modelplayer.bipedBodyWear.showModel = modelplayer.bipedBody.isHidden ? false : clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
			modelplayer.bipedLeftLegwear.showModel = modelplayer.bipedLeftLeg.isHidden ? false : clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
			modelplayer.bipedRightLegwear.showModel = modelplayer.bipedRightLeg.isHidden ? false : clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
			modelplayer.bipedLeftArmwear.showModel = modelplayer.bipedLeftArm.isHidden ? false : clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
			modelplayer.bipedRightArmwear.showModel = modelplayer.bipedRightArm.isHidden ? false :clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
			modelplayer.isSneak = clientPlayer.isSneaking();
			ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
			ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

			if (itemstack != null)
			{
				modelbiped$armpose = ModelBiped.ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction = itemstack.getItemUseAction();

					if (enumaction == EnumAction.BLOCK)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
					}
					else if (enumaction == EnumAction.BOW)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
					}
				}
			}

			if (itemstack1 != null)
			{
				modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction1 = itemstack1.getItemUseAction();

					if (enumaction1 == EnumAction.BLOCK)
					{
						modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
					}
				}
			}

			if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
			{
				modelplayer.rightArmPose = modelbiped$armpose;
				modelplayer.leftArmPose = modelbiped$armpose1;
			}
			else
			{
				modelplayer.rightArmPose = modelbiped$armpose1;
				modelplayer.leftArmPose = modelbiped$armpose;
			}
		}
	}

}
