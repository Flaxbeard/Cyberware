package flaxbeard.cyberware.common.integration.roots;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import elucent.roots.capability.mana.IManaCapability;
import elucent.roots.capability.mana.ManaProvider;
import elucent.roots.event.SpellCastEvent;
import elucent.roots.item.ItemCastingBase;
import elucent.roots.item.ItemMeleeCastingBase;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class ItemRootsUpgrade extends ItemCyberware
{

	public ItemRootsUpgrade(String name, EnumSlot[] slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private boolean hasCapability(EntityLivingBase entity)
	{
		return entity.hasCapability(ManaProvider.manaCapability, null);
	}
	
	private IManaCapability getCapability(EntityLivingBase entity)
	{
		return entity.getCapability(ManaProvider.manaCapability, null);
	}
	
	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0 && hasCapability(entity))
		{
			IManaCapability mc = getCapability(entity);
			mc.setMaxMana(mc.getMaxMana() + 40);
		}
	}
	
	
	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0 && hasCapability(entity))
		{
			IManaCapability mc = getCapability(entity);
			mc.setMaxMana(mc.getMaxMana() - 40);
		}
	}
	
	@SubscribeEvent
	public void handleTerraHurt(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (e instanceof EntityPlayer && CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{
			if (hasCapability(e))
			{
				EntityPlayer player = (EntityPlayer) e;
				IManaCapability mc = getCapability(player);
				System.out.println(event.getAmount());

				float result = mc.getMana() - (event.getAmount() * 4);
				result = Math.max(0, result);
				mc.setMana(player, result);
			
			}
		}
	}
	
	@SubscribeEvent
	public void handleTerraRegen(CyberwareUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (e instanceof EntityPlayer && e.ticksExisted % 5 == 0)
		{
			if (hasCapability(e))
			{
				EntityPlayer player = (EntityPlayer) e;
				IManaCapability mc = getCapability(player);
				
				if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)))
				{
					if (mc.getMana() != mc.getMaxMana())
					{
						mc.setMana(player, mc.getMana() - 0.4f);
					}
				}
				if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
				{
					mc.setMana(player, mc.getMana() + .2f);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleSpellcast(SpellCastEvent event)
	{
		EntityPlayer p = event.getPlayer();
		if (CyberwareAPI.hasCapability(p) && CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 1)))
		{
			int dura1 = p.getHeldItemMainhand() == null ? 0 : p.getHeldItemMainhand().getMetadata();
			int dura2 = p.getHeldItemOffhand() == null ? 0 : p.getHeldItemOffhand().getMetadata();
			Random random = new Random(dura1 + dura2 + p.getUniqueID().getMostSignificantBits() + ((int) ManaProvider.get(p).getMana()));
			
			if (random.nextFloat() < .15F)
			{
				System.out.println(p.worldObj.rand.nextFloat());
				event.setCanceled(true);
				ItemStack stack = p.getHeldItemMainhand() != null && (p.getHeldItemMainhand().getItem() instanceof ItemCastingBase || p.getHeldItemMainhand().getItem() instanceof ItemMeleeCastingBase) ? p.getHeldItemMainhand() : p.getHeldItemOffhand();
				EnumHand hand = p.getHeldItemMainhand() != null && (p.getHeldItemMainhand().getItem() instanceof ItemCastingBase || p.getHeldItemMainhand().getItem() instanceof ItemMeleeCastingBase) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
				ItemCastingBase.decrementUses(stack, p, hand);
			}
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 2)))
			{
				event.setEfficiency(event.getEfficiency() + 1);
			}
		}
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		if (stack.getItemDamage() <= 1)
		{
			if (other.getItem() == stack.getItem() && other.getItemDamage() <= 1)
			{
				return false;
			}
			if (other.getItem() == CyberwareContent.cyberheart)
			{
				return false;
			}
			
		}
		return false;
	}
	
}
