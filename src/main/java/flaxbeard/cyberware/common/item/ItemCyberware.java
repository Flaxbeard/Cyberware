package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.ICyberware;
import flaxbeard.cyberware.api.ICyberwareTabItem;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;

public class ItemCyberware extends Item implements ICyberware, ICyberwareTabItem
{
	private EnumSlot[] slots;
	public String[] subnames;
	private int[] essence;
	
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
		this.essence = new int[subnames.length + 1];

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
	
	public ItemCyberware setWeights(int... weight)
	{
		for (int meta = 0; meta < weight.length; meta++)
		{
			ItemStack stack = new ItemStack(this, 1, meta);
			int installedStackSize = installedStackSize(stack);
			stack.stackSize = installedStackSize;
			CyberwareContent.zombieItems.add(new ZombieItem(weight[meta], stack));
		}
		return this;
	}
	
	public ItemCyberware setEssenceCost(int... essence)
	{
		this.essence = essence;
		return this;
	}
	
	@Override
	public int getEssenceCost(ItemStack stack)
	{
		return essence[Math.min(this.subnames.length, stack.getItemDamage())];
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
		List<String> desc = this.getDesciption(stack);
		if (desc != null && desc.size() > 0)
		{

			ret.addAll(desc);
			
		}
		return ret;
	}
	
	public List<String> getStackDesc(ItemStack stack)
	{
		String[] toReturnArray = I18n.format("cyberware.tooltip." + this.getRegistryName().toString().substring(10)
				+ (this.subnames.length > 0 ? "." + stack.getItemDamage() : "")).split("\\\\n");
		List<String> toReturn = new ArrayList<String>(Arrays.asList(toReturnArray));
		
		if (toReturn.size() > 0 && toReturn.get(0).length() == 0)
		{
			toReturn.remove(0);
		}
		
		return toReturn;
	}

	public List<String> getDesciption(ItemStack stack)
	{
		List<String> toReturn = getStackDesc(stack);
		
		if (installedStackSize(stack) > 1)
		{
			toReturn.add(ChatFormatting.BLUE + I18n.format("cyberware.tooltip.maxInstall", installedStackSize(stack)));
		}
		
		boolean hasPowerConsumption = false;
		String toAddPowerConsumption = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.stackSize = i + 1;
			int cost = this.getPowerConsumption(temp);
			if (cost > 0)
			{
				hasPowerConsumption = true;
			}
			
			if (i != 0)
			{
				toAddPowerConsumption += I18n.format("cyberware.tooltip.joiner");
			}
			
			toAddPowerConsumption += " " + cost;
		}
		
		if (hasPowerConsumption)
		{
			String toTranslate = hasCustomPowerMessage(stack) ? 
					"cyberware.tooltip." + this.getRegistryName().toString().substring(10)
					+ (this.subnames.length > 0 ? "." + stack.getItemDamage() : "") + ".powerConsumption"
					:
					"cyberware.tooltip.powerConsumption";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, toAddPowerConsumption));
		}
		
		boolean hasPowerProduction = false;
		String toAddPowerProduction = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.stackSize = i + 1;
			int cost = this.getPowerProduction(temp);
			if (cost > 0)
			{
				hasPowerProduction = true;
			}
			
			if (i != 0)
			{
				toAddPowerProduction += I18n.format("cyberware.tooltip.joiner");
			}
			
			toAddPowerProduction += " " + cost;
		}
		
		if (hasPowerProduction)
		{
			String toTranslate = hasCustomPowerMessage(stack) ? 
					"cyberware.tooltip." + this.getRegistryName().toString().substring(10)
					+ (this.subnames.length > 0 ? "." + stack.getItemDamage() : "") + ".powerProduction"
					:
					"cyberware.tooltip.powerProduction";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, toAddPowerProduction));
		}
		
		if (getCapacity(stack) > 0)
		{
			String toTranslate = hasCustomCapacityMessage(stack) ? 
					"cyberware.tooltip." + this.getRegistryName().toString().substring(10)
					+ (this.subnames.length > 0 ? "." + stack.getItemDamage() : "") + ".capacity"
					:
					"cyberware.tooltip.capacity";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, getCapacity(stack)));
		}
		
		
		boolean hasEssenceCost = false;
		String toAddEssence = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.stackSize = i + 1;
			int cost = this.getEssenceCost(temp);
			if (cost > 0)
			{
				hasEssenceCost = true;
			}
			
			if (i != 0)
			{
				toAddEssence += I18n.format("cyberware.tooltip.joiner");
			}
			
			toAddEssence += " " + cost;
		}
		
		if (hasEssenceCost)
		{
			toReturn.add(ChatFormatting.DARK_PURPLE + I18n.format("cyberware.tooltip.essence", toAddEssence));
		}
		

		
		
		return toReturn;
	}
	
	public int getPowerConsumption(ItemStack stack)
	{
		return 0;
	}
	
	public int getPowerProduction(ItemStack stack)
	{
		return 0;
	}
	
	public boolean hasCustomPowerMessage(ItemStack stack)
	{
		return false;
	}
	
	public boolean hasCustomCapacityMessage(ItemStack stack)
	{
		return false;
	}

	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		return new ItemStack[0][0];
	}

	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.values()[this.getSlot(stack).ordinal() + 2];
	}

	@Override
	public int getCapacity(ItemStack wareStack)
	{
		return 0;
	}

	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack) {}

	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack) {}
	
}
