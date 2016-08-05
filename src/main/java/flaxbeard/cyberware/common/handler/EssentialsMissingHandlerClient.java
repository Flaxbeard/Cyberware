package flaxbeard.cyberware.common.handler;

import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Objects;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.client.render.RenderCyberlimbHand;
import flaxbeard.cyberware.client.render.RenderPlayerCyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberlimb;

public class EssentialsMissingHandlerClient
{
	public static final EssentialsMissingHandlerClient INSTANCE = new EssentialsMissingHandlerClient();

	@SideOnly(Side.CLIENT)
	private static final RenderPlayerCyberware renderT = new RenderPlayerCyberware(Minecraft.getMinecraft().getRenderManager(), true);

	@SideOnly(Side.CLIENT)
	public static final RenderPlayerCyberware renderF = new RenderPlayerCyberware(Minecraft.getMinecraft().getRenderManager(), false);
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleMissingSkin(RenderPlayerEvent.Pre event)
	{
		
		EntityPlayer p = event.getEntityPlayer();
		if (CyberwareAPI.hasCapability(p))
		{	
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(p);
			boolean hasLeftLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.LEFT);
			boolean hasRightLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.RIGHT);
			boolean hasLeftArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT);
			boolean hasRightArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT);
			
			boolean robotLeftArm = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.cyberlimbs, 1, 0));
			boolean robotRightArm = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.cyberlimbs, 1, 1));
			boolean robotLeftLeg = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.cyberlimbs, 1, 2));
			boolean robotRightLeg = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.cyberlimbs, 1, 3));

			
			if (!(event.getRenderer() instanceof RenderPlayerCyberware))
			{
				boolean bigArms = ReflectionHelper.getPrivateValue(RenderPlayer.class, event.getRenderer(), 0);

				
				
				if (!cyberware.hasEssential(EnumSlot.SKIN))
				{
					event.setCanceled(true);
									
					renderF.doMuscles = true;
					renderF.doRender((AbstractClientPlayer) p, event.getX(), event.getY(), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
					renderF.doMuscles = false;
				}
				

				boolean lower = false;
				if (!hasRightLeg && !hasLeftLeg)
				{
					// Hide pants + shoes
					pants.put(p, p.inventory.armorInventory[1]);
					p.inventory.armorInventory[1] = null;
					shoes.put(p, p.inventory.armorInventory[0]);
					p.inventory.armorInventory[0] = null;
					lower = true;
				}
				
				if (!hasRightLeg || !hasLeftLeg || !hasRightArm || !hasLeftArm || robotLeftArm || robotRightArm || robotLeftLeg || robotRightLeg)
				{
					event.setCanceled(true);

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
						ModelPlayer mp = renderF.getMainModel();
						mp.bipedBody.isHidden = true;
						mp.bipedHead.isHidden = true;
						mp.bipedLeftArm.isHidden = !robotLeftArm;
						mp.bipedRightArm.isHidden = !robotRightArm;
						mp.bipedLeftLeg.isHidden = !robotLeftLeg;
						mp.bipedRightLeg.isHidden = !robotRightLeg;
						
						if (bigArms)
						{
							renderT.doRobo = true;
							renderT.doRender((AbstractClientPlayer) p, event.getX(), event.getY() - (lower ? (11F / 16F) : 0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
							renderT.doRobo = false;
						}
						else
						{
							renderF.doRobo = true;
							renderF.doRender((AbstractClientPlayer) p, event.getX(), event.getY() - (lower ? (11F / 16F) : 0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
							renderF.doRobo = false;
						}
						
						mp.bipedBody.isHidden = false;
						mp.bipedHead.isHidden = false;
						mp.bipedLeftArm.isHidden = false;
						mp.bipedRightArm.isHidden = false;
						mp.bipedLeftLeg.isHidden = false;
						mp.bipedRightLeg.isHidden = false;
					}
				
				}
			}
			
			RenderPlayer renderer = event.getRenderer();

			


			if (!hasLeftLeg)
			{
				renderer.getMainModel().bipedLeftLeg.isHidden = true;
			}
			
			if (!hasRightLeg)
			{
				renderer.getMainModel().bipedRightLeg.isHidden = true;
			}

			
			if (!hasLeftArm)
			{
				renderer.getMainModel().bipedLeftArm.isHidden = true;
				
				// Hide the main or offhand item if no arm there
				if (!mainHand.containsKey(p))
				{
					mainHand.put(p, p.getHeldItemMainhand());
					offHand.put(p, p.getHeldItemOffhand());
				}
				if (mc.gameSettings.mainHand == EnumHandSide.LEFT)
				{
					p.inventory.mainInventory[p.inventory.currentItem] = null;
				}
				else
				{
					p.inventory.offHandInventory[0] = null;
				}
			}
			
			if (!hasRightArm)
			{
				renderer.getMainModel().bipedRightArm.isHidden = true;
				
				// Hide the main or offhand item if no arm there
				if (!mainHand.containsKey(p))
				{
					mainHand.put(p, p.getHeldItemMainhand());
					offHand.put(p, p.getHeldItemOffhand());
				}
				if (mc.gameSettings.mainHand == EnumHandSide.RIGHT)
				{
					p.inventory.mainInventory[p.inventory.currentItem] = null;
				}
				else
				{
					p.inventory.offHandInventory[0] = null;
				}
			}
			

			


		}
	}
	
	private static Map<EntityPlayer, ItemStack> mainHand = new HashMap<EntityPlayer, ItemStack>();
	private static Map<EntityPlayer, ItemStack> offHand = new HashMap<EntityPlayer, ItemStack>();
	
	private static Map<EntityPlayer, ItemStack> pants = new HashMap<EntityPlayer, ItemStack>();
	private static Map<EntityPlayer, ItemStack> shoes = new HashMap<EntityPlayer, ItemStack>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleMissingSkin(RenderPlayerEvent.Post event)
	{
		event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
		event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
		event.getRenderer().getMainModel().bipedLeftLeg.isHidden = false;
		event.getRenderer().getMainModel().bipedRightLeg.isHidden = false;

		EntityPlayer p = event.getEntityPlayer();
		if (CyberwareAPI.hasCapability(p))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(p);
			
			if (pants.containsKey(p))
			{
				p.inventory.armorInventory[1] = pants.get(p);
				pants.remove(p);
			}
			
			if (shoes.containsKey(p))
			{
				p.inventory.armorInventory[0] = shoes.get(p);
				shoes.remove(p);
			}

			if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT))
			{
				event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
				if (mainHand.containsKey(p))
				{
					p.inventory.mainInventory[p.inventory.currentItem] = mainHand.get(p);
					p.inventory.offHandInventory[0] = offHand.get(p);
					mainHand.remove(p);
					offHand.remove(p);
				}
			}
			

			if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT))
			{
				event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
				if (mainHand.containsKey(p))
				{
					p.inventory.mainInventory[p.inventory.currentItem] = mainHand.get(p);
					p.inventory.offHandInventory[0] = offHand.get(p);
					mainHand.remove(p);
					offHand.remove(p);
				}
			}

		}
	}
	
	private static boolean missingArm = false;
	private static boolean missingSecondArm = false;
	private static boolean hasRoboLeft = false;
	private static boolean hasRoboRight = false;
	private static EnumHandSide oldHand;
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleMissingEssentials(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (e != null && e == Minecraft.getMinecraft().thePlayer)
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			boolean stillMissingArm = false;
			boolean stillMissingSecondArm = false;
			
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
			
			
			boolean hasSkin = cyberware.isCyberwareInstalled(new ItemStack(CyberwareContent.skinUpgrades, 1, 2));
			hasRoboLeft = armLeft != null && !hasSkin;
			hasRoboRight = armRight != null && !hasSkin;
			boolean hasRightArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT) && !rightUnpowered;
			boolean hasLeftArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT) && !leftUnpowered;
			
			if (!hasRightArm)
			{
				if (settings.mainHand != EnumHandSide.LEFT)
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

			if (!stillMissingArm && oldHand != null)
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
		if (missingArm || missingSecondArm || hasRoboLeft || hasRoboRight)
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
	
	private void renderItemInFirstPerson(float partialTicks)
	{
		ItemRenderer ir = mc.getItemRenderer();
		AbstractClientPlayer abstractclientplayer = mc.thePlayer;
		float f = abstractclientplayer.getSwingProgress(partialTicks);
		EnumHand enumhand = (EnumHand)Objects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
		float f1 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
		float f2 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
		boolean flag = true;
		boolean flag1 = true;

		if (abstractclientplayer.isHandActive())
		{
			ItemStack itemstack = abstractclientplayer.getActiveItemStack();

			if (itemstack != null && itemstack.getItem() == Items.BOW) //Forge: Data watcher can desync and cause this to NPE...
			{
				EnumHand enumhand1 = abstractclientplayer.getActiveHand();
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

		if (flag && !missingSecondArm)
		{
			float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
			float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.leftRobot = hasRoboLeft;
			RenderCyberlimbHand.INSTANCE.rightRobot = hasRoboRight;
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.MAIN_HAND, f3, itemStackMainHand, f5);
		}

		if (flag1 && !missingArm)
		{
			float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
			float f6 = 1.0F - (prevEquippedProgressOffHand + (equippedProgressOffHand - prevEquippedProgressOffHand) * partialTicks);
			RenderCyberlimbHand.INSTANCE.leftRobot = hasRoboLeft;
			RenderCyberlimbHand.INSTANCE.rightRobot = hasRoboRight;
			RenderCyberlimbHand.INSTANCE.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.OFF_HAND, f4, itemStackOffHand, f6);
		}

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
