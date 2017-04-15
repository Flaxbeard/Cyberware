package flaxbeard.cyberware.common.integration.am2;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;

public class ModelPegLeg extends ModelPlayer
{

	public ModelPegLeg()
	{
		super(0F, false);
		this.bipedLeftLeg = new ModelRenderer(this, 0, 0);
		this.bipedLeftLeg.addBox(0.0F, 0.0F, -2.0F, 4, 4, 4, 0);
		ModelRenderer peg = new ModelRenderer(this, 0, 8);
		peg.addBox(1.0F, 4.0F, -1.0F, 2, 8, 2, 0);
		this.bipedLeftLeg.addChild(peg);
	}

}
