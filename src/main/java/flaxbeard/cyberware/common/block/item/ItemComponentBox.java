package flaxbeard.cyberware.common.block.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemComponentBox extends ItemBlockCyberware
{

	public ItemComponentBox(Block block)
	{
		super(block);
		this.setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		playerIn.openGui(Cyberware.INSTANCE, 6, worldIn, 0, 0, 0);
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (playerIn.isSneaking())
		{
			EnumActionResult res = super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			if (res == EnumActionResult.SUCCESS && playerIn.isCreative())
			{
				playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
			}
			return res;
		}
		else
		{
			playerIn.openGui(Cyberware.INSTANCE, 6, worldIn, 0, 0, 0);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.componentBox"));
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.componentBox2"));
	}
	
}
