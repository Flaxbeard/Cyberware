package flaxbeard.cyberware.common.block.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.item.ICyberwareTabItem;

public class ItemBlockCyberware extends ItemBlock implements ICyberwareTabItem
{
	private String[] tt;
	public int[] metaMap = new int[] { 0 };
	
	public ItemBlockCyberware(Block block)
	{
		super(block);
	}
	
	public ItemBlockCyberware(Block block, String... tooltip)
	{
		super(block);
		this.tt = tooltip;
	}

	public ItemBlockCyberware(Block block, int[] metaMap)
	{
		super(block);
		this.metaMap = metaMap;
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
	
	@Override
	public int getMetadata(int damage)
	{
		return metaMap[Math.min(metaMap.length - 1, damage)];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		if (itemIn == this)
		{
			for (int i = 0; i < metaMap.length; i++)
			{
				subItems.add(new ItemStack(itemIn, 1, metaMap[i]));
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack)
	{
		if (metaMap.length == 1 && metaMap[0] == 0)
		{
			return super.getUnlocalizedName(stack);
		}
		
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}
}
