package flaxbeard.cyberware.common.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IBlueprint;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemBlueprint extends Item implements IBlueprint
{
	public ItemBlueprint(String name)
	{
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
		
		this.setCreativeTab(Cyberware.creativeTab);
				
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);

		CyberwareContent.items.add(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound comp = stack.getTagCompound();
			if (comp.hasKey("blueprintItem"))
			{
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				if (settings.isKeyDown(settings.keyBindSneak))
				{
					ItemStack blueprintItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("blueprintItem"));
					if (blueprintItem != null && CyberwareAPI.canDeconstruct(blueprintItem))
					{
						ItemStack[] items = CyberwareAPI.getComponents(blueprintItem).clone();
						tooltip.add(I18n.format("cyberware.tooltip.blueprint", blueprintItem.getDisplayName()));
						for (ItemStack item : items)
						{
							if (item != null)
							{
								tooltip.add(item.stackSize + " x " + item.getDisplayName());
							}
						}
						return;
					}
				}
				else
				{
					tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.shiftPrompt", Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName()));
					return;
				}
			}
		}
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.craftBlueprint"));
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{

		list.add(new ItemStack(this, 1, 1));
	}
	
	public static ItemStack getBlueprintForItem(ItemStack stack)
	{
		if (stack != null && CyberwareAPI.canDeconstruct(stack))
		{
			ItemStack toBlue = stack.copy();
			

			toBlue.stackSize = 1;
			if (toBlue.isItemStackDamageable())
			{
				toBlue.setItemDamage(0);
			}
			toBlue.setTagCompound(null);
			
			ItemStack ret = new ItemStack(CyberwareContent.blueprint);
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound itemTag = new NBTTagCompound();
			toBlue.writeToNBT(itemTag);
			tag.setTag("blueprintItem", itemTag);
			
			ret.setTagCompound(tag);
			return ret;
		}
		else
		{
			return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound comp = stack.getTagCompound();
			if (comp.hasKey("blueprintItem"))
			{
				ItemStack blueprintItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("blueprintItem"));
				if (blueprintItem != null)
				{
					return I18n.format("item.cyberware.blueprint.notBlank.name", blueprintItem.getDisplayName()).trim();
				}
			}
		}
		return ("" + I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	@Override
	public ItemStack getResult(ItemStack stack, ItemStack[] craftingItems)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound comp = stack.getTagCompound();
			if (comp.hasKey("blueprintItem"))
			{
				ItemStack blueprintItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("blueprintItem"));
				if (blueprintItem != null && CyberwareAPI.canDeconstruct(blueprintItem))
				{
					ItemStack[] requiredItems = CyberwareAPI.getComponents(blueprintItem).clone();
					for (int i = 0; i < requiredItems.length; i++)
					{
						ItemStack required = requiredItems[i].copy();
						boolean satisfied = false;
						for (ItemStack crafting : craftingItems)
						{
							if (crafting != null && required != null)
							{
								if (crafting.getItem() == required.getItem() && crafting.getItemDamage() == required.getItemDamage() && (!required.hasTagCompound() || (ItemStack.areItemStackTagsEqual(required, crafting))))
								{
									required.stackSize -= crafting.stackSize;
								}
								if (required.stackSize <= 0)
								{
									satisfied = true;
									break;
								}
							}
						}
						if (!satisfied) return null;
					}
					
					return blueprintItem;
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack[] consumeItems(ItemStack stack, ItemStack[] craftingItems)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound comp = stack.getTagCompound();
			if (comp.hasKey("blueprintItem"))
			{
				ItemStack blueprintItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("blueprintItem"));
				if (blueprintItem != null && CyberwareAPI.canDeconstruct(blueprintItem))
				{
					ItemStack[] requiredItems = CyberwareAPI.getComponents(blueprintItem).clone();
					ItemStack[] newCrafting = new ItemStack[6];
					for (int c = 0; c < craftingItems.length; c++)
					{
						newCrafting[c] = craftingItems[c];
					}
					for (int i = 0; i < requiredItems.length; i++)
					{
						ItemStack required = requiredItems[i].copy();
						boolean satisfied = false;
						for (int c = 0; c < newCrafting.length; c++)
						{
							ItemStack crafting = newCrafting[c];
							if (crafting != null && required != null)
							{
								if (crafting.getItem() == required.getItem() && crafting.getItemDamage() == required.getItemDamage() && (!required.hasTagCompound() || (ItemStack.areItemStackTagsEqual(required, crafting))))
								{
									int toSubtract = Math.min(required.stackSize, crafting.stackSize);
									required.stackSize -= toSubtract;
									crafting.stackSize -= toSubtract;
									if (crafting.stackSize <= 0)
									{
										crafting = null;
									}
									newCrafting[c] = crafting;
								}
								if (required.stackSize <= 0)
								{
									break;
								}
							}
						}
					}
					
					return newCrafting;
				}
			}
		}
		throw new IllegalStateException("Consuming items when items shouldn't be consumed!");
	}
	
	@Override
	public ItemStack[] getRequirementsForDisplay(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound comp = stack.getTagCompound();
			if (comp.hasKey("blueprintItem"))
			{
				ItemStack blueprintItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("blueprintItem"));
				if (blueprintItem != null && CyberwareAPI.canDeconstruct(blueprintItem))
				{
					return CyberwareAPI.getComponents(blueprintItem).clone();
				}
			}
		}
		return null;
	}
}
