package flaxbeard.cyberware.common.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemSwordCyberware extends ItemSword implements IDeconstructable
{

	public ItemSwordCyberware(String name, ToolMaterial material)
	{
		super(material);
		
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
        
		this.setCreativeTab(Cyberware.creativeTab);
				
        CyberwareContent.items.add(this);
	}

	@Override
	public boolean canDestroy(ItemStack stack)
	{
		return true;
	}

	@Override
	public ItemStack[] getComponents(ItemStack stack)
	{
		return new ItemStack[]
				{
					new ItemStack(Items.IRON_INGOT, 2, 0),
					new ItemStack(CyberwareContent.component, 1, 2),
					new ItemStack(CyberwareContent.component, 1, 4)
				};
	}

}
