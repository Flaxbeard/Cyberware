package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.HashMultimap;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.GuiPacket;

public class ItemHandUpgrade extends ItemCyberware implements IMenuItem
{

	public ItemHandUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		return new ItemStack[][] { 
				new ItemStack[] { new ItemStack(CyberwareContent.cyberlimbs, 1, 0), new ItemStack(CyberwareContent.cyberlimbs, 1, 1) }};
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleOpenInv(GuiOpenEvent event)
	{
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null && event.getGui() != null && event.getGui().getClass() == GuiInventory.class && !Minecraft.getMinecraft().thePlayer.isCreative())
		{
			if (CyberwareAPI.isCyberwareInstalled(Minecraft.getMinecraft().thePlayer, new ItemStack(this, 1, 0)))
			{
				event.setCanceled(true);
	
				Minecraft.getMinecraft().thePlayer.openGui(Cyberware.INSTANCE, 1, Minecraft.getMinecraft().thePlayer.worldObj, 0, 0, 0);
				CyberwarePacketHandler.INSTANCE.sendToServer(new GuiPacket(1, 0, 0, 0));
			}
		}
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == this;
	}
	
	private Map<Integer, Boolean> lastClaws = new HashMap<Integer, Boolean>();
	public static float clawsTime;

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
		{
			boolean last = getLastClaws(e);
			boolean isEquipped = e.getHeldItemMainhand() == null && 
					(e.getPrimaryHand() == EnumHandSide.RIGHT ? 
							(CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 1))) : 
							(CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 0))));
			if (isEquipped && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)))
			{
				this.addUnarmedDamage(e, test);
				lastClaws.put(e.getEntityId(), true);

				if (!last)
				{
					if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
					{
						updateHand(e, true);
					}
				}
			}
			else
			{
				this.removeUnarmedDamage(e, test);
				lastClaws.put(e.getEntityId(), false);
			}
			
		}
		else
		{
			
			lastClaws.put(e.getEntityId(), false);
		}
	}
	
	private void updateHand(EntityLivingBase e, boolean delay)
	{
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null)
		{
			if (e == Minecraft.getMinecraft().thePlayer)
			{
				clawsTime = Minecraft.getMinecraft().getRenderPartialTicks() + e.ticksExisted + (delay ? 5 : 0);
			}
		}
	}
	
	private boolean getLastClaws(EntityLivingBase e)
	{
		if (!lastClaws.containsKey(e.getEntityId()))
		{
			lastClaws.put(e.getEntityId(), false);
		}
		return lastClaws.get(e.getEntityId());
	}
	
	
	public void addUnarmedDamage(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 1)
		{
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(strengthId, "Claws damage upgrade", 5.5F, 0));
			entity.getAttributeMap().applyAttributeModifiers(multimap);
		}
	}
	
	public void removeUnarmedDamage(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 1)
		{
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(strengthId, "Claws Claws upgrade", 5.5F, 0));
			entity.getAttributeMap().removeAttributeModifiers(multimap);
		}
	}
	
	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 1)
		{
			removeUnarmedDamage(entity, stack);
		}
	}
	
	@SubscribeEvent
	public void handleMining(HarvestCheck event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack test = new ItemStack(this, 1, 2);
		boolean rightArm = (p.getPrimaryHand() == EnumHandSide.RIGHT ? 
				(CyberwareAPI.isCyberwareInstalled(p, new ItemStack(CyberwareContent.cyberlimbs, 1, 1))) : 
				(CyberwareAPI.isCyberwareInstalled(p, new ItemStack(CyberwareContent.cyberlimbs, 1, 0))));
		if (rightArm && CyberwareAPI.isCyberwareInstalled(p, test) && p.getHeldItemMainhand() == null)
		{
			ItemStack pick = new ItemStack(Items.STONE_PICKAXE);
			if (pick.canHarvestBlock(event.getTargetBlock()))
			{
				event.setCanHarvest(true);
			}
		}
	}
	
	@SubscribeEvent
	public void handleMineSpeed(BreakSpeed event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack test = new ItemStack(this, 1, 2);
		boolean rightArm = (p.getPrimaryHand() == EnumHandSide.RIGHT ? 
						(CyberwareAPI.isCyberwareInstalled(p, new ItemStack(CyberwareContent.cyberlimbs, 1, 1))) : 
						(CyberwareAPI.isCyberwareInstalled(p, new ItemStack(CyberwareContent.cyberlimbs, 1, 0))));
		if (rightArm && CyberwareAPI.isCyberwareInstalled(p, test) && p.getHeldItemMainhand() == null)
		{
			ItemStack pick = new ItemStack(Items.STONE_PICKAXE);
			event.setNewSpeed(event.getNewSpeed() * pick.getStrVsBlock(p.worldObj.getBlockState(event.getPos())));
		}
	}
	
	private static final UUID strengthId = UUID.fromString("63c32801-94fb-40d4-8bd2-89135c1e44b1");

	@Override
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 1;
	}

	@Override
	public void use(Entity e, ItemStack stack)
	{
		EnableDisableHelper.toggle(stack);
		if (e instanceof EntityLivingBase && FMLCommonHandler.instance().getSide() == Side.CLIENT)
		{
			updateHand((EntityLivingBase) e, false);
		}
	}

	@Override
	public String getUnlocalizedLabel(ItemStack stack)
	{
		return EnableDisableHelper.getUnlocalizedLabel(stack);
	}

	private static final float[] f = new float[] { 1F, 0F, 0F };
	
	@Override
	public float[] getColor(ItemStack stack)
	{
		return EnableDisableHelper.isEnabled(stack) ? f : null;
	}
}
