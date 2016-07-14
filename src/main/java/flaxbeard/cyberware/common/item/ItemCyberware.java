package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.List;

import scala.actors.threadpool.Arrays;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.ICyberware;
import flaxbeard.cyberware.api.ICyberwareTabItem;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemCyberware extends Item implements ICyberware, ICyberwareTabItem
{
	private EnumSlot[] slots;
	public String[] subnames;
	
	public ItemCyberware(String name, EnumSlot[] slots, String[] subnames)
	{		
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
        
		this.setCreativeTab(Cyberware.creativeTab);
		
		this.slots = slots;
		
		this.subnames = subnames;

		this.setHasSubtypes(this.subnames.length > 0);
		this.setMaxDamage(0);

        CyberwareContent.items.add(this);
	}
	
	public ItemCyberware(String name, EnumSlot slot, String[] subnames)
	{		
		this(name, new EnumSlot[] { slot }, subnames);
	}
	
	public ItemCyberware(String name, EnumSlot slot)
	{
		this(name, slot, new String[0]);
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
	public EnumSlot getSlot(ItemStack stack)
	{
		return slots[Math.min(slots.length - 1, stack.getItemDamage())];
	}

	@Override
	public int installedStackSize(ItemStack stack)
	{
		return 1;
	}

	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return false;
	}
	
	@Override
	public boolean isEssential(ItemStack stack)
	{
		return false;		
	}

	@Override
	public List<String> getInfo(ItemStack stack)
	{
		List<String> ret = new ArrayList<String>();
		String[] desc = this.getDesciption(stack);
		if (desc != null && desc.length > 0)
		{
			String format = desc[0];
			if (format.length() > 0)
			{
				ret.addAll(Arrays.asList(desc));
			}
		}
		return ret;
	}

	public String[] getDesciption(ItemStack stack)
	{
		return I18n.format("cyberware.tooltip." + this.getRegistryName().toString().substring(10)
				+ (this.subnames.length > 0 ? "." + stack.getItemDamage() : "")).split("\\\\n");
	}

	@Override
	public ItemStack[] required(ItemStack stack)
	{
		return new ItemStack[0];
	}

	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.values()[this.getSlot(stack).ordinal() + 2];
	}
	
}
