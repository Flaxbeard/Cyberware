package flaxbeard.cyberware.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.DodgePacket;

public class ItemBrainUpgrade extends ItemCyberware implements IMenuItem
{

	public ItemBrainUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

	}

	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == this && stack.getItemDamage() == 0 && other.getItemDamage() == 2;
	}
	
	@SubscribeEvent
	public void handleTeleJam(EnderTeleportEvent event)
	{
		EntityLivingBase te = event.getEntityLiving();
		
		if (CyberwareAPI.isCyberwareInstalled(te, new ItemStack(this, 1, 1)))
		{
			event.setCanceled(true);
			return;
		}
		if (te != null)
		{
			float range = 25F;
			List<EntityLivingBase> test = te.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(te.posX - range, te.posY - range, te.posZ - range, te.posX + te.width + range, te.posY + te.height + range, te.posZ + te.width + range));
			for (EntityLivingBase e : test)
			{
				if (te.getDistanceToEntity(e) <= range)
				{
					if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
					{
						event.setCanceled(true);
						return;
					}
				}
			}
		}
		
	}

	@SubscribeEvent
	public void handleClone(PlayerEvent.Clone event)
	{
		if (event.isWasDeath())
		{
			EntityPlayer p = event.getOriginal();
			
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0)) && !p.worldObj.getGameRules().getBoolean("keepInventory"))
			{
				/*float range = 5F;
				List<EntityXPOrb> orbs = p.worldObj.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(p.posX - range, p.posY - range, p.posZ - range, p.posX + p.width + range, p.posY + p.height + range, p.posZ + p.width + range));
				for (EntityXPOrb orb : orbs)
				{
					orb.setDead();
				}*/

				if (!p.worldObj.isRemote)
				{
					ItemStack stack = new ItemStack(CyberwareContent.expCapsule);
					NBTTagCompound c = new NBTTagCompound();
					c.setInteger("xp", p.experienceTotal);
					stack.setTagCompound(c);
					EntityItem item = new EntityItem(p.worldObj, p.posX, p.posY, p.posZ, stack);
					p.worldObj.spawnEntityInWorld(item);
				}
			}
			else if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 2)) && !p.worldObj.getGameRules().getBoolean("keepInventory"))
			{
				event.getEntityPlayer().addExperience((int) (Math.min(100, p.experienceLevel * 7) * .9F));
			}
		}
	}
	
	@SubscribeEvent
	public void handleMining(BreakSpeed event)
	{
		EntityPlayer p = event.getEntityPlayer();
		
		ItemStack test = new ItemStack(this, 1, 3);
		if (CyberwareAPI.isCyberwareInstalled(p, test) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(p, test)) && isContextWorking(p) && !p.isSneaking())
		{
			IBlockState state = event.getState();
			ItemStack tool = p.getHeldItem(EnumHand.MAIN_HAND);
			
			if (tool != null && (tool.getItem() instanceof ItemSword || tool.getItem().getUnlocalizedName().contains("sword"))) return;
			
			if (isToolEffective(tool, state)) return;
			
			for (int i = 0; i < 10; i++)
			{
				if (i != p.inventory.currentItem)
				{
					ItemStack potentialTool = p.inventory.mainInventory[i];
					if (isToolEffective(potentialTool, state))
					{
						p.inventory.currentItem = i;
						return;
					}
				}
			}
		}
	}
	
	private static Map<Integer, Boolean> isContextWorking = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> isMatrixWorking = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> isRadioWorking = new HashMap<Integer, Boolean>();

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 3);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)))
		{
			isContextWorking.put(e.getEntityId(), CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)));
		}
		
		test = new ItemStack(this, 1, 4);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			isMatrixWorking.put(e.getEntityId(), CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)));
		}
		
		test = new ItemStack(this, 1, 5);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)))
		{
			isRadioWorking.put(e.getEntityId(), CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)));
		}
		
	}
	
	private boolean isRadioWorking(EntityLivingBase e)
	{
		if (!isRadioWorking.containsKey(e.getEntityId()))
		{
			isRadioWorking.put(e.getEntityId(), false);
		}
		
		return isRadioWorking.get(e.getEntityId());
	}
	
	private boolean isContextWorking(EntityLivingBase e)
	{
		if (!isContextWorking.containsKey(e.getEntityId()))
		{
			isContextWorking.put(e.getEntityId(), false);
		}
		
		return isContextWorking.get(e.getEntityId());
	}
	
	private boolean isMatrixWorking(EntityLivingBase e)
	{
		if (!isMatrixWorking.containsKey(e.getEntityId()))
		{
			isMatrixWorking.put(e.getEntityId(), false);
		}
		
		return isMatrixWorking.get(e.getEntityId());
	}
	
	public boolean isToolEffective(ItemStack tool, IBlockState state)
	{
		if (tool != null)
		{
			for (String toolType : tool.getItem().getToolClasses(tool))
			{
				if (state.getBlock().isToolEffective(toolType, state))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	@SubscribeEvent
	public void handleXPDrop(LivingExperienceDropEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)) || CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{
			event.setCanceled(true);
		}
	}
	
	private static ArrayList<String> lastHits = new ArrayList<String>();

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void handleHurt(LivingAttackEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 4)) && isMatrixWorking(e))
		{

			if (!e.worldObj.isRemote && event.getSource() instanceof EntityDamageSource)
			{
				Entity attacker = ((EntityDamageSource) event.getSource()).getSourceOfDamage();
				if (e instanceof EntityPlayer)
				{
					String str = e.getEntityId() + " " + e.ticksExisted + " " + attacker.getEntityId();
					if (lastHits.contains(str))
					{
						return;
					}
					else
					{
						lastHits.add(str);
					}
				}
				
				boolean armor = false;
				for (ItemStack stack : e.getArmorInventoryList())
				{
					if (stack != null && stack.getItem() instanceof ItemArmor)
					{
						if (((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(EntityEquipmentSlot.CHEST) > 4)
						{
							return;
						}
					}
					else if (stack != null && stack.getItem() instanceof ISpecialArmor)
					{
						if (((ISpecialArmor) stack.getItem()).getProperties(e, stack, event.getSource(), event.getAmount(), 1).AbsorbRatio * 25D > 4)
						{
							return;
						}
					}
					
					if (stack != null)
					{
						armor = true;
					}
					
				}
				

				if (!((float) e.hurtResistantTime > (float) e.maxHurtResistantTime / 2.0F))
                {
					Random random = e.getRNG();
					if (random.nextFloat() < (armor ? LibConstants.DODGE_ARMOR : LibConstants.DODGE_NO_ARMOR))
					{
						event.setCanceled(true);
						e.hurtResistantTime = e.maxHurtResistantTime;
						e.hurtTime = e.maxHurtTime = 10;
						ReflectionHelper.setPrivateValue(EntityLivingBase.class, e, 9999F, 46);
						
						CyberwarePacketHandler.INSTANCE.sendToAllAround(new DodgePacket(e.getEntityId()), new TargetPoint(e.worldObj.provider.getDimension(), e.posX, e.posY, e.posZ, 50));
					}
				}
			}
		}
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 3 ? LibConstants.CONTEXTUALIZER_CONSUMPTION :
			 stack.getItemDamage() == 4 ? LibConstants.MATRIX_CONSUMPTION :
			 stack.getItemDamage() == 5 ? LibConstants.RADIO_CONSUMPTION: 0;
	}


	@Override
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 3 || stack.getItemDamage() == 5;
	}


	@Override
	public void use(Entity e, ItemStack stack)
	{
		EnableDisableHelper.toggle(stack);
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
