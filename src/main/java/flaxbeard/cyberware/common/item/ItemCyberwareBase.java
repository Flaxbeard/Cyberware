package flaxbeard.cyberware.common.item;

import java.util.List;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberwareTabItem;
import flaxbeard.cyberware.common.CyberwareContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemCyberwareBase extends Item
{
	public String[] subnames;

	public ItemCyberwareBase(String name, String... subnames)
	{
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
        
		this.setCreativeTab(Cyberware.creativeTab);
				
		this.subnames = subnames;

		this.setHasSubtypes(this.subnames.length > 0);
		this.setMaxDamage(0);

        CyberwareContent.items.add(this);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		int damage = itemstack.getItemDamage();
		if (damage >= subnames.length)
		{
			return super.getUnlocalizedName();
		}
		return super.getUnlocalizedName(itemstack) + "." + subnames[damage];
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if (subnames.length == 0)
		{
			list.add(new ItemStack(this));
		}
		for (int i = 0; i < subnames.length; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

}
