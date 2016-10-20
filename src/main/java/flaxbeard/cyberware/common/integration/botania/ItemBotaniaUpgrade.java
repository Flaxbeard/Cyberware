package flaxbeard.cyberware.common.integration.botania;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaDiscountEvent;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaItemsEvent;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ItemManaGun;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.client.render.RenderBifrostBody;
import flaxbeard.cyberware.client.render.RenderBifrostBody.RenderBifrostBodyPlayer;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemBotaniaUpgrade extends ItemCyberware implements IManaItem, IManaTooltipDisplay, IMenuItem
{

	public ItemBotaniaUpgrade(String name, EnumSlot[] slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
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
		return stack.getItemDamage() == 0 ? LibConstants.MANA_BONES_MAX_MANA : 0;
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
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)))
		{
			event.add(CyberwareAPI.getCyberware(e, new ItemStack(this, 1, 0)));
		}
	}
	
	@SubscribeEvent
	public void handleDiscount(ManaDiscountEvent event)
	{
		EntityPlayer p = event.getEntityPlayer();
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 3)))
		{
			event.setDiscount(event.getDiscount() + .1F);
		}
	}
	
	@SubscribeEvent
	public void handleBlasterAndFluxfield(CyberwareUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{
			ICyberwareUserData data = CyberwareAPI.getCapability(e);
			ItemStack stack = data.getCyberware(new ItemStack(this, 1, 1));
			
			boolean hasRightArm = CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 1));
			boolean hasLeftArm = CyberwareAPI.isCyberwareInstalled(e, new ItemStack(CyberwareContent.cyberlimbs, 1, 0));

			boolean mainhand = (e.getPrimaryHand() == EnumHandSide.RIGHT ? hasRightArm : hasLeftArm);
			boolean offhand = (e.getPrimaryHand() == EnumHandSide.LEFT ? hasRightArm : hasLeftArm);
			
			ItemStack heldOffhand = e.getHeldItem(EnumHand.OFF_HAND);
			ItemStack heldMainhand = e.getHeldItem(EnumHand.MAIN_HAND);

			if (mainhand && heldMainhand != null && heldMainhand.getItem() instanceof ItemManaGun)
			{
				if (heldMainhand.isItemDamaged() && CyberwareAPI.getCapability(e).usePower(stack, getPowerConsumption(stack)))
				{
					heldMainhand.setItemDamage(heldMainhand.getItemDamage() - 1);
				}
			}
			
			if (offhand && heldOffhand != null && heldOffhand.getItem() instanceof ItemManaGun)
			{
				if (heldOffhand.isItemDamaged() && CyberwareAPI.getCapability(e).usePower(stack, getPowerConsumption(stack)))
				{
					heldOffhand.setItemDamage(heldOffhand.getItemDamage() - 1);
				}
			}
			

		}
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{
			ICyberwareUserData data = CyberwareAPI.getCapability(e);
			ItemStack stack = data.getCyberware(new ItemStack(this, 1, 2));
			if (e.ticksExisted % 20 == 0)
			{
				int i = 0;
				while (!data.isAtCapacity(stack) && i < LibConstants.FLUXFIELD_PRODUCTION)
				{
					if (!(e instanceof EntityPlayer) || ManaItemHandler.requestManaExact(stack, (EntityPlayer) e, LibConstants.MANA_PER_POWER, true))
					{
						data.addPower(1, stack);
					}
					else
					{
						break;
					}
					i++;
				}
			}
		}
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 1 ? LibConstants.OVERCLOCK_BLASTER_CONSUMPTION : 0;
	}
	
	@Override
	public int getPowerProduction(ItemStack stack)
	{
		return stack.getItemDamage() == 2 ? LibConstants.FLUXFIELD_PRODUCTION : 0;
	}
	
	public boolean isUpgraded(EntityLivingBase e)
	{
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{
			ItemStack stack = CyberwareAPI.getCyberware(e, new ItemStack(this, 1, 1));
			NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
			return comp.hasKey("active") ? comp.getBoolean("active") : false;
		}
		return false;
	}
	
	private static float ease(float percent, float startValue, float endValue)
	{
		endValue -= startValue;
		float total = 100;
		float elapsed = percent * total;
		
		if ((elapsed /= total / 2) < 1)
			return endValue / 2 * elapsed * elapsed + startValue;
		return -endValue / 2 * ((--elapsed) * (elapsed - 2) - 1) + startValue;
	}
	
	
	private Map<Integer, Float[]> entitiesClient = new HashMap<Integer, Float[]>();
	private Map<Integer, Float[]> entitiesServer = new HashMap<Integer, Float[]>();
	
	boolean pushed = false;
	
	
	@SideOnly(Side.CLIENT)
	public static RenderBifrostBody render;
	
	@SideOnly(Side.CLIENT)
	public static RenderBifrostBodyPlayer renderP;
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleMissingSkin(RenderLivingEvent.Pre event)
	{
		EntityLivingBase e = event.getEntity();
		if (!pushed && CyberwareAPI.hasCapability(e) && isUpgraded(e))
		{
			float progress = getProgress(e);
			pushed = true;
			
			GL11.glPushMatrix();
			GL11.glColor3d(
					(Math.sin((2*Math.PI/3) + 0.125 * (e.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks())) + 1.) / 2.,
					(Math.sin((4*Math.PI/3) + 0.125 * (e.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks())) + 1.) / 2.,
					(Math.sin(0.125 * (e.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks())) + 1.) / 2.);
			
			GL11.glTranslated(event.getX(), event.getY(), event.getZ());
			GL11.glScalef(2.5F, 2.5F, 2.5F);
			GL11.glTranslated(-event.getX(), -event.getY(), -event.getZ());
			
			float f = e.limbSwingAmount;
			float g = e.prevLimbSwingAmount;
			float q = e.limbSwing;
			e.prevLimbSwingAmount *= .5F;
			e.limbSwingAmount *= .5F;
			e.limbSwing *= 0.5F;
			
			ItemStack mainhand = e.getHeldItemMainhand();
			ItemStack offhand = e.getHeldItemOffhand();

			
			if (e instanceof AbstractClientPlayer)
			{
				if (progress > 60F)
				{
					EnumHandSide side = ((EntityPlayer) e).getPrimaryHand();
					AbstractClientPlayer p = (AbstractClientPlayer) e;
					if (renderP == null)
					{
						renderP = new RenderBifrostBodyPlayer(Minecraft.getMinecraft().getRenderManager());
					}
					renderP.getMainModel().bipedBody.isHidden = true;
					renderP.getMainModel().bipedHead.isHidden = true;
	
					float z = e.renderYawOffset;
					
					GL11.glPushMatrix();
					if (side == EnumHandSide.RIGHT)
					{
						p.inventory.mainInventory[p.inventory.currentItem] = mainhand;
						p.inventory.offHandInventory[0] = null;
					}
					else
					{
						p.inventory.mainInventory[p.inventory.currentItem] = null;
						p.inventory.offHandInventory[0] = offhand;
					}
					renderP.getMainModel().bipedLeftLeg.isHidden = true;
					renderP.getMainModel().bipedLeftArm.isHidden = true;
					renderP.getMainModel().bipedRightLeg.isHidden = false;
					renderP.getMainModel().bipedRightArm.isHidden = false;
					GL11.glRotatef(-z, 0F, 1F, 0F);
					GL11.glTranslatef(-0.05F, 0F, 0F);
					GL11.glRotatef(-z, 0F, -1F, 0F);
					renderP.doRender((AbstractClientPlayer) e, event.getX(), event.getY(), event.getZ(), e.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks());
					GL11.glPopMatrix();
					
					GL11.glPushMatrix();
					if (side == EnumHandSide.LEFT)
					{
						p.inventory.mainInventory[p.inventory.currentItem] = mainhand;
						p.inventory.offHandInventory[0] = null;
					}
					else
					{
						p.inventory.mainInventory[p.inventory.currentItem] = null;
						p.inventory.offHandInventory[0] = offhand;
					}
					renderP.getMainModel().bipedLeftLeg.isHidden = false;
					renderP.getMainModel().bipedLeftArm.isHidden = false;
					renderP.getMainModel().bipedRightLeg.isHidden = true;
					renderP.getMainModel().bipedRightArm.isHidden = true;
					GL11.glRotatef(-z, 0F, 1F, 0F);
					GL11.glTranslatef(0.05F, 0F, 0F);
					GL11.glRotatef(-z, 0F, -1F, 0F);
					renderP.doRender((AbstractClientPlayer) e, event.getX(), event.getY(), event.getZ(), e.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks());
					GL11.glPopMatrix();
				}
			}
			else if (e instanceof EntityLiving)
			{
				if (render == null)
				{
					render = new RenderBifrostBody(Minecraft.getMinecraft().getRenderManager());
				}
				((ModelPlayer) render.getMainModel()).bipedBody.isHidden = true;
				((ModelPlayer) render.getMainModel()).bipedHead.isHidden = true;
				render.doRender((EntityLiving) e, event.getX(), event.getY(), event.getZ(), e.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks());
			}
			
			GL11.glColor3f(1F, 1F, 1F);
			GL11.glPopMatrix();
			

			
			GL11.glPushMatrix();
			float ticks = e.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
			float percentMove = (float) (1F + Math.sin(ticks / 20F)) / 2F;
			GL11.glTranslatef(0F, ease(Math.min(60F, progress) / 60F, 0F, 1F) * (2.2F - 0.3F * percentMove), 0F);
			event.setCanceled(true);
			e.limbSwingAmount *= 0.25F;
			e.prevLimbSwingAmount *= 0.25F;

			e.setHeldItem(EnumHand.MAIN_HAND, null);
			e.setHeldItem(EnumHand.OFF_HAND, null);
			
			event.getRenderer().doRender(e, event.getX(), event.getY(), event.getZ(), e.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks());
			GL11.glPopMatrix();
			
			e.limbSwingAmount = f;
			e.prevLimbSwingAmount = g;
			e.limbSwing = q;
			
			if (e instanceof AbstractClientPlayer)
			{
				AbstractClientPlayer p = (AbstractClientPlayer) e;
				p.inventory.mainInventory[p.inventory.currentItem] = mainhand;
				p.inventory.offHandInventory[0] = offhand;
			}
			else
			{
				e.setHeldItem(EnumHand.MAIN_HAND, mainhand);
				e.setHeldItem(EnumHand.OFF_HAND, offhand);
			}
		
			
			pushed = false;
		}
	}

	private float getProgress(EntityLivingBase e)
	{
		ItemStack stack = CyberwareAPI.getCyberware(e, new ItemStack(this, 1, 1));
		NBTTagCompound comp = CyberwareAPI.getCyberwareNBT(stack);
		int ticks = comp.hasKey("ticks") ? comp.getInteger("ticks") : e.ticksExisted;
		return Math.max(0, ((e.ticksExisted - ticks) - 25 + Minecraft.getMinecraft().getRenderPartialTicks()));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleMissingSkin(RenderLivingEvent.Post event)
	{
		EntityLivingBase e = event.getEntity();

	}
	
	float lastProgress = 0;
	
	@SubscribeEvent
	public void handleWorld(RenderWorldLastEvent event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (p != null && CyberwareAPI.hasCapability(p) && isUpgraded(p))
		{
			float progress = Math.min(60F, getProgress(p));
			//p.rotationYaw += (ease(progress / 60F, 0F, 1F) * 720F) - (ease(lastProgress / 60F, 0F, 1F) * 720F);
			//p.renderYawOffset += (ease(progress / 60F, 0F, 1F) * 720F) - (ease(lastProgress / 60F, 0F, 1F) * 720F);
			p.eyeHeight = p.getDefaultEyeHeight() + ease(progress / 60F, 0F, 1F) *  2.2f;
			lastProgress = progress;
		}
	}
	
	
	@SubscribeEvent
    public void playerTick(LivingUpdateEvent event)
    {
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.hasCapability(e) && isUpgraded(e))
		{
			if (e.worldObj.isRemote)
			{
				if (e == Minecraft.getMinecraft().thePlayer && getProgress(e) < 60F)
				{
					KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode(), false);
					KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode(), false);
					KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode(), false);
					KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode(), false);
					KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), false);

				}
				if (!entitiesClient.keySet().contains(e.getEntityId()))
				{
					entitiesClient.put(e.getEntityId(), new Float[] { e.width, e.height });
					e.moveEntity(-0.5F, 0F, -0.5F);
				}
			}
			else
			{
				if (!entitiesServer.keySet().contains(e.getEntityId()))
				{
	
					entitiesServer.put(e.getEntityId(), new Float[] { e.width, e.height });
					e.moveEntity(-0.5F, 0F, -0.5F);
				}
			}
			
			e.width = 1.6f;
			e.height = 4.0f;
			if (e instanceof EntityPlayer && (!e.worldObj.isRemote || e != Minecraft.getMinecraft().thePlayer))
			{
				((EntityPlayer) e).eyeHeight = ((EntityPlayer) e).getDefaultEyeHeight() + 2.2f;
			}
			AxisAlignedBB boundingBox = e.getEntityBoundingBox();
			AxisAlignedBB boundingBox2 = new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.minX + e.width, boundingBox.minY + e.height, boundingBox.minZ + e.width);
			e.setEntityBoundingBox(boundingBox2);
		}
		else
		{
			
			if ((e.worldObj.isRemote && entitiesClient.keySet().contains(e.getEntityId())))
			{
				Float[] f = entitiesClient.get(e.getEntityId());
				e.width = f[0];
				e.height = f[1];
				AxisAlignedBB boundingBox = e.getEntityBoundingBox();
				AxisAlignedBB boundingBox2 = new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.minX + e.width, boundingBox.minY + e.height, boundingBox.minZ + e.width);
				e.setEntityBoundingBox(boundingBox2);
				entitiesClient.remove(e.getEntityId());
				
				if (e instanceof EntityPlayer)
				{
					((EntityPlayer) e).eyeHeight = ((EntityPlayer) e).getDefaultEyeHeight();
				}
				
				float partX = (float)Math.cos(Math.toRadians(e.renderYawOffset));
				float partZ = (float)Math.sin(Math.toRadians(e.renderYawOffset));
			//	e.setPosition(e.posX, e.posY + 2.2F, e.posZ);
				e.setPositionAndRotation(e.posX, e.posY + 2.2F, e.posZ, e.rotationYaw, e.rotationPitch);
				//e.moveEntity(0, 2.2F, 0);
				//e.motionY = .2F;

		
				for (int x = -1; x <=1; x++)
				{
					for (int z = -1; z <=1; z++)
					{
						for (int y = 0; y < 8; y++)
						{
							float yDist = y * .3125F;
							float xDist = x * .3125F;
							float zDist = z * .3125F;
							
							e.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, e.posX + partX * (xDist + 1F) + partZ * (zDist), e.posY + yDist + 1.4F, e.posZ + partZ * (xDist + 1F) + partX * (zDist), 0, 0, 0, Block.getStateId(ModBlocks.bifrost.getDefaultState()));
							
							e.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, e.posX + partX * (xDist - 1F) + partZ * (zDist), e.posY + yDist + 1.4F, e.posZ + partZ * (xDist - 1F) + partX * (zDist), 0, 0, 0, Block.getStateId(ModBlocks.bifrost.getDefaultState()));

							e.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, e.posX + partX * (xDist + .25F) + partZ * (zDist), e.posY + yDist, e.posZ + partZ * (xDist + .25F) + partX * (zDist), 0, 0, 0, Block.getStateId(ModBlocks.bifrost.getDefaultState()));
														
							e.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, e.posX + partX * (xDist - .25F) + partZ * (zDist), e.posY + yDist, e.posZ + partZ * (xDist - .25F) + partX * (zDist), 0, 0, 0, Block.getStateId(ModBlocks.bifrost.getDefaultState()));
							e.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1F, 1F);
						}
					}
				}
					
				
			}
			if ((!e.worldObj.isRemote && entitiesServer.keySet().contains(e.getEntityId())))
			{
				Float[] f = entitiesServer.get(e.getEntityId());
				e.width = f[0];
				e.height = f[1];
				AxisAlignedBB boundingBox = e.getEntityBoundingBox();
				AxisAlignedBB boundingBox2 = new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.minX + e.width, boundingBox.minY + e.height, boundingBox.minZ + e.width);
				e.setEntityBoundingBox(boundingBox2);
				entitiesServer.remove(e.getEntityId());
				
				e.setPosition(e.posX, e.posY + 2.2F, e.posZ);
				e.motionY = .2F;

				
				if (e instanceof EntityPlayer)
				{
					((EntityPlayer) e).eyeHeight = ((EntityPlayer) e).getDefaultEyeHeight();
				}
			}
			
		}
    }

	@Override
	public boolean hasMenu(ItemStack stack)
	{
		return stack.getItemDamage() == 1;
	}

	@Override
	public void use(Entity e, ItemStack stack)
	{
		NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(stack);
		boolean active = false;
		if (tag.hasKey("active"))
		{
			active = tag.getBoolean("active");
		}
		tag.setBoolean("active", !active);
		if (active)
		{
			tag.setInteger("ticks", e.ticksExisted);
		}
	}

	@Override
	public String getUnlocalizedLabel(ItemStack stack)
	{
		return "EE";
	}

	@Override
	public float[] getColor(ItemStack stack)
	{
		return null;
	}
	
}
