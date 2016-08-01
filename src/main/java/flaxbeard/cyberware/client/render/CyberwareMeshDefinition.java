package flaxbeard.cyberware.client.render;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class CyberwareMeshDefinition implements ItemMeshDefinition
{

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		ItemStack test = ItemStack.copyItemStack(stack);
		if (test != null && test.hasTagCompound())
		{
			test.getTagCompound().removeTag(CyberwareAPI.QUALITY_TAG);
		}
		
		ItemCyberware ware = (ItemCyberware) stack.getItem();
		String added = "";
		if (ware.subnames.length > 0)
		{
			int i = Math.min(ware.subnames.length - 1, stack.getItemDamage());
			added = "_" + ware.subnames[i];
		}
		
		Quality q = CyberwareAPI.getCyberware(stack).getQuality(stack);

		if (q != null && CyberwareAPI.getCyberware(test).getQuality(test) != q && q.getSpriteSuffix() != null)
		{
			return new ModelResourceLocation(ware.getRegistryName() + added + "_" + q.getSpriteSuffix(), "inventory");
		}
		else
		{
			return new ModelResourceLocation(ware.getRegistryName() + added, "inventory");
		}
	}

}
