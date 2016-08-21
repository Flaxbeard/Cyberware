package flaxbeard.cyberware.common.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.HashMultimap;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import flaxbeard.cyberware.common.lib.LibConstants;

public class EssentialsMissingHandler
{
	public static final DamageSource brainless = new DamageSource("cyberware.brainless").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource heartless = new DamageSource("cyberware.heartless").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource surgery = new DamageSource("cyberware.surgery").setDamageBypassesArmor();
	public static final DamageSource spineless = new DamageSource("cyberware.spineless").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource nomuscles = new DamageSource("cyberware.nomuscles").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource noessence = new DamageSource("cyberware.noessence").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource lowessence = new DamageSource("cyberware.lowessence").setDamageBypassesArmor().setDamageIsAbsolute();

	public static final EssentialsMissingHandler INSTANCE = new EssentialsMissingHandler();

	private static Map<EntityLivingBase, Integer> timesLungs = new HashMap<EntityLivingBase, Integer>();
	
	private static final UUID speedId = UUID.fromString("fe00fdea-5044-11e6-beb8-9e71128cae77");

	private Map<EntityLivingBase, Boolean> last = new HashMap<EntityLivingBase, Boolean>();
	private Map<EntityLivingBase, Boolean> lastClient = new HashMap<EntityLivingBase, Boolean>();

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleMissingEssentials(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (e.ticksExisted % 20 == 0)
			{
				cyberware.resetBuffer();
			}
			
			if (!cyberware.hasEssential(EnumSlot.CRANIUM))
			{
				e.attackEntityFrom(brainless, Integer.MAX_VALUE);
			}
			
			if (cyberware.getEssence() < CyberwareConfig.CRITICAL_ESSENCE && e instanceof EntityPlayer && e.ticksExisted % 100 == 0 && !e.isPotionActive(CyberwareContent.neuropozyneEffect))
			{
				e.attackEntityFrom(lowessence, 2F);
			}
						
			int numMissingLegs = 0;
			int numMissingLegsVisible = 0;
			
			if (cyberware.getEssence() <= 0)
			{
				e.attackEntityFrom(noessence, Integer.MAX_VALUE);
			}
			
			if (!cyberware.hasEssential(EnumSlot.LEG, EnumSide.LEFT))
			{
				numMissingLegs++;
				numMissingLegsVisible++;
			}
			if (!cyberware.hasEssential(EnumSlot.LEG, EnumSide.RIGHT))
			{
				numMissingLegs++;
				numMissingLegsVisible++;

			}
			
			ItemStack legLeft = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 2));
			if (legLeft != null && !ItemCyberlimb.isPowered(legLeft))
			{
				numMissingLegs++;
			}
			
			ItemStack legRight = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 3));
			if (legRight != null && !ItemCyberlimb.isPowered(legRight))
			{
				numMissingLegs++;
			}
			

			if (e instanceof EntityPlayer)
			{
		
				
				if (numMissingLegsVisible == 2)
				{
					e.height = 1.8F - (10F / 16F);
					((EntityPlayer) e).eyeHeight = ((EntityPlayer) e).getDefaultEyeHeight() - (10F / 16F);
					AxisAlignedBB axisalignedbb = e.getEntityBoundingBox();
					e.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)e.width, axisalignedbb.minY + (double)e.height, axisalignedbb.minZ + (double)e.width));
					
					if (e.worldObj.isRemote)
					{
						lastClient.put(e, true);
					}
					else
					{
						last.put(e, true);
					}
	
				}
				else if (last(e.worldObj.isRemote, e))
				{
					e.height = 1.8F;
					((EntityPlayer) e).eyeHeight = ((EntityPlayer) e).getDefaultEyeHeight();
					AxisAlignedBB axisalignedbb = e.getEntityBoundingBox();
					e.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)e.width, axisalignedbb.minY + (double)e.height, axisalignedbb.minZ + (double)e.width));
					
					if (e.worldObj.isRemote)
					{
						lastClient.put(e, false);
					}
					else
					{
						last.put(e, false);
					}
				}
			}
			
			if (numMissingLegs >= 1 && e.onGround)
			{

				HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
				
				multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Missing leg speed", -100F, 0));
				e.getAttributeMap().applyAttributeModifiers(multimap);

				//e.moveEntity(e.lastTickPosX - e.posX, 0, e.lastTickPosZ - e.posZ);
			}
			else
			{
				HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
				
				multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Missing leg speed", -100F, 0));
				e.getAttributeMap().removeAttributeModifiers(multimap);
			}
			
			

			if (!cyberware.hasEssential(EnumSlot.HEART))
			{
				e.attackEntityFrom(heartless, Integer.MAX_VALUE);
			}
			
			if (!cyberware.hasEssential(EnumSlot.BONE))
			{
				
				e.attackEntityFrom(spineless, Integer.MAX_VALUE);
			}
			
			if (!cyberware.hasEssential(EnumSlot.MUSCLE))
			{
				e.attackEntityFrom(nomuscles, Integer.MAX_VALUE);
			}
			
			
			if (!cyberware.hasEssential(EnumSlot.LUNGS))
			{
				if (getLungsTime(e) >= 20)
				{
					timesLungs.put(e, e.ticksExisted);
					e.attackEntityFrom(DamageSource.drown, 2F);
				}
			}
			else
			{
				timesLungs.remove(e);
			}

			
			
		}
	}
	
	private boolean last(boolean remote, EntityLivingBase e)
	{
		if (remote)
		{
			if (!lastClient.containsKey(e))
			{
				lastClient.put(e, false);
			}
			return lastClient.get(e);
		}
		else
		{
			if (!last.containsKey(e))
			{
				last.put(e, false);
			}
			return last.get(e);
		}
	}
	

	@SubscribeEvent
	public void handleJump(LivingJumpEvent event)
	{
			EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);

			int numMissingLegs = 0;
			
			if (!cyberware.hasEssential(EnumSlot.LEG, EnumSide.LEFT))
			{
				numMissingLegs++;
			}
			if (!cyberware.hasEssential(EnumSlot.LEG, EnumSide.RIGHT))
			{
				numMissingLegs++;
			}
			
			ItemStack legLeft = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 2));
			if (legLeft != null && !ItemCyberlimb.isPowered(legLeft))
			{
				numMissingLegs++;
			}
			
			ItemStack legRight = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 3));
			if (legRight != null && !ItemCyberlimb.isPowered(legRight))
			{
				numMissingLegs++;
			}
			
			if (numMissingLegs == 2)
			{
				e.motionY = 0.2F;
			}
		}
	}
		
	private int getLungsTime(EntityLivingBase e)
	{
		if (!timesLungs.containsKey(e))
		{
			timesLungs.put(e, e.ticksExisted);
		}
		return e.ticksExisted - timesLungs.get(e);
	}
	
	private static Map<EntityLivingBase, Integer> hunger = new HashMap<EntityLivingBase, Integer>();
	private static Map<EntityLivingBase, Float> saturation = new HashMap<EntityLivingBase, Float>();

	@SubscribeEvent
	public void handleEatFoodTick(LivingEntityUseItemEvent.Tick event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack stack = event.getItem();
		
		if (e instanceof EntityPlayer && CyberwareAPI.hasCapability(e) && stack != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT)
		{
			EntityPlayer p = (EntityPlayer) e;
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (!cyberware.hasEssential(EnumSlot.LOWER_ORGANS))
			{
				hunger.put(p, p.getFoodStats().getFoodLevel());
				saturation.put(p, p.getFoodStats().getSaturationLevel());
			}
		}
		else
		{
			hunger.remove(e);
			saturation.remove(e);
		}
	}
	
	@SubscribeEvent
	public void handleEatFoodEnd(LivingEntityUseItemEvent.Finish event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack stack = event.getItem();
		
		if (e instanceof EntityPlayer && CyberwareAPI.hasCapability(e) && stack != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT)
		{
			EntityPlayer p = (EntityPlayer) e;
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (!cyberware.hasEssential(EnumSlot.LOWER_ORGANS))
			{
				int hungerVal = hunger.keySet().contains(p) ? hunger.get(p) : p.getFoodStats().getFoodLevel();
				float satVal = saturation.keySet().contains(p) ? saturation.get(p) : p.getFoodStats().getSaturationLevel();

				p.getFoodStats().setFoodLevel(hungerVal);
				p.getFoodStats().setFoodSaturationLevel(satVal);
			}
		}
	}
	
	public static final ResourceLocation BLACK_PX = new ResourceLocation(Cyberware.MODID + ":textures/gui/blackpx.png");
	
	private boolean removedMove = false;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void overlayPre(ClientTickEvent event)
	{
		if (event.phase == Phase.START && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null)
		{
			EntityPlayer e = Minecraft.getMinecraft().thePlayer;
	
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Missing leg speed", -100F, 0));
			e.getAttributeMap().removeAttributeModifiers(multimap);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void overlayPre(RenderGameOverlayEvent.Pre event)
	{
	
		if (event.getType() == ElementType.ALL)
		{
			EntityPlayer e = Minecraft.getMinecraft().thePlayer;
			
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(speedId, "Missing leg speed", -100F, 0));
			//e.getAttributeMap().removeAttributeModifiers(multimap);
			
			
			if (CyberwareAPI.hasCapability(e))
			{
				ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
				
				if (!cyberware.hasEssential(EnumSlot.EYES) && !e.isCreative())
				{
					Minecraft.getMinecraft().getTextureManager().bindTexture(BLACK_PX);
					ClientUtils.drawTexturedModalRect(0, 0, 0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
				}
			}
			
			if (TileEntitySurgery.workingOnPlayer)
			{
				float trans = 1.0F;
				float ticks = TileEntitySurgery.playerProgressTicks + event.getPartialTicks();
				if (ticks < 20F)
				{
					trans = ticks / 20F;
				}
				else if (ticks > 60F)
				{
					trans = (80F - ticks) / 20F;
				}
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, trans);
				Minecraft.getMinecraft().getTextureManager().bindTexture(BLACK_PX);
				ClientUtils.drawTexturedModalRect(0, 0, 0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
	}
	

	@SubscribeEvent
	public void handleMissingSkin(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (!cyberware.hasEssential(EnumSlot.SKIN))
			{
		
				if (!event.getSource().isUnblockable() || event.getSource() == DamageSource.fall)
				{
					event.setAmount(event.getAmount() * 3F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			processEvent(event, event.getHand(), event.getEntityPlayer(), cyberware);
		}
	}
	
	@SubscribeEvent
	public void handleLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			processEvent(event, event.getHand(), event.getEntityPlayer(), cyberware);
		}
	}
	
	@SubscribeEvent
	public void handleRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			processEvent(event, event.getHand(), event.getEntityPlayer(), cyberware);
		}
	}
	
	@SubscribeEvent
	public void handleRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			processEvent(event, event.getHand(), event.getEntityPlayer(), cyberware);
		}
	}

	private void processEvent(Event event, EnumHand hand, EntityPlayer p, ICyberwareUserData cyberware)
	{
		EnumHandSide mainHand = p.getPrimaryHand();
		EnumHandSide offHand = ((mainHand == EnumHandSide.LEFT) ? EnumHandSide.RIGHT : EnumHandSide.LEFT);
		EnumSide correspondingMainHand = ((mainHand == EnumHandSide.RIGHT) ? EnumSide.RIGHT : EnumSide.LEFT);
		EnumSide correspondingOffHand = ((offHand == EnumHandSide.RIGHT) ? EnumSide.RIGHT : EnumSide.LEFT);
		
		boolean leftUnpowered = false;
		ItemStack armLeft = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 0));
		if (armLeft != null && !ItemCyberlimb.isPowered(armLeft))
		{
			leftUnpowered = true;
		}
		
		boolean rightUnpowered = false;
		ItemStack armRight = cyberware.getCyberware(new ItemStack(CyberwareContent.cyberlimbs, 1, 1));
		if (armRight != null && !ItemCyberlimb.isPowered(armRight))
		{
			rightUnpowered = true;
		}

		if (hand == EnumHand.MAIN_HAND && (!cyberware.hasEssential(EnumSlot.ARM, correspondingMainHand) || leftUnpowered))
		{
			event.setCanceled(true);
		}
		else if (hand == EnumHand.OFF_HAND && (!cyberware.hasEssential(EnumSlot.ARM, correspondingOffHand) || rightUnpowered))
		{
			event.setCanceled(true);
		}
	}
}
