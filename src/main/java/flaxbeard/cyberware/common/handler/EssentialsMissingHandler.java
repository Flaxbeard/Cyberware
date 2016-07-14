package flaxbeard.cyberware.common.handler;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.block.tile.TileEntitySurgery;

public class EssentialsMissingHandler
{
	private static final DamageSource brainless = new DamageSource("cyberware.brainless").setDamageBypassesArmor();
	private static final DamageSource heartless = new DamageSource("cyberware.heartless").setDamageBypassesArmor();
	public static final DamageSource surgery = new DamageSource("cyberware.surgery").setDamageBypassesArmor();

	public static final EssentialsMissingHandler INSTANCE = new EssentialsMissingHandler();

	private static Map<EntityLivingBase, Integer> timesLungs = new HashMap<EntityLivingBase, Integer>();

	
	@SubscribeEvent
	public void handleMissingEssentials(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.hasCapability(e))
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			
			if (!cyberware.hasEssential(EnumSlot.CRANIUM))
			{
				e.attackEntityFrom(brainless, Integer.MAX_VALUE);
			}

			if (!cyberware.hasEssential(EnumSlot.HEART))
			{
				e.attackEntityFrom(heartless, Integer.MAX_VALUE);
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
	
	private int getLungsTime(EntityLivingBase e)
	{
		if (!timesLungs.containsKey(e))
		{
			timesLungs.put(e, e.ticksExisted);
		}
		return e.ticksExisted - timesLungs.get(e);
	}
	
	private static final ResourceLocation BLACK_PX = new ResourceLocation(Cyberware.MODID + ":textures/gui/blackpx.png");

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void overlayPre(RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() == ElementType.ALL)
		{
			EntityPlayer e = Minecraft.getMinecraft().thePlayer;
			
			if (CyberwareAPI.hasCapability(e))
			{
				ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
				
				if (!cyberware.hasEssential(EnumSlot.EYES))
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

	
	
}
