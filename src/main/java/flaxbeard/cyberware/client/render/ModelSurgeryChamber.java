package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSurgeryChamber extends ModelBase
{
	public ModelRenderer left;
	public ModelRenderer right;

	public ModelSurgeryChamber()
	{
		this.textureWidth = 14;
		this.textureHeight = 29;
		
		this.left = new ModelRenderer(this, 0, 0);
		this.left.addBox(0F, -22F, -1F, 6, 28, 1);
		
		this.right = new ModelRenderer(this, 0, 0);
		this.right.addBox(-6F, -22F, -1F, 6, 28, 1);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.left.render(f5);
	}
	
	public void renderRight(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.right.render(f5);
	}
	
	
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
