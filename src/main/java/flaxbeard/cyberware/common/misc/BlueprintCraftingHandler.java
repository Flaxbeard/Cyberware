package flaxbeard.cyberware.common.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemBlueprint;

public class BlueprintCraftingHandler implements IRecipe
{
	static
	{
		RecipeSorter.register(Cyberware.MODID + ":blueprintCrafting", BlueprintCraftingHandler.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		return new BlueprintResult(inv).canCraft;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		return new BlueprintResult(inv).output;
	}

	@Override
	public int getRecipeSize()
	{
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
	{
		return new BlueprintResult(inv).remaining;
	}
	
	private class BlueprintResult
	{
		private final boolean canCraft;
		private final ItemStack[] remaining;
		private final ItemStack output;
		
		private ItemStack ware;
		int wareStack = 0;

		public BlueprintResult(InventoryCrafting inv)
		{
			this.ware = null;
			this.canCraft = process(inv);
			if (canCraft)
			{
				remaining = new ItemStack[9];
				remaining[wareStack] = ItemStack.copyItemStack(ware);
				output = ItemBlueprint.getBlueprintForItem(ware);
			}
			else
			{
				remaining = new ItemStack[9];
				output = null;
			}
		}
		
		private boolean process(InventoryCrafting inv)
		{
			boolean hasBlankBlueprint = false;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack stack = inv.getStackInSlot(i);
				if (stack != null)
				{
					if (stack.getItem() instanceof IDeconstructable)
					{
						if (ware == null)
						{
							ware = stack;
							wareStack = i;
						}
						else
						{
							return false;
						}
					}
					else if (stack.getItem() == CyberwareContent.blueprint
							&& (!stack.hasTagCompound()
							|| !stack.getTagCompound().hasKey("blueprintItem")))
					{
						if (!hasBlankBlueprint)
						{
							hasBlankBlueprint = true;
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}
					
				}
			}
			return ware != null && hasBlankBlueprint;
		}
	}

}
