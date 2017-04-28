package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import flaxbeard.cyberware.common.item.ItemCyberarmTool;

public class ModelChainsaw extends ModelPlayer
{
	public ModelRenderer rDrillHousing;
	public ModelRenderer rPipes;
	public ModelRenderer rChainBase;
	public ModelRenderer[] rChainPieces;
	public ModelRenderer lDrillHousing;
	public ModelRenderer lPipes;
	public ModelRenderer lChainBase;
	public ModelRenderer[] lChainPieces;

	public ModelChainsaw()
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
		rDrillHousing.addBox(-3.501F, 4.0F, -3.0F, 5, 4, 6, 0);
		
		rChainPieces = new ModelRenderer[22];
		for (int i = 0; i < rChainPieces.length; i++)
		{
			rChainPieces[i] = new ModelRenderer(this, 0, 32 + (i % 8) * 2);
			rChainPieces[i].addBox(-1F, -0.5F, -0.5F, 2, 1, 1, 0);
			float[] offset = ItemCyberarmTool.getOffset(i);
			rChainPieces[i].setRotationPoint(offset[0] - 2, offset[1], offset[2]);
			
			float[] tO1 = ItemCyberarmTool.getOffset(i - 0.1F);
			float[] tO2 = ItemCyberarmTool.getOffset(i + 0.1F);
			float deltaY = tO2[1] - tO1[1];
			float deltaZ = tO2[2] - tO1[2];
			float rads = (float) Math.atan2(deltaZ, deltaY);
			rChainPieces[i].rotateAngleX = rads;
			
			if (i % 2 == 0)
			{
				ModelRenderer spike = new ModelRenderer(this, 6, 32 + ((i/2) % 4) * 2);
				spike.addBox(-0.5F, -0.5F, -1.49F, 1, 1, 1);
				rChainPieces[i].addChild(spike);
			}
			bipedRightArm.addChild(rChainPieces[i]);
		}
		rChainBase = new ModelRenderer(this, 0, 0);
		rChainBase.addBox(-1.5F, 7.1F, -1.0F, 1, 9, 2, 0);
		


		bipedRightArm.addChild(rDrillHousing);
		bipedRightArm.addChild(rChainBase);
		bipedRightArm.addChild(rPipes);
		
		
		bipedLeftArm = new ModelRenderer(this, 40, 16);
		bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 6, 4, 0);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		
		lPipes = new ModelRenderer(this, 0, 16);
		lPipes.addBox(4.0F, -4.0F, -2.0F, 4, 6, 4, 0.5F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		lPipes.setRotationPoint(-5.0F, 2.0F, 0.0F);
		
		lDrillHousing = new ModelRenderer(this, 16, 16);
		lDrillHousing.addBox(-1.501F, 4.0F, -3.0F, 5, 4, 6, 0);
		
		lChainPieces = new ModelRenderer[22];
		for (int i = 0; i < lChainPieces.length; i++)
		{
			lChainPieces[i] = new ModelRenderer(this, 0, 32 + (i % 8) * 2);
			lChainPieces[i].addBox(-1F, -0.5F, -0.5F, 2, 1, 1, 0);
			float[] offset = ItemCyberarmTool.getOffset(i);
			lChainPieces[i].setRotationPoint(offset[0], offset[1], offset[2]);
			
			float[] tO1 = ItemCyberarmTool.getOffset(i - 0.1F);
			float[] tO2 = ItemCyberarmTool.getOffset(i + 0.1F);
			float deltaY = tO2[1] - tO1[1];
			float deltaZ = tO2[2] - tO1[2];
			float rads = (float) Math.atan2(deltaZ, deltaY);
			lChainPieces[i].rotateAngleX = rads;
			
			if (i % 2 == 0)
			{
				ModelRenderer spike = new ModelRenderer(this, 6, 32 + ((i/2) % 4) * 2);
				spike.addBox(-0.5F, -0.5F, -1.49F, 1, 1, 1);
				lChainPieces[i].addChild(spike);
			}
			bipedLeftArm.addChild(lChainPieces[i]);
		}
		lChainBase = new ModelRenderer(this, 0, 0);
		lChainBase.addBox(0.5F, 7.1F, -1.0F, 1, 9, 2, 0);
		


		bipedLeftArm.addChild(lDrillHousing);
		bipedLeftArm.addChild(lChainBase);
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
