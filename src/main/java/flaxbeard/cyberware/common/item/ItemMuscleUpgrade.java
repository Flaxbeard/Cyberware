package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.SwitchHeldItemAndRotationPacket;

public class ItemMuscleUpgrade extends ItemCyberware implements IMenuItem
{

	public ItemMuscleUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	private static final UUID speedId = UUID.fromString("f0ab4766-4be1-11e6-beb8-9e71128cae77");
	private static final UUID strengthId = UUID.fromString("f63d6916-4be1-11e6-beb8-9e71128cae77");

	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			//System.out.println("ADDED0");
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Muscle speed upgrade", 1.5F, 0));
			entity.getAttributeMap().applyAttributeModifiers(multimap);
		}
		else if (stack.getItemDamage() == 1)
		{
			//System.out.println("ADDED1");
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(strengthId, "Muscle damage upgrade", 3F, 0));
			entity.getAttributeMap().applyAttributeModifiers(multimap);
		}
	}

	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			//System.out.println("REMOVED0");
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Muscle speed upgrade", 1.5F, 0));
			entity.getAttributeMap().removeAttributeModifiers(multimap);
		}
		else if (stack.getItemDamage() == 1)
		{
			//System.out.println("REMOVED1");
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(strengthId, "Muscle damage upgrade", 3F, 0));
			entity.getAttributeMap().removeAttributeModifiers(multimap);
		}
	}
	
	@Override
	public int installedStackSize(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? 3 : 1;
	}
	
	@SubscribeEvent
	public void handleHurt(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();

		ItemStack test = new ItemStack(this, 1, 0);
		int rank = CyberwareAPI.getCyberwareRank(e, test);
		if (!event.isCanceled() && e instanceof EntityPlayer && (rank > 1) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)) && getLastBoostSpeed(e))
		{
			EntityPlayer p = (EntityPlayer) e;
			if (event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof EntityDamageSourceIndirect))
			{
				EntityDamageSource source = (EntityDamageSource) event.getSource();
				Entity attacker = source.getEntity();
				int lastAttacked = ReflectionHelper.getPrivateValue(CombatTracker.class, p.getCombatTracker(), 2);
				
				if (p.ticksExisted - lastAttacked > 120)
				{
					ItemStack weapon = p.getHeldItemMainhand();
					int loc = -1;
					if (weapon != null)
					{
						if (p.getItemInUseCount() > 0 || weapon.getItem() instanceof ItemSword || weapon.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, weapon).containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()))
						{
							loc = p.inventory.currentItem;
						}
					}
					
					if (loc == -1)
					{
						double mostDamage = 0F;
						
						for (int i = 0; i < 10; i++)
						{
							if (i != p.inventory.currentItem)
							{
								ItemStack potentialWeapon = p.inventory.mainInventory[i];
								if (potentialWeapon != null)
								{
									Multimap<String, AttributeModifier> modifiers = potentialWeapon.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, potentialWeapon);
									if (modifiers.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()))
									{
										double damage = modifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()).iterator().next().getAmount();
										
										if (damage > mostDamage || loc == -1)
										{
											mostDamage = damage;
											loc = i;
										}
									}
								}
							}
						}
					}
					
					if (loc != -1)
					{
						//System.out.println("LOC " + loc);

						p.inventory.currentItem = loc;

			
						CyberwarePacketHandler.INSTANCE.sendTo(new SwitchHeldItemAndRotationPacket(loc, p.getEntityId(), rank > 2 ? attacker.getEntityId() : -1), (EntityPlayerMP) p);
						
						WorldServer world = (WorldServer) p.worldObj;
						
						for (EntityPlayer trackingPlayer : world.getEntityTracker().getTrackingPlayers(p))
						{
							CyberwarePacketHandler.INSTANCE.sendTo(new SwitchHeldItemAndRotationPacket(loc, p.getEntityId(), rank > 2 ? attacker.getEntityId() : -1), (EntityPlayerMP) trackingPlayer);
						}
						
					}
				}

			}

		}
	}
	
	private Map<EntityLivingBase, Boolean> lastBoostSpeed = new HashMap<EntityLivingBase, Boolean>();
	private Map<EntityLivingBase, Boolean> lastBoostStrength = new HashMap<EntityLivingBase, Boolean>();

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, test))
		{
			boolean last = getLastBoostStrength(e);

			boolean powerUsed = e.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)) : last;
			if (powerUsed)
			{
				if (!e.isInWater() && e.onGround && e.moveForward > 0)
				{
					e.moveRelative(0F, .5F, 0.075F);
				}
				
				if (!last)
				{
					this.onAdded(e, test);
				}
			}
			else if (last)
			{
				this.onRemoved(e, test);
			}
			
			lastBoostStrength.put(e, powerUsed);
		}
		else
		{
			lastBoostStrength.put(e, true);
		}
		
		test = new ItemStack(this, 1, 0);
		if (CyberwareAPI.isCyberwareInstalled(e, test) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(e, test)))
		{
			boolean last = getLastBoostSpeed(e);
			boolean powerUsed = e.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)) : last;
			if (powerUsed)
			{
				this.onAdded(e, test);
			}
			else
			{
				this.onRemoved(e, test);
			}
			
			lastBoostSpeed.put(e, powerUsed);
		}
		else 
		{

			this.onRemoved(e, test);
			
			lastBoostSpeed.remove(e);
		}
	}
	
	private boolean getLastBoostStrength(EntityLivingBase e)
	{
		if (!lastBoostStrength.containsKey(e))
		{
			lastBoostStrength.put(e, true);
		}
		return lastBoostStrength.get(e);
	}
	
	private boolean getLastBoostSpeed(EntityLivingBase e)
	{
		if (!lastBoostSpeed.containsKey(e))
		{
			lastBoostSpeed.put(e, true);
		}
		return lastBoostSpeed.get(e);
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? LibConstants.REFLEXES_CONSUMPTION : LibConstants.REPLACEMENTS_CONSUMPTION;
	}
	
	@Override
	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			switch (stack.stackSize)
			{
				case 1:
					return 9;
				case 2:
					return 10;
				case 3:
					return 11;
			}
		}
		return super.getUnmodifiedEssenceCost(stack);
	}

	@Override
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 0;
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
}
