package flaxbeard.cyberware.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;


public class ItemComponent extends ItemCyberwareBase
{

	public ItemComponent(String name1, String... names)
	{
		super(name1, names);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		int length = 10;
		

		
		for (int i = 0; i < length; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
		
		if (Loader.isModLoaded("botania") || Loader.isModLoaded("Botania"))
		{
			list.add(new ItemStack(this, 1, 10));
		}
		
		if (Loader.isModLoaded("roots"))
		{
			list.add(new ItemStack(this, 1, 11));
		}
	}

}
