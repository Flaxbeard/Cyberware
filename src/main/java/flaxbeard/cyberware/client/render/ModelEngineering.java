package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEngineering extends ModelBase
{
	public ModelRenderer head;
	public ModelRenderer bar;

	public ModelEngineering()
	{
		this.textureWidth = 24;
		this.textureHeight = 17;
		
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-3F, -2F, -3F, 6, 2, 6);
		this.bar = new ModelRenderer(this, 0, 8);
		this.bar.addBox(-1F, 0F, -1F, 2, 7, 2);
		this.head.addChild(bar);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.head.render(f5);
	}
	
	
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
