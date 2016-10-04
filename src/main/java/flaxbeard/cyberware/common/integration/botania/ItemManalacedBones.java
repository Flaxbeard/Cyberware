package flaxbeard.cyberware.common.integration.botania;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaItemsEvent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemManalacedBones extends ItemCyberware implements IManaItem, IManaTooltipDisplay
{

	public ItemManalacedBones(String name, EnumSlot slot)
	{
		super(name, slot);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public float getManaFractionForDisplay(ItemStack stack)
	{
		return (float) getMana(stack) / (float) getMaxMana(stack);
	}

	@Override
	public int getMana(ItemStack stack)
	{
		if (!CyberwareAPI.getCyberwareNBT(stack).hasKey("mana"))
		{
			return 0;
		}
		return CyberwareAPI.getCyberwareNBT(stack).hasKey("on") ? CyberwareAPI.getCyberwareNBT(stack).getInteger("mana") : 0;
	}

	@Override
	public int getMaxMana(ItemStack stack)
	{
		return LibConstants.MANA_BONES_MAX_MANA;
	}

	@Override
	public void addMana(ItemStack stack, int mana)
	{
		setMana(stack, Math.min(getMaxMana(stack), getMana(stack) + mana));
	}
	
	private void setMana(ItemStack stack, int mana)
	{
		CyberwareAPI.getCyberwareNBT(stack).setInteger("mana", mana);
	}

	@Override
	public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool)
	{
		return false;
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack)
	{
		return CyberwareAPI.getCyberwareNBT(stack).hasKey("on");
	}

	@Override
	public boolean canExportManaToPool(ItemStack stack, TileEntity pool)
	{
		return false;
	}

	@Override
	public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack)
	{
		return CyberwareAPI.getCyberwareNBT(stack).hasKey("on");
	}

	@Override
	public boolean isNoExport(ItemStack stack)
	{
		return !CyberwareAPI.getCyberwareNBT(stack).hasKey("on");
	}
	
	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack)
	{
		CyberwareAPI.getCyberwareNBT(stack).setByte("on", (byte) 1);
	}

	@SubscribeEvent
	public void addToPool(ManaItemsEvent event)
	{
		EntityPlayer e = event.getEntityPlayer();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this)))
		{
			event.add(CyberwareAPI.getCyberware(e, new ItemStack(this)));
		}
	}
	
}
