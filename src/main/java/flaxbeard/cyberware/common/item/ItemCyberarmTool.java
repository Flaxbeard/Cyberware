package flaxbeard.cyberware.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ILimbReplacement;
import flaxbeard.cyberware.client.render.ModelChainsaw;
import flaxbeard.cyberware.client.render.ModelDrill;
import flaxbeard.cyberware.client.render.ModelLifter;
import flaxbeard.cyberware.common.entity.EntityThrownBlock;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.ShootBlockPacket;
import flaxbeard.cyberware.common.network.ToggleCyberarmPacket;

public class ItemCyberarmTool extends ItemCyberware implements ISidedLimb, ILimbReplacement
{
	
	public ItemCyberarmTool(String name, EnumSlot[] slots, String[] subnames)
	{
		super(name, slots, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isEssential(ItemStack stack)
	{
		return true;		
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		ICyberware ware = CyberwareAPI.getCyberware(other);
		return ware.isEssential(other);
	}
	
	@Override
	public EnumSide getSide(ItemStack stack)
	{
		return stack.getItemDamage() % 2 == 0 ? EnumSide.LEFT : EnumSide.RIGHT;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void power(CyberwareUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		for (int i = 0; i < 6; i++)
		{
			ItemStack test = new ItemStack(this, 1, i);
			ItemStack installed = CyberwareAPI.getCyberware(e, test);
			if (e.ticksExisted % 20 == 0 && installed != null)
			{
				boolean used = CyberwareAPI.getCapability(e).usePower(installed, getPowerConsumption(installed));
				
				CyberwareAPI.getCyberwareNBT(installed).setBoolean("active", used);
			}
		}
		
		if (e instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e;
			if (isActive(p) && p.getHeldItemMainhand() != null)
			{
				p.getAttributeMap().removeAttributeModifiers(p.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			}
		}

	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return LibConstants.LIMB_CONSUMPTION;
	}
	
	@Override
	public boolean isLimbActive(ItemStack stack)
	{
		NBTTagCompound data = CyberwareAPI.getCyberwareNBT(stack);
		if (!data.hasKey("active"))
		{
			data.setBoolean("active", true);
		}
		return data.getBoolean("active");
	}
	
	@Override
	public boolean canHoldItems(ItemStack stack)
	{
		return false;
	}
	
	private static Object modelDrill = null;
	private static Object modelChainsaw = null;
	private static Object modelLifter = null;
	
	private static final ResourceLocation textureChainsaw = new ResourceLocation(Cyberware.MODID + ":textures/models/playerChainsawArm.png");
	private static final ResourceLocation textureDrill = new ResourceLocation(Cyberware.MODID + ":textures/models/playerDrillArm.png");
	private static final ResourceLocation textureLifter = new ResourceLocation(Cyberware.MODID + ":textures/models/playerLiftArm.png");
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(ItemStack stack)
	{
		switch (stack.getItemDamage() / 2)
		{
			case 0:
				return textureDrill;
			case 1:
				return textureChainsaw;
			default:
				return textureLifter;
		}
	}
	
	private float lastDrillTicks = 0;
	private float lastSawTicks = 0;
	private float sawTime = 0;
	private float openTimeL = 0;
	private float openTimeR = 0;

	private float lastLifterTime = 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getModel(ItemStack itemStack, boolean wideArms, Object baseWide, Object baseSkinny, EntityPlayer player)
	{	
		if (modelDrill == null)
		{
			modelDrill = new ModelDrill();
			modelChainsaw = new ModelChainsaw();
			modelLifter = new ModelLifter();
		}
		ModelDrill modelDrill	 	= (ModelDrill)    this.modelDrill;
		ModelChainsaw modelChainsaw = (ModelChainsaw) this.modelChainsaw;
		ModelLifter modelLifter 	= (ModelLifter)   this.modelLifter;

		if ((itemStack.getItemDamage() / 2) == 0)
		{
			if (player == Minecraft.getMinecraft().thePlayer)
			{
				float t = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
				if (Minecraft.getMinecraft().thePlayer.getSwingProgress(Minecraft.getMinecraft().getRenderPartialTicks()) > 0
						&& itemStack.equals(getActive(Minecraft.getMinecraft().thePlayer)))
				{
					modelDrill.rBit1.rotateAngleY += 0.5F * (t - lastDrillTicks);
					modelDrill.rBit2.rotateAngleY -= 0.5F * (t - lastDrillTicks);
					modelDrill.rBit3.rotateAngleY += 0.5F * (t - lastDrillTicks);
					modelDrill.rBit4.rotateAngleY -= 0.5F * (t - lastDrillTicks);
					modelDrill.lBit1.rotateAngleY += 0.5F * (t - lastDrillTicks);
					modelDrill.lBit2.rotateAngleY -= 0.5F * (t - lastDrillTicks);
					modelDrill.lBit3.rotateAngleY += 0.5F * (t - lastDrillTicks);
					modelDrill.lBit4.rotateAngleY -= 0.5F * (t - lastDrillTicks);
				}
				lastDrillTicks = t;
			}
			
			return modelDrill;
		}
		else if ((itemStack.getItemDamage() / 2) == 1)
		{
			if (player == Minecraft.getMinecraft().thePlayer)
			{
				float t = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
				
				if (Minecraft.getMinecraft().thePlayer.getSwingProgress(Minecraft.getMinecraft().getRenderPartialTicks()) > 0
						 && itemStack.equals(getActive(Minecraft.getMinecraft().thePlayer)))
				{
					
					sawTime += t - lastSawTicks;
					int n = 22;
					for (int i = 0; i < n; i++)
					{
						float offset = (22F / n) * i;
						
						float[] tO1 = getOffset(offset + sawTime - 0.1F);
						float[] tO2 = getOffset(offset + sawTime + 0.1F);
						float deltaY = tO2[1] - tO1[1];
						float deltaZ = tO2[2] - tO1[2];
						float rads = (float) Math.atan2(deltaZ, deltaY);
						
						float[] rotationPoint = getOffset(offset + sawTime);
						modelChainsaw.lChainPieces[i].setRotationPoint(rotationPoint[0], rotationPoint[1], rotationPoint[2]);
						modelChainsaw.lChainPieces[i].rotateAngleX = rads;
						modelChainsaw.rChainPieces[i].setRotationPoint(rotationPoint[0] - 2, rotationPoint[1], rotationPoint[2]);
						modelChainsaw.rChainPieces[i].rotateAngleX = rads;
					}
				}
				lastSawTicks = t;
			}
			
			return modelChainsaw;
		}
		else
		{
			if (Minecraft.getMinecraft().thePlayer.isSneaking())
			{
				modelLifter = new ModelLifter();
			}


			if (player == Minecraft.getMinecraft().thePlayer)
			{
				boolean isLeft = ((ICyberware) CyberwareAPI.getCyberware(itemStack)).getFirstSlot(itemStack) == EnumSlot.ARMLEFT;
				float t = Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
				ItemStack active = getActive(player);
				
				NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(itemStack);
				
				RayTraceResult res = Minecraft.getMinecraft().objectMouseOver;
				
				if (res.typeOfHit != RayTraceResult.Type.BLOCK)
				{
					openTimeL = Math.min(1, 0.25F * (t - lastLifterTime) + openTimeL);
					openTimeR = Math.min(1, 0.25F * (t - lastLifterTime) + openTimeR);
				}
				else if (active == itemStack && isLeft)
				{
					openTimeL = Math.max(0, openTimeL - 0.25F * (t - lastLifterTime));
					openTimeR = Math.min(1, 0.25F * (t - lastLifterTime) + openTimeR);
				}
				else
				{
					openTimeR = Math.max(0, openTimeR - 0.25F * (t - lastLifterTime));
					openTimeL = Math.min(1, 0.25F * (t - lastLifterTime) + openTimeL);
				}
				
				
				if (tag.hasKey("blockData"))
				{

					NBTTagCompound data = tag.getCompoundTag("blockData");
					int meta = data.getInteger("meta");
					String rn = data.getString("rn");
					Block b = Block.getBlockFromName(rn);
					ItemStack stack = b.getItem(player.worldObj, new BlockPos(0, 0, 0), b.getStateFromMeta(meta));
					
					if (isLeft)
					{
						modelLifter.stackLeft = stack;
						openTimeL = 0;
					}
					else
					{
						modelLifter.stackRight = stack;
						openTimeR = 0;
					}
				}
				else
				{
					
					if (isLeft)
					{
						modelLifter.stackLeft = null;
					}
					else
					{
						modelLifter.stackRight = null;
					}
				}
				
				modelLifter.lBit1.rotationPointZ = -1 - 3 * (float) (Math.cos(openTimeL * Math.PI) + 1F) / 2F;
				modelLifter.lBit2.rotationPointZ = 1 + 3 * (float) (Math.cos(openTimeL * Math.PI) + 1F) / 2F;
				modelLifter.rBit1.rotationPointZ = -1 - 3 * (float) (Math.cos(openTimeR * Math.PI) + 1F) / 2F;
				modelLifter.rBit2.rotationPointZ = 1 + 3 * (float) (Math.cos(openTimeR * Math.PI) + 1F) / 2F;
				
				lastLifterTime = t;
			}
			else
			{
				modelLifter.stackLeft = null;
				modelLifter.stackRight = null;
			}
			return modelLifter;
		}


		
		/*if (Minecraft.getMinecraft().thePlayer.getSwingProgress(Minecraft.getMinecraft().getRenderPartialTicks()) > 0)
		{
			modelDrill.rBit1.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.rBit2.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.rBit3.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.rBit4.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.lBit1.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.lBit2.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.lBit3.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			modelDrill.lBit4.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
		}*/
	}
	
	public static float[] getOffset(float time)
	{
		float t = (time) % 22;
		if (t <= 7)
		{
			return new float[] { 1F , 7.5F + t, -1.5F };
		}
		if (t <= 11)
		{
			float v = (t - 7F) / 4F;
			return new float[] { 1F, 7.5F + 7F + 1.5F * (float) Math.sin(v * Math.PI), -1.5F + 1.5F - 1.5F * (float) Math.cos(v * Math.PI) };
			//return new float[] { 1.5F , 8.5F + 8F, -1.5F + (t-9)};
		}
		if (t > 20)
		{
			return new float[] { 1F , 7.5F, -1.5F + 3};
		}
		return new float[] { 1F , 7.5F + 7F - (t-11), -1.5F + 3};
	}
	
	@Override
	public boolean showAsOffhandIfMainhandEmpty(ItemStack stack)
	{
		return getEnabledState(stack) && isLimbActive(stack);
	}
	
	public static boolean getEnabledState(ItemStack stack)
	{
		NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(stack);
		if (tag.hasKey("enabled"))
		{
			return tag.getBoolean("enabled");
		}
		return false;
	}
	
	public static void toggleEnabledState(ItemStack stack, boolean value)
	{
		NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(stack);
		tag.setBoolean("enabled", value);
	}
	
	
	
	public static boolean isActive(EntityPlayer p)
	{
		ItemStack leftLimb = CyberwareAPI.getCapability(p).getLimb(EnumSlot.ARM, EnumSide.LEFT);
		ItemStack rightLimb = CyberwareAPI.getCapability(p).getLimb(EnumSlot.ARM, EnumSide.RIGHT);
		boolean isLeftInstalled = false;
		boolean isRightInstalled = false;

		if (leftLimb != null && leftLimb.getItem() instanceof ItemCyberarmTool)
		{
			isLeftInstalled = true;
		}
		if (rightLimb != null && rightLimb.getItem() instanceof ItemCyberarmTool)
		{
			isRightInstalled = true;
		}
		
		if (p.getPrimaryHand() == EnumHandSide.LEFT)
		{
			return isLeftInstalled || (isRightInstalled && ((ItemCyberarmTool) rightLimb.getItem()).showAsOffhandIfMainhandEmpty(rightLimb));
		}
		else
		{
			return isRightInstalled || (isLeftInstalled && ((ItemCyberarmTool) leftLimb.getItem()).showAsOffhandIfMainhandEmpty(leftLimb));
		}
	}
	
	public static ItemStack getActive(EntityPlayer p)
	{
		ItemStack leftLimb = CyberwareAPI.getCapability(p).getLimb(EnumSlot.ARM, EnumSide.LEFT);
		ItemStack rightLimb = CyberwareAPI.getCapability(p).getLimb(EnumSlot.ARM, EnumSide.RIGHT);
		boolean isLeftInstalled = false;
		boolean isRightInstalled = false;

		if (leftLimb != null && leftLimb.getItem() instanceof ItemCyberarmTool)
		{
			isLeftInstalled = true;
		}
		if (rightLimb != null && rightLimb.getItem() instanceof ItemCyberarmTool)
		{
			isRightInstalled = true;
		}
		
		if (p.getPrimaryHand() == EnumHandSide.LEFT)
		{
			if (isRightInstalled && ((ItemCyberarmTool) rightLimb.getItem()).showAsOffhandIfMainhandEmpty(rightLimb)) return rightLimb;
			
			return leftLimb;
		}
		else
		{
			if (isLeftInstalled && ((ItemCyberarmTool) leftLimb.getItem()).showAsOffhandIfMainhandEmpty(leftLimb)) return leftLimb;
			
			return rightLimb;
		}
	}
	
	boolean proc = false;
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleBreakSpeed(PlayerEvent.BreakSpeed event)
	{
		if (!proc)
		{
			EntityPlayer p = event.getEntityPlayer();
			
			ItemStack active = getActive(p);
			if (active != null)
			{
				proc = true;

				ItemStack store = p.getHeldItemMainhand();
				p.inventory.mainInventory[p.inventory.currentItem] = new ItemStack(Items.APPLE);
				float bspeed = p.getDigSpeed(event.getState(), event.getPos());
				ItemStack replace = getMiningItem(active);
				p.inventory.mainInventory[p.inventory.currentItem] = replace;
				float speed = p.getDigSpeed(event.getState(), event.getPos());
				p.inventory.mainInventory[p.inventory.currentItem] = store;
				
				if (speed > 1)
				{
					PlayerEvent.BreakSpeed newEvent = new PlayerEvent.BreakSpeed(p, event.getState(), speed > bspeed ? speed * 1.5F : bspeed, event.getPos());
					MinecraftForge.EVENT_BUS.post(newEvent);
					event.setNewSpeed(newEvent.getNewSpeed());
					event.setCanceled(newEvent.isCanceled());
				}
				
				proc = false;

			}
		}
	}
	
	private ItemStack getMiningItem(ItemStack active)
	{
		switch (active.getItemDamage() / 2)
		{
			case 0:
				return new ItemStack(Items.IRON_PICKAXE);
			case 1:
				return new ItemStack(Items.IRON_AXE);
			default:
				return new ItemStack(Items.APPLE);
		}
	}
	
	private ItemStack getAttackItem(ItemStack active)
	{
		switch (active.getItemDamage() / 2)
		{
			case 0:
				return new ItemStack(Items.IRON_AXE);
			case 1:
				return new ItemStack(Items.IRON_SWORD);
			default:
				return new ItemStack(Items.APPLE);
		}	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleAttack(AttackEntityEvent event)
	{
		EntityPlayer p = event.getEntityPlayer();

		ItemStack active = getActive(p);
		if (!proc && active != null)
		{
			proc = true;
			ItemStack store = p.getHeldItemMainhand();

			p.setHeldItem(EnumHand.MAIN_HAND, getAttackItem(active));
			
			event.setCanceled(true);
			p.attackTargetEntityWithCurrentItem(event.getTarget());

			p.inventory.mainInventory[p.inventory.currentItem] = store;

			proc = false;
		}
	}
		
	@SubscribeEvent
	public void handleMining(HarvestCheck event)
	{
		EntityPlayer p = event.getEntityPlayer();
		
		ItemStack active = getActive(p);
		if (active != null)
		{
			ItemStack pick = getMiningItem(active);
			if (pick.canHarvestBlock(event.getTargetBlock()))
			{
				event.setCanHarvest(true);
			}
		}
	}
	
	
	boolean wasPressed = false;
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tick(ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(event.phase == Phase.START && CyberwareAPI.hasCapability(mc.thePlayer))
		{
			if (mc.gameSettings.keyBindSwapHands.isKeyDown() && mc.currentScreen == null)
			{
				if (!wasPressed)
				{
					wasPressed = true;
					ItemStack leftLimb = CyberwareAPI.getCapability(mc.thePlayer).getLimb(EnumSlot.ARM, EnumSide.LEFT);
					ItemStack rightLimb = CyberwareAPI.getCapability(mc.thePlayer).getLimb(EnumSlot.ARM, EnumSide.RIGHT);
					boolean wasActive = isActive(mc.thePlayer);
					
					if (leftLimb != null && leftLimb.getItem() instanceof ItemCyberarmTool)
					{
	
						boolean state = this.getEnabledState(leftLimb);
						
		                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
	
						this.toggleEnabledState(leftLimb, !state);
						CyberwarePacketHandler.INSTANCE.sendToServer(new ToggleCyberarmPacket(true, !state));
					}
					if (rightLimb != null && rightLimb.getItem() instanceof ItemCyberarmTool)
					{
						boolean state = this.getEnabledState(rightLimb);
						this.toggleEnabledState(rightLimb, !state);
						CyberwarePacketHandler.INSTANCE.sendToServer(new ToggleCyberarmPacket(false, !state));
					}
					
					if (!isActive(mc.thePlayer) && wasActive && mc.thePlayer.getHeldItemMainhand() != null)
					{
						mc.thePlayer.getAttributeMap().applyAttributeModifiers(mc.thePlayer.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
					}
					
					
				}
			}
			else
			{
				wasPressed = false;
			}
		}
	}
	
	@SubscribeEvent
	public void handleShootBlock(LeftClickEmpty event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack active = getActive(p);
		if (!event.isCanceled() && active != null && (active.getItemDamage() / 2 == 2))
		{
			CyberwarePacketHandler.INSTANCE.sendToServer(new ShootBlockPacket());
			
			NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(active);
			if (tag.hasKey("blockData") && p.worldObj.isRemote)
			{
				tag.removeTag("blockData");

			}
		}
	}
	
	
	@SubscribeEvent
	public void handleShootBlock(AttackEntityEvent event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack active = getActive(p);
		if (!event.isCanceled() && active != null && (active.getItemDamage() / 2 == 2))
		{
			NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(active);
			
			if (tag.hasKey("blockData") && p.worldObj.isRemote)
			{
				CyberwarePacketHandler.INSTANCE.sendToServer(new ShootBlockPacket());
				tag.removeTag("blockData");
				event.setCanceled(true);

			}
		}
	}
	
	
	@SubscribeEvent
	public void handleShootBlock(LeftClickBlock event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack active = getActive(p);
		if (!event.isCanceled() && active != null && (active.getItemDamage() / 2 == 2))
		{
			NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(active);
			World w = p.getEntityWorld();
			if (tag.hasKey("blockData"))
			{
				if (!w.isRemote)
				{
					NBTTagCompound data = tag.getCompoundTag("blockData");
					String rn = data.getString("rn");
					int meta = data.getInteger("meta");
					
					Block b = Block.getBlockFromName(rn);
					//IBlockState state = 
					EntityThrownBlock falling = new EntityThrownBlock(w, p.posX, p.posY, p.posZ, b.getStateFromMeta(meta), p);
					falling.motionX = p.getLookVec().xCoord * 1;
					falling.motionY = p.getLookVec().yCoord * 1 + 0.5F;
					falling.motionZ = p.getLookVec().zCoord * 1;
					falling.fallTime = 1;
					
					if (data.hasKey("te"))
					{
						NBTTagCompound teTag = data.getCompoundTag("te");
						falling.tileEntityData = teTag;
					}
					
					w.spawnEntityInWorld(falling);
				}
				tag.removeTag("blockData");
			}
			event.setCanceled(true);

		}
	}
	
	
	
	@SubscribeEvent
	public void handleRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack active = getActive(p);
		if (!event.isCanceled() && active != null && (active.getItemDamage() / 2 == 2))
		{
			NBTTagCompound tag = CyberwareAPI.getCyberwareNBT(active);
			World w = event.getWorld();
			IBlockState s = w.getBlockState(event.getPos());
			if (!tag.hasKey("ticks") || tag.getInteger("ticks") != p.ticksExisted)
			{
				if (tag.hasKey("blockData"))
				{
					NBTTagCompound data = tag.getCompoundTag("blockData");
					String rn = data.getString("rn");
					int meta = data.getInteger("meta");
					
					
					BlockPos pos = event.getPos();
					if (!w.getBlockState(pos).getBlock().isReplaceable(w, pos))
					{
						pos = pos.add(event.getFace().getDirectionVec());
					}
					if (w.getBlockState(pos).getBlock().isReplaceable(w, pos))
					{
						Block b = Block.getBlockFromName(rn);
						//IBlockState state = 
						if (b.canPlaceBlockOnSide(w, pos, event.getFace().getOpposite()))
						{
							if (!w.isRemote)
							{
								w.setBlockState(pos, b.getStateFromMeta(meta));
								
								if (data.hasKey("te"))
								{
									NBTTagCompound teTag = data.getCompoundTag("te");
									TileEntity te = TileEntity.func_190200_a(w, teTag);
									w.setTileEntity(pos, te);
								}
								
								w.notifyBlockOfStateChange(pos, b);
								
								List<Entity> entities = w.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
								for (Entity e : entities)
								{
									e.setPosition(e.posX, e.posY + 1, e.posZ);
								}
								
							}
							
							SoundType soundtype = b.getSoundType(b.getStateFromMeta(meta), w, pos, p);
							w.playSound(pos.getX(), pos.getY(), pos.getZ(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
							tag.removeTag("blockData");
							tag.setInteger("ticks", p.ticksExisted);

						}
	
					
						
					}
				}
				else
				{
	
					
					NBTTagCompound data = new NBTTagCompound();
					IBlockState state = w.getBlockState(event.getPos());
					Block block = state.getBlock();
					
					if (state.getBlockHardness(w, event.getPos()) != -1)
					{
						String rn = block.getRegistryName().toString();
						int meta = block.getMetaFromState(state);
						BlockPos pos = event.getPos();
						data.setInteger("meta", meta);
						data.setString("rn", rn);
						
						TileEntity te = w.getTileEntity(pos);
						if (te != null)
						{
							NBTTagCompound teTag = te.serializeNBT();
							data.setTag("te", teTag);
						}
		
						if (!w.isRemote)
						{
							w.removeTileEntity(pos);
							w.setBlockToAir(pos);
						}
						
						SoundType soundtype = block.getSoundType(state, w, pos, p);
						w.playSound(pos.getX(), pos.getY(), pos.getZ(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
						
						
						tag.setTag("blockData", data);
						tag.setInteger("ticks", p.ticksExisted);
					}
				}
			}
		

			event.setCanceled(true);
		}
	}

}
