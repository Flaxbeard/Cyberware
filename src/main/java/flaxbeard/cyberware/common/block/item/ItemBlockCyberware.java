package flaxbeard.cyberware.common.block.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.item.ICyberwareTabItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockCyberware extends ItemBlock implements ICyberwareTabItem
{
	private String[] tt;
	
	public ItemBlockCyberware(Block block)
	{
		super(block);
	}
	
	public ItemBlockCyberware(Block block, String... tooltip)
	{
		super(block);
		this.tt = tooltip;
	}

	@Override
	public EnumCategory getCategory(ItemStack stack)
	{
		return EnumCategory.BLOCKS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if (this.tt != null)
		{
			for (String str : tt)
			{
				tooltip.add(ChatFormatting.DARK_GRAY + I18n.format(str));
			}
		}
	}
}
