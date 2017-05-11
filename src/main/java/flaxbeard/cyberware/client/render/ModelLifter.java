package flaxbeard.cyberware.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import flaxbeard.cyberware.api.render.ISpecialArmRenderer;

public class ModelLifter extends ModelPlayer implements ISpecialArmRenderer
{
	public ModelRenderer rDrillHousing;
	public ModelRenderer rPipes;
	public ModelRenderer rBit1;
	public ModelRenderer rBit2;
	public ModelRenderer lDrillHousing;
	public ModelRenderer lPipes;
	public ModelRenderer lBit1;
	public ModelRenderer lBit2;
	public ItemStack stackLeft;
	public ItemStack stackRight;

	public ModelLifter()
	{
		super(0F, false);
		textureHeight = 64;
		textureWidth = 64;
		bipedRightArm = new ModelRenderer(this, 40, 16);
		bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 6, 4, 0);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		
		rPipes = new ModelRenderer(this, 0, 16);
		rPipes.addBox(2.0F, -4.0F, -2.0F, 4, 6, 4, 0.5F);
		rPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);

		rDrillHousing = new ModelRenderer(this, 0, 47);
		rDrillHousing.addBox(-3F, 4.0F, -7.0F, 4, 3, 14, 0);
		
		rBit1 = new ModelRenderer(this, 40, 39);
		rBit1.addBox(-3F, 7.0F, -2.0F, 4, 10, 2, 0);
		rBit1.setRotationPoint(0F, 0F, -1F);
		
		rBit2 = new ModelRenderer(this, 28, 39);
		rBit2.addBox(-3F, 7.0F, 0.0F, 4, 10, 2, 0);
		rBit2.setRotationPoint(0F, 0F, 1F);
		
		
		bipedRightArm.addChild(rDrillHousing);
		bipedRightArm.addChild(rBit1);
		bipedRightArm.addChild(rBit2);
		bipedRightArm.addChild(rPipes);
		
		bipedLeftArm = new ModelRenderer(this, 40, 16);
		bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 6, 4, 0);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		
		lPipes = new ModelRenderer(this, 0, 16);
		lPipes.addBox(4.0F, -4.0F, -2.0F, 4, 6, 4, 0.5F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		
		lDrillHousing = new ModelRenderer(this, 0, 47);
		lDrillHousing.addBox(-1F, 4.0F, -7.0F, 4, 3, 14, 0);
		
		lBit1 = new ModelRenderer(this, 40, 39);
		lBit1.addBox(-1F, 7.0F, -2.0F, 4, 10, 2, 0);
		lBit1.setRotationPoint(0F, 0F, -1F);
		
		lBit2 = new ModelRenderer(this, 28, 39);
		lBit2.addBox(-1F, 7.0F, 0.0F, 4, 10, 2, 0);
		lBit2.setRotationPoint(0F, 0F, 1F);
		
		
		bipedLeftArm.addChild(lDrillHousing);
		bipedLeftArm.addChild(lBit1);
		bipedLeftArm.addChild(lBit2);
		bipedLeftArm.addChild(lPipes);
		
		
		/*  d1.rotateAngleY = -(Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			d2.rotateAngleY = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
			d3.rotateAngleY = -(Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			d4.rotateAngleY = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
		 */
		this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
		this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 0, 0, 0, 0);
		this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
		this.bipedLeftArmwear = new ModelRenderer(this, 40, 32);
		this.bipedLeftArmwear.addBox(-3.0F, -2.0F, -2.0F, 0, 0, 0, 0);
		this.bipedLeftArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.bipedRightArm.rotateAngleX *= 0.3;
		this.bipedRightArm.rotateAngleX -= Math.toRadians(40);
		this.bipedLeftArm.rotateAngleX *= 0.3;
		this.bipedLeftArm.rotateAngleX -= Math.toRadians(40);
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		
		if (!bipedLeftArm.isHidden)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.bipedLeftArm.rotationPointX * (1F/16F), this.bipedLeftArm.rotationPointY * (1F/16F), this.bipedLeftArm.rotationPointZ * (1F/16F));
			GlStateManager.rotate(bipedLeftArm.rotateAngleX * 180F / (float) Math.PI, 1, 0, 0);
			GlStateManager.rotate(bipedLeftArm.rotateAngleZ * 90F / (float) Math.PI, 0, 0, 1F);
			GlStateManager.rotate(270F, 1, 0, 0);
			GlStateManager.translate(0.075F, 0F, 0.75F);
	        Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase) entityIn, stackLeft, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
		
		if (!bipedRightArm.isHidden)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.bipedRightArm.rotationPointX * (1F/16F), this.bipedRightArm.rotationPointY * (1F/16F), this.bipedRightArm.rotationPointZ * (1F/16F));
			GlStateManager.rotate(bipedRightArm.rotateAngleX * 180F / (float) Math.PI, 1, 0, 0);
			GlStateManager.rotate(bipedRightArm.rotateAngleZ * 90F / (float) Math.PI, 0, 0, 1F);
			GlStateManager.rotate(270F, 1, 0, 0);
			GlStateManager.translate(-0.075F, 0F, 0.75F);
	        Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase) entityIn, stackRight, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
		
		/*GlStateManager.rotate(270F, 1, 0, 0);
		GlStateManager.translate(0.4F, 0, 0);
		GlStateManager.translate(0, 0, 0.2F);
		GlStateManager.translate(0F, 0F, 1F);
		GlStateManager.scale(0.8, 0.8, 0.8);*/


        /*
         * 		GlStateManager.translate(this.bipedLeftArm.rotationPointX * (1F/16F), this.bipedLeftArm.rotationPointY * (1F/16F), this.bipedLeftArm.rotationPointZ * (1F/16F));
		GlStateManager.rotate(bipedLeftArm.rotateAngleX * 180F / (float) Math.PI, 1, 0, 0);
		GlStateManager.rotate(bipedLeftArm.rotateAngleZ * 90F / (float) Math.PI, 0, 0, 1F);
		GlStateManager.rotate(270F, 1, 0, 0);
		GlStateManager.translate(0.075F, -0.05F, 1.0F);
		GlStateManager.rotate(270F, 1, 0, 0);
		GlStateManager.rotate(15F, 1, 0, 0);
		GlStateManager.scale(1.33, 1.33, 1.33);

		Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase) entityIn, stack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
         */
	}

	@Override
	public void postRenderArm(EnumHandSide side)
	{
		GlStateManager.pushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		GlStateManager.translate(0.5F, .9F, -0.01F);
		if (side == EnumHandSide.RIGHT)
		{
			GlStateManager.translate(-1, 0F, -0);
		}
		GlStateManager.rotate(-90F, 1, 0, 0);
        Minecraft.getMinecraft().getItemRenderer().renderItem(player, side == EnumHandSide.RIGHT ? stackRight : stackLeft, ItemCameraTransforms.TransformType.FIXED);

		GlStateManager.popMatrix();		
	}


	

}
