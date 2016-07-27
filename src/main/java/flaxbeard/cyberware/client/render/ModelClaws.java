package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelClaws extends ModelBase
{
    public ModelRenderer claw1;
    public ModelRenderer claw2;
    public ModelRenderer claw3;

    public ModelClaws(float modelSize)
    {
		this.textureWidth = 64;
		this.textureHeight = 64;
		
        this.claw1 = new ModelRenderer(this, 0, 0);
        this.claw1.addBox(-2.5F, 10.0F, -1.8F, 1, 7, 1, modelSize);
        this.claw1.setRotationPoint(-5.0F, 2.0F, 0.0F);
        
        this.claw2 = new ModelRenderer(this, 0, 0);
        this.claw2.addBox(-2.5F, 10.0F, -0.3F, 1, 7, 1, modelSize);
        this.claw1.addChild(claw2);
        
        this.claw3 = new ModelRenderer(this, 0, 0);
        this.claw3.addBox(-2.5F, 10.0F, 1.2F, 1, 7, 1, modelSize);
        this.claw1.addChild(claw3);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.pushMatrix();

        if (entityIn.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        this.claw1.render(scale);
    

        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
      
    }


}