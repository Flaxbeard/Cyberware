package flaxbeard.cyberware.common.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemNeuropozyne extends Item
{
	public ItemNeuropozyne(String name)
	{
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
		
		this.setCreativeTab(Cyberware.creativeTab);
				
		this.setMaxDamage(0);

		CyberwareContent.items.add(this);
	}
	
	
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		if (!playerIn.capabilities.isCreativeMode)
		{
			--stack.stackSize;
		}
		
		playerIn.addPotionEffect(new PotionEffect(CyberwareContent.neuropozyneEffect, 24000, 0, false, false));

		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{

		String neuropozyne = I18n.format("cyberware.tooltip.neuropozyne");

		tooltip.add(ChatFormatting.BLUE + neuropozyne);
	}
}
