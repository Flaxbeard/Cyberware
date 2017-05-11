package flaxbeard.cyberware.common.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Objects;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.RFIDRegistry;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.api.item.ILimbReplacement;
import flaxbeard.cyberware.client.render.RenderCyberlimbHand;
import flaxbeard.cyberware.client.render.RenderPlayerCyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberarmTool;

public class EssentialsMissingHandlerClient
{
	public static final EssentialsMissingHandlerClient INSTANCE = new EssentialsMissingHandlerClient();

	@SideOnly(Side.CLIENT)
	public static final RenderPlayerCyberware renderT = new RenderPlayerCyberware(Minecraft.getMinecraft().getRenderManager(), true);

	@SideOnly(Side.CLIENT)
	public static final RenderPlayerCyberware renderF = new RenderPlayerCyberware(Minecraft.getMinecraft().getRenderManager(), false);
	
	private static float lastPingTime = 0F;
	private static float totalPing = 0F;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleMissingSkin(RenderPlayerEvent.Pre event)
	{
		
		EntityPlayer p = event.getEntityPlayer();
		if (CyberwareConfig.RENDER && CyberwareAPI.hasCapability(p))
		{	
			
			
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(p);
			boolean hasLeftLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.LEFT);
			boolean hasRightLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.RIGHT);
			boolean hasLeftArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT);
			boolean hasRightArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT);
			
			ItemStack leftArm = cyberware.getLimb(EnumSlot.ARM, EnumSide.LEFT);
			ItemStack rightArm = cyberware.getLimb(EnumSlot.ARM, EnumSide.RIGHT);
			ItemStack leftLeg = cyberware.getLimb(EnumSlot.LEG, EnumSide.LEFT);
			ItemStack rightLeg = cyberware.getLimb(EnumSlot.LEG, EnumSide.RIGHT);

			
			if (!(event.getRenderer() instanceof RenderPlayerCyberware))
			{
				boolean bigArms = ReflectionHelper.getPrivateValue(RenderPlayer.class, event.getRenderer(), 0);

				boolean noSkin = false;
				if (!cyberware.hasEssential(EnumSlot.SKIN))
				{
					event.setCanceled(true);
					noSkin = true;
					if (bigArms)
					{
						renderT.doMuscles = true;
					}
					else
					{
						renderF.doMuscles = true;
					}
				}
				

				boolean lower = false;
				if (!hasRightLeg && !hasLeftLeg)
				{
					// Hide pants + shoes
					pants.put(p.getEntityId(), p.inventory.armorInventory[1]);
					p.inventory.armorInventory[1] = null;
					shoes.put(p.getEntityId(), p.inventory.armorInventory[0]);
					p.inventory.armorInventory[0] = null;
					lower = true;
				}
				


				
				if (!hasRightLeg || !hasLeftLeg || !hasRightArm || !hasLeftArm || leftArm != null || rightArm != null || leftLeg != null || rightLeg != null)
				{
					event.setCanceled(true);

					//boolean leftArmRusty = robotLeftArm && CyberwareContent.cyberlimbs.getQuality(CyberwareAPI.getCyberware(p, new ItemStack(CyberwareContent.cyberlimbs, 0, 0))) == CyberwareAPI.QUALITY_SCAVENGED;
					//boolean rightArmRusty = robotRightArm && CyberwareContent.cyberlimbs.getQuality(CyberwareAPI.getCyberware(p, new ItemStack(CyberwareContent.cyberlimbs, 0, 1))) == CyberwareAPI.QUALITY_SCAVENGED;
					//boolean leftLegRusty = robotLeftLeg && CyberwareContent.cyberlimbs.getQuality(CyberwareAPI.getCyberware(p, new ItemStack(CyberwareContent.cyberlimbs, 0, 2))) == CyberwareAPI.QUALITY_SCAVENGED;
					//boolean rightLegRusty = robotRightLeg && CyberwareContent.cyberlimbs.getQuality(CyberwareAPI.getCyberware(p, new ItemStack(CyberwareContent.cyberlimbs, 0, 3))) == CyberwareAPI.QUALITY_SCAVENGED;

					if (bigArms)
					{
						renderT.doRender((AbstractClientPlayer) p, event.getX(), event.getY() - (lower ? (11F / 16F) : 0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());

					}
					else
					{
						renderF.doRender((AbstractClientPlayer) p, event.getX(), event.getY() - (lower ? (11F / 16F) : 0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
					}
					
					if (!cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.skinUpgrades, 1, 2)))
					{
						
						
						Set<Tuple<ResourceLocation, Tuple<ModelPlayer, Boolean>>> setups = new HashSet<Tuple<ResourceLocation, Tuple<ModelPlayer, Boolean>>>();
						Tuple<ResourceLocation, Tuple<ModelPlayer, Boolean>>[] individualSetups = new Tuple[4];
						ItemStack[] limbs = new ItemStack[] { leftArm, rightArm, leftLeg, rightLeg };

						for (int i = 0; i < limbs.length; i++)
						{
							if (limbs[i] != null)
							{
								ILimbReplacement lr = ((ILimbReplacement) CyberwareAPI.getCyberware(limbs[i]));
								individualSetups[i] = new Tuple<>(
													lr.getTexture(limbs[i]), 
													new Tuple<>(
															(ModelPlayer) lr.getModel(limbs[i], !bigArms, renderF.getMainModel(), renderT.getMainModel(), p),
															lr.canHoldItems(limbs[i])
															));
								setups.add(individualSetups[i]);
							}
						}
						
						for (Tuple<ResourceLocation, Tuple<ModelPlayer, Boolean>> setup : setups)
						{
							ModelPlayer temp = renderF.getMainModel();
							
							ModelPlayer mp = setup.getSecond().getFirst();
							renderF.setMainModel(mp);
							mp.bipedBody.isHidden = true;
							mp.bipedHead.isHidden = true;
							
							mp.bipedLeftArm.isHidden  = !setup.equals(individualSetups[0]);
							mp.bipedRightArm.isHidden = !setup.equals(individualSetups[1]);
							mp.bipedLeftLeg.isHidden  = !setup.equals(individualSetups[2]);
							mp.bipedRightLeg.isHidden = !setup.equals(individualSetups[3]);

							renderF.doCustom = true;
							renderF.canHoldItems = setup.getSecond().getSecond();
							renderF.texture = setup.getFirst();
							renderF.doRender((AbstractClientPlayer) p, event.getX(), event.getY() - (lower ? (11F / 16F) : 0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
							renderF.doCustom = false;
							renderF.canHoldItems = true;

							mp.bipedBody.isHidden = false;
							mp.bipedHead.isHidden = false;
							mp.bipedLeftArm.isHidden = false;
							mp.bipedRightArm.isHidden = false;
							mp.bipedLeftLeg.isHidden = false;
							mp.bipedRightLeg.isHidden = false;
							
							renderF.setMainModel(temp);
						}

					}
				
				}
				else if (noSkin)
				{
					if (bigArms)
					{
						renderT.doRender((AbstractClientPlayer) p, event.getX(), event.getY(), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
					}
					else
					{
						renderF.doRender((AbstractClientPlayer) p, event.getX(), event.getY(), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
					}
				}
				
				if (noSkin)
				{
					if (bigArms)
					{
						renderT.doMuscles = false;
					}
					else
					{
						renderF.doMuscles = false;
					}
				}
			}
			
			RenderPlayer renderer = event.getRenderer();

			


			if (!hasLeftLeg || (leftLeg != null && (!(renderer instanceof RenderPlayerCyberware) || !((RenderPlayerCyberware) renderer).doCustom)))
			{
				renderer.getMainModel().bipedLeftLeg.isHidden = true;
			}
			
			if (!hasRightLeg || (rightLeg != null && (!(renderer instanceof RenderPlayerCyberware) || !((RenderPlayerCyberware) renderer).doCustom)))
			{
				renderer.getMainModel().bipedRightLeg.isHidden = true;
			}

			
			if (!hasLeftArm)
			{
				renderer.getMainModel().bipedLeftArm.isHidden = true;
				/*
				// Hide the main or offhand item if no arm there
				if (mc.gameSettings.mainHand == EnumHandSide.LEFT)
				{
					if (!mainHand.containsKey(p.getEntityId()))
					{
						mainHand.put(p.getEntityId(), p.getHeldItemMainhand());
						p.inventory.mainInventory[p.inventory.currentItem] = null;
					}
				}
				else
				{
					if (!offHand.containsKey(p.getEntityId()))
					{
						offHand.put(p.getEntityId(), p.getHeldItemOffhand());
						//p.inventory.offHandInventory[0] = null;
					}
				}*/
			}
			else if (leftArm != null && (!(renderer instanceof RenderPlayerCyberware) || !((RenderPlayerCyberware) renderer).doCustom))
			{
				renderer.getMainModel().bipedLeftArm.isHidden = true;
			}
			
			if (!hasRightArm)
			{
				renderer.getMainModel().bipedRightArm.isHidden = true;
				
				/*// Hide the main or offhand item if no arm there
				if (mc.gameSettings.mainHand == EnumHandSide.RIGHT)
				{
					if (!mainHand.containsKey(p.getEntityId()))
					{
						mainHand.put(p.getEntityId(), p.getHeldItemMainhand());
						p.inventory.mainInventory[p.inventory.currentItem] = null;
					}
				}
				else
				{
					if (!offHand.containsKey(p.getEntityId()))
					{
						offHand.put(p.getEntityId(), p.getHeldItemOffhand());
						p.inventory.offHandInventory[0] = null;
					}
				}*/
			}
			else if (rightArm != null && (!(renderer instanceof RenderPlayerCyberware) || !((RenderPlayerCyberware) renderer).doCustom))
			{
				renderer.getMainModel().bipedRightArm.isHidden = true;
			}

			


		}
	}
	
	private static Map<Integer, ItemStack> mainHand = new HashMap<Integer, ItemStack>();
	private static Map<Integer, ItemStack> offHand = new HashMap<Integer, ItemStack>();
	
	private static Map<Integer, ItemStack> pants = new HashMap<Integer, ItemStack>();
	private static Map<Integer, ItemStack> shoes = new HashMap<Integer, ItemStack>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleMissingSkin(RenderPlayerEvent.Post event)
	{
		if (CyberwareConfig.RENDER)
		{
			RenderPlayer renderer = event.getRenderer();
			event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
			event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
			event.getRenderer().getMainModel().bipedLeftLeg.isHidden = false;
			event.getRenderer().getMainModel().bipedRightLeg.isHidden = false;
	
			EntityPlayer p = event.getEntityPlayer();
			if (CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData cyberware = CyberwareAPI.getCapability(p);
				
				if (pants.containsKey(p.getEntityId()))
				{
					p.inventory.armorInventory[1] = pants.get(p.getEntityId());
					pants.remove(p.getEntityId());
				}
				
				if (shoes.containsKey(p.getEntityId()))
				{
					p.inventory.armorInventory[0] = shoes.get(p.getEntityId());
					shoes.remove(p.getEntityId());
				}
	
				/*if (!(renderer instanceof RenderPlayerCyberware) || !((RenderPlayerCyberware) renderer).doCustom)
				{
					if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT))
					{
						event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
						if (mc.gameSettings.mainHand == EnumHandSide.LEFT)
						{
							if (mainHand.containsKey(p.getEntityId()))
							{
								p.inventory.mainInventory[p.inventory.currentItem] = mainHand.get(p.getEntityId());
								mainHand.remove(p.getEntityId());
							}
						}
						else
						{
							if (offHand.containsKey(p.getEntityId()))
							{
								p.inventory.offHandInventory[0] = offHand.get(p.getEntityId());
								offHand.remove(p.getEntityId());
							}
						}
						
					}
					
		
					if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT))
					{
						event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
						if (mc.gameSettings.mainHand == EnumHandSide.RIGHT)
						{
							if (mainHand.containsKey(p.getEntityId()))
							{
								p.inventory.mainInventory[p.inventory.currentItem] = mainHand.get(p.getEntityId());
								mainHand.remove(p.getEntityId());
							}
						}
						else
						{
							if (offHand.containsKey(p.getEntityId()))
							{
								p.inventory.offHandInventory[0] = offHand.get(p.getEntityId());
								offHand.remove(p.getEntityId());
							}
						}
						
					}
				}*/
			}
		}
	}
	
	private static boolean missingArm = false;
	private static boolean missingSecondArm = false;
	private static boolean hasReplacementLeft = false;
	private static boolean hasReplacementRight = false;
	private static EnumHandSide oldHand;
	
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleMissingEssentials(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (e != null && e == Minecraft.getMinecraft().thePlayer)
		{
			EntityPlayer p = (EntityPlayer) e;
			BlockPos ping = RFIDRegistry.getSignal(p.worldObj, p.posX, p.posY, p.posZ);
			if (ping != null)
			{
				float pingTime = p.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
				
				double dist = RFIDRegistry.getDist(p.worldObj, p.posX, p.posY, p.posZ, ping);
				
				int extraTimes = 0;
				if (dist < 10F)
				{
				//extraTimes = (int) ((10F - dist) / 3F);
				}
				
				for (int ll = 1; ll < 5; ll++)
				{
					double dist2 = RFIDRegistry.getDist(p.worldObj, p.posX - .5F + ll * p.getLookVec().xCoord, p.posY - .5F + ll * p.getLookVec().yCoord, p.posZ - .5F + ll * p.getLookVec().zCoord, ping);
					if (dist2 < dist)
					{
						dist = dist2;
					}
				}
				
				double squarePlus = Math.min(5, dist) * Math.min(5, dist);
				dist -= Math.min(5, dist);
				dist += squarePlus;
				//System.out.println(dist);
				
				float percentDist = 1F - (float) (dist / 320F);
				float ticksPerPing = 102F - (int) ((percentDist / 2F) * 200F);
				
				totalPing += pingTime - lastPingTime;
				
				
				if (totalPing >= ticksPerPing)
				{
					totalPing = 0;
					
				
					p.playSound(SoundEvents.BLOCK_NOTE_BASS, .5F + (percentDist * .8F), 0F + (percentDist * 2F));
					
					
				}
				
				lastPingTime = pingTime;
			}

			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			boolean stillMissingArm = false;
			boolean stillMissingSecondArm = false;
			
			boolean leftInactive = false;
			boolean leftBad = false;
			ItemStack armLeft = cyberware.getLimb(EnumSlot.ARM, EnumSide.LEFT);
			if (armLeft != null && !((ILimbReplacement) CyberwareAPI.getCyberware(armLeft)).isLimbActive(armLeft))
			{
				leftInactive = true;
			}
			
			if (armLeft != null && !((ILimbReplacement) CyberwareAPI.getCyberware(armLeft)).canHoldItems(armLeft))
			{
				leftBad = true;
			}
			
			boolean rightInactive = false;
			boolean rightBad = false;
			ItemStack armRight = cyberware.getLimb(EnumSlot.ARM, EnumSide.RIGHT);

			if (armRight != null && !((ILimbReplacement) CyberwareAPI.getCyberware(armRight)).isLimbActive(armRight))
			{
				rightInactive = true;
			}
			
			if (armRight != null && !((ILimbReplacement) CyberwareAPI.getCyberware(armRight)).canHoldItems(armRight))
			{
				rightBad = true;
			}
			
			
			boolean hasSkin = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.skinUpgrades, 1, 2));
			hasReplacementLeft = armLeft != null && !hasSkin;
			hasReplacementRight = armRight != null && !hasSkin;
			boolean hasRightArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT) && !rightInactive;
			boolean hasLeftArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT) && !leftInactive;
			
			if (hasRightArm && leftBad)
			{
				hasLeftArm = false;
			}
			else if (hasLeftArm && rightBad)
			{
				hasRightArm = false;
			}
						
			if (!hasRightArm)
			{
				if (hasLeftArm && settings.mainHand != EnumHandSide.LEFT)
				{
					oldHand = settings.mainHand;
					settings.mainHand = EnumHandSide.LEFT;
					settings.sendSettingsToServer();
				}

				if (!missingArm)
				{
					missingArm = true;
				}
				stillMissingArm = true;


				if (!hasLeftArm)
				{
					if (!missingSecondArm)
					{
						missingSecondArm = true;
					}
					stillMissingSecondArm = true;
				}

			}
			else if (!hasLeftArm)
			{
				if (settings.mainHand != EnumHandSide.RIGHT)
				{
					oldHand = settings.mainHand;
					settings.mainHand = EnumHandSide.RIGHT;
					settings.sendSettingsToServer();
				}

				if (!missingArm)
				{
					missingArm = true;
				}
				stillMissingArm = true;
			

				if (!hasRightArm)
				{
					if (!missingSecondArm)
					{
						missingSecondArm = true;
					}
					stillMissingSecondArm = true;
				}

			}

			if (!stillMissingArm && missingArm && oldHand != null)
			{
				missingArm = false;
				settings.mainHand = oldHand;
				settings.sendSettingsToServer();
			
			}

			if (!stillMissingSecondArm)
			{
				missingSecondArm = false;
			}
		}
	}
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public void handleRenderHand(RenderHandEvent event)
	{
		if (CyberwareConfig.RENDER && !(FMLClientHandler.instance().hasOptifine()) && (missingArm || missingSecondArm || hasReplacementLeft || hasReplacementRight))
		{
			float partialTicks = event.getPartialTicks();
			EntityRenderer er = mc.entityRenderer;
			event.setCanceled(true);
			
			
			boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();

			if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
			{
				er.enableLightmap();
				renderItemInFirstPerson(partialTicks);
				er.disableLightmap();
			}
		}
	}
	
	private boolean wasHittingBlock = false;
	
	private void renderItemInFirstPerson(float partialTicks)
	{
		ItemRenderer ir = mc.getItemRenderer();
		AbstractClientPlayer player = mc.thePlayer;
		float f = player.getSwingProgress(partialTicks);
		EnumHand enumhand = (EnumHand)Objects.firstNonNull(player.swingingHand, EnumHand.MAIN_HAND);
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
		boolean flag = true;
		boolean flag1 = true;

		if (player.isHandActive())
		{
			ItemStack itemstack = player.getActiveItemStack();

			if (itemstack != null && itemstack.getItem() == Items.BOW) //Forge: Data watcher can desync and cause this to NPE...
			{
				EnumHand enumhand1 = player.getActiveHand();
				flag = enumhand1 == EnumHand.MAIN_HAND;
				flag1 = !flag;
			}
		}

		rotateArroundXAndY(f1, f2);
		setLightmap();
		rotateArm(partialTicks);
		GlStateManager.enableRescaleNormal();

		ItemStack itemStackMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 3);
		ItemStack itemStackOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 4);
		float equippedProgressMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 5);
		float prevEquippedProgressMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 6);
		float equippedProgressOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 7);
		float prevEquippedProgressOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 8);

		RenderCyberlimbHand.INSTANCE.itemStackMainHand = itemStackMainHand;
		RenderCyberlimbHand.INSTANCE.itemStackOffHand = itemStackOffHand;

		boolean bigArms = player.getSkinType().equals("default");

		ICyberwareUserData cyberware = CyberwareAPI.getCapability(player);
		ItemStack leftArm = cyberware.getLimb(EnumSlot.ARM, EnumSide.LEFT);
		ResourceLocation leftTexture = RenderPlayerCyberware.robo;
		ModelPlayer leftModel = renderF.getMainModel();
		if (CyberwareAPI.isCyberware(leftArm))
		{
			ILimbReplacement lr = ((ILimbReplacement) CyberwareAPI.getCyberware(leftArm));
			leftTexture = lr.getTexture(leftArm);
			leftModel = (ModelPlayer) lr.getModel(leftArm, bigArms, renderF.getMainModel(), renderT.getMainModel(), player);
		}
		 
		ItemStack rightArm = cyberware.getLimb(EnumSlot.ARM, EnumSide.RIGHT);
		ResourceLocation rightTexture = RenderPlayerCyberware.robo;
		ModelPlayer rightModel = renderF.getMainModel();
		if (CyberwareAPI.isCyberware(rightArm))
		{
			ILimbReplacement lr = ((ILimbReplacement) CyberwareAPI.getCyberware(rightArm));
			rightTexture = lr.getTexture(rightArm);
			rightModel = (ModelPlayer) lr.getModel(rightArm, bigArms, renderF.getMainModel(), renderT.getMainModel(), player);
		}
		
		ItemStack mainHand = player.getPrimaryHand() == EnumHandSide.RIGHT ? rightArm : leftArm;
		ItemStack offHand = player.getPrimaryHand() == EnumHandSide.RIGHT ? leftArm : rightArm;
		
		ModelPlayer tempF = renderF.getMainModel();
		renderF.setMainModel(rightModel);
		renderF.texture = rightTexture;
		ModelPlayer tempT = renderT.getMainModel();
		renderT.setMainModel(leftModel);
		renderT.texture = leftTexture;
		RenderCyberlimbHand.INSTANCE.leftReplacement = hasReplacementLeft;
		RenderCyberlimbHand.INSTANCE.rightReplacement = hasReplacementRight;
		
		boolean fg = !(offHand != null && ((ILimbReplacement) CyberwareAPI.getCyberware(offHand)).showAsOffhandIfMainhandEmpty(offHand));
		if (mainHand != null && CyberwareAPI.getCyberware(mainHand) instanceof ItemCyberarmTool)
		{
			if (!wasHittingBlock && Minecraft.getMinecraft().playerController != null)
			{
				if (Minecraft.getMinecraft().playerController.getIsHittingBlock())
				{
					wasHittingBlock = true;
				}
			}
			
			float f4 = fg ? (wasHittingBlock ? 0 : f) : 0;
			
			if (f == 0)
			{
				wasHittingBlock = false;
			}
			float change = fg ? (float) Math.sin(f * Math.PI * 2f) * 0.05F : 0;
			float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(player, partialTicks, f1, EnumHand.MAIN_HAND, f4, null, f5 + change, true);
		}
		else if (flag && !missingSecondArm)
		{
			float f3 = enumhand == EnumHand.MAIN_HAND && fg ? f : 0.0F;
			float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(player, partialTicks, f1, EnumHand.MAIN_HAND, f3, itemStackMainHand, f5);
		}

		if (flag1 && !missingArm)
		{
			float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
			float f6 = 1.0F - (prevEquippedProgressOffHand + (equippedProgressOffHand - prevEquippedProgressOffHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(player, partialTicks, f1, EnumHand.OFF_HAND, f4, itemStackOffHand, f6);
		}
		else if (offHand != null &&
				((ILimbReplacement) CyberwareAPI.getCyberware(offHand)).showAsOffhandIfMainhandEmpty(offHand))
		{
			if (!wasHittingBlock && Minecraft.getMinecraft().playerController != null)
			{
				if (Minecraft.getMinecraft().playerController.getIsHittingBlock())
				{
					wasHittingBlock = true;
				}
			}
			
			float f4 = wasHittingBlock ? 0 : f;
			
			if (f == 0)
			{
				wasHittingBlock = false;
			}

			float change = (float) Math.sin(f * Math.PI * 2f) * 0.10F;
			float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(player, partialTicks, f1, EnumHand.OFF_HAND, f4, null, f5 + change, true);
		}
		
		renderF.setMainModel(tempF);
		renderT.setMainModel(tempT);

		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}
	
	
	private void rotateArroundXAndY(float angle, float angleY)
	{
		GlStateManager.pushMatrix();
		GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
	
	private void setLightmap()
	{
		AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
		int i = this.mc.theWorld.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
		float f = (float)(i & 65535);
		float f1 = (float)(i >> 16);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
	}

	private void rotateArm(float p_187458_1_)
	{
		EntityPlayerSP entityplayersp = this.mc.thePlayer;
		float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_187458_1_;
		float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_187458_1_;
		GlStateManager.rotate((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
	}
	
	@SubscribeEvent
	public void handleWorldUnload(WorldEvent.Unload event)
	{
		if (missingArm)
		{
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			missingArm = false;
			settings.mainHand = oldHand;
		}
	}
	
	
}
