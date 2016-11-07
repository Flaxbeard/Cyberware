package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCrate extends ModelBase
{
	public ModelRenderer lower;
	public ModelRenderer upper;

	public ModelCrate()
	{
		this.textureWidth = 64;
		this.textureHeight = 64;
		
		this.lower = new ModelRenderer(this, 0, 22);
		this.lower.addBox(-8F, 0F, -7F, 16, 8, 14);

		
		this.upper = new ModelRenderer(this, 0, 0);
		this.upper.addBox(-8F, -8F, -7F, 16, 8, 14);

	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.lower.render(f5);
	}
	
	public void renderUpper(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.upper.render(f5);
	}
	
	
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
