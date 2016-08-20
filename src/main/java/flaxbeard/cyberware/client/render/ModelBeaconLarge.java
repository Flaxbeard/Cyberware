package flaxbeard.cyberware.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBeaconLarge extends ModelBase
{
	public ModelRenderer bar1;
	public ModelRenderer bar2;
	public ModelRenderer bar3;
	public ModelRenderer bar4;
	public ModelRenderer base;

	public ModelRenderer[] crossbars;

	public ModelBeaconLarge()
	{
		this.textureWidth = 128;
		this.textureHeight = 256;
		
		float angle = 173.8F;
		
		this.bar1 = new ModelRenderer(this, 0, 0);
		this.bar1.addBox(-1.5F, 8F, -2F, 3, 163, 3);
		this.bar1.rotateAngleY = (float) Math.toRadians(45F);
		this.bar1.rotateAngleX = (float) Math.toRadians(angle);
		
		this.bar2 = new ModelRenderer(this, 0, 0);
		this.bar2.addBox(-1.5F, 8F, -2F, 3, 163, 3);
		this.bar2.rotateAngleY = (float) Math.toRadians(135F);
		this.bar2.rotateAngleX = (float) Math.toRadians(angle);
		
		this.bar3 = new ModelRenderer(this, 0, 0);
		this.bar3.addBox(-1.5F, 8F, -2F, 3, 163, 3);
		this.bar3.rotateAngleY = (float) Math.toRadians(-45F);
		this.bar3.rotateAngleX = (float) Math.toRadians(angle);
		
		this.bar4 = new ModelRenderer(this, 0, 0);
		this.bar4.addBox(-1.5F, 8F, -2F, 3, 163, 3);
		this.bar4.rotateAngleY = (float) Math.toRadians(-135F);
		this.bar4.rotateAngleX = (float) Math.toRadians(angle);

		
		
		float hPercent = (float) -Math.cos(Math.toRadians(angle));
		float wPercent = (float) Math.sin(Math.toRadians(angle));

		int num = 6;
		
		float progressChg = 25F;
		crossbars = new ModelRenderer[num * 4];
		float x = 0F;
		float y = 0F;
		float z = 0F;
		float progress = 10F + progressChg;
		float pi4 = (float) Math.PI / 4F;
		for (int i = 0; i < num; i++)
		{
			x = (float) Math.ceil(.3F + -wPercent * progress * pi4);
			z = -1.6F + -wPercent * progress * (pi4 - .1F);

			y = -hPercent * progress;
			
			ModelRenderer bar = new ModelRenderer(this, 12, 0);
			bar.addBox(x, y, z, (int) (-x * 2F), 2, 2);
			crossbars[i * 4] = bar;
			
			ModelRenderer bar2 = new ModelRenderer(this, 12, 0);
			bar2.addBox(x, y, -z - 2F, (int) (-x * 2F), 2, 2);
			crossbars[i * 4 + 1] = bar2;
			
			
			ModelRenderer bar3 = new ModelRenderer(this, 12, 0);
			bar3.addBox(z, y, x, 2, 2, (int) (-x * 2F));
			crossbars[i * 4 + 2] = bar3;
			
			
			ModelRenderer bar4 = new ModelRenderer(this, 12, 0);
			bar4.addBox(-z - 2F, y, x, 2, 2, (int) (-x * 2F));
			crossbars[i * 4 + 3] = bar4;
			
			progress += progressChg;
			
		}
		
		
		this.textureWidth = 256;
		this.textureHeight = 64;
		
		this.base = new ModelRenderer(this, 0, 0);
		this.base.addBox(-24F, -168F, -24F, 48, 4, 48);
		
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.bar1.render(f5);
		this.bar2.render(f5);
		this.bar3.render(f5);
		this.bar4.render(f5);
		this.base.render(f5);
		
		for (ModelRenderer bar : crossbars)
		{
			if (bar != null)
			{
				bar.render(f5);
			}
		}
	}
	
	public void renderBase(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.base.render(f5);
	}
	
	
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
