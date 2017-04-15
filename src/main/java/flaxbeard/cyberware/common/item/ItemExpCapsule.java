package flaxbeard.cyberware.common.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemExpCapsule extends Item
{
	public ItemExpCapsule(String name)
	{
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
		
		this.setCreativeTab(Cyberware.creativeTab);
				
		this.setMaxDamage(0);
		this.setMaxStackSize(1);

		CyberwareContent.items.add(this);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		ItemStack stack = new ItemStack(this);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("xp", 100);
		stack.setTagCompound(compound);
		list.add(stack);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		if (!playerIn.capabilities.isCreativeMode)
		{
			--stack.stackSize;
		}
		
		int xp = 0;
		if (stack.hasTagCompound())
		{
			NBTTagCompound c = stack.getTagCompound();
			if (c.hasKey("xp"))
			{
				xp = c.getInteger("xp");
			}
		}
		
		playerIn.addExperience(xp);
		
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		int xp = 0;
		if (stack.hasTagCompound())
		{
			NBTTagCompound c = stack.getTagCompound();
			if (c.hasKey("xp"))
			{
				xp = c.getInteger("xp");
			}
		}
		String before = I18n.format("cyberware.tooltip.expCapsule.before");
		if (before.length() > 0) before = before += " ";
		
		String after = I18n.format("cyberware.tooltip.expCapsule.after");
		if (after.length() > 0) after = " " + after;
		
		tooltip.add(ChatFormatting.RED + before + xp + after);
	}
}
