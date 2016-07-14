package flaxbeard.cyberware.common.block.item;

import flaxbeard.cyberware.api.ICyberwareTabItem;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCyberware extends ItemBlock implements ICyberwareTabItem
{
	public ItemBlockCyberware(Block block)
	{
		super(block);
	}

	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.BLOCKS;
	}

}
