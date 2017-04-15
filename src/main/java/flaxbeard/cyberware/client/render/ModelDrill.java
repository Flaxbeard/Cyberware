package flaxbeard.cyberware.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDrill extends ModelPlayer
{
	public ModelRenderer rDrillHousing;
	public ModelRenderer rPipes;
	public ModelRenderer rBit1;
	public ModelRenderer rBit2;
	public ModelRenderer rBit3;
	public ModelRenderer rBit4;
	public ModelRenderer lDrillHousing;
	public ModelRenderer lPipes;
	public ModelRenderer lBit1;
	public ModelRenderer lBit2;
	public ModelRenderer lBit3;
	public ModelRenderer lBit4;

	public ModelDrill()
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
		
		rDrillHousing = new ModelRenderer(this, 16, 16);
		rDrillHousing.addBox(-4.0F, 4.0F, -3.0F, 6, 4, 6, 0);
		
		rBit1 = new ModelRenderer(this, 0, 54);
		rBit1.addBox(-4.0F, 8.0F, -4.0F, 8, 2, 8, 0);
		rBit1.setRotationPoint(-1F, 0F, 0F);
		
		rBit2 = new ModelRenderer(this, 32, 54);
		rBit2.addBox(-3.0F, 10.0F, -3.0F, 6, 2, 6, 0);
		rBit2.setRotationPoint(-1F, 0F, 0F);
		
		
		rBit3 = new ModelRenderer(this, 0, 0);
		rBit3.addBox(-2.0F, 12.0F, -2.0F, 4, 2, 4, 0);
		rBit3.setRotationPoint(-1F, 0F, 0F);
		
		
		rBit4 = new ModelRenderer(this, 16, 0);
		rBit4.addBox(-1.0F, 14.0F, -1.0F, 2, 2, 2, 0);
		rBit4.setRotationPoint(-1F, 0F, 0F);
		
		bipedRightArm.addChild(rDrillHousing);
		bipedRightArm.addChild(rBit1);
		bipedRightArm.addChild(rBit2);
		bipedRightArm.addChild(rBit3);
		bipedRightArm.addChild(rBit4);
		bipedRightArm.addChild(rPipes);
		
		bipedLeftArm = new ModelRenderer(this, 40, 16);
		bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 6, 4, 0);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		
		lPipes = new ModelRenderer(this, 0, 16);
		lPipes.addBox(4.0F, -4.0F, -2.0F, 4, 6, 4, 0.5F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		
		rDrillHousing = new ModelRenderer(this, 16, 16);
		rDrillHousing.addBox(-2.0F, 4.0F, -3.0F, 6, 4, 6, 0);
		
		lBit1 = new ModelRenderer(this, 0, 54);
		lBit1.addBox(-4.0F, 8.0F, -4.0F, 8, 2, 8, 0);
		lBit1.setRotationPoint(1F, 0F, 0F);
		
		lBit2 = new ModelRenderer(this, 32, 54);
		lBit2.addBox(-3.0F, 10.0F, -3.0F, 6, 2, 6, 0);
		lBit2.setRotationPoint(1F, 0F, 0F);
		
		
		lBit3 = new ModelRenderer(this, 0, 0);
		lBit3.addBox(-2.0F, 12.0F, -2.0F, 4, 2, 4, 0);
		lBit3.setRotationPoint(1F, 0F, 0F);
		
		
		lBit4 = new ModelRenderer(this, 16, 0);
		lBit4.addBox(-1.0F, 14.0F, -1.0F, 2, 2, 2, 0);
		lBit4.setRotationPoint(1F, 0F, 0F);
		
		bipedLeftArm.addChild(rDrillHousing);
		bipedLeftArm.addChild(lBit1);
		bipedLeftArm.addChild(lBit2);
		bipedLeftArm.addChild(lBit3);
		bipedLeftArm.addChild(lBit4);
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
    

}
