package flaxbeard.cyberware.common.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.client.ClientUtils;

public class ItemLungsUpgrades extends ItemCyberware
{

	public ItemLungsUpgrades(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0)))
		{
			GL11.glPushMatrix();
			ItemStack stack = CyberwareAPI.getCyberware(p, new ItemStack(this, 1, 0));
			int air = getAir(stack);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
	
			ScaledResolution res = event.getResolution();
			GlStateManager.enableBlend();
			int left = res.getScaledWidth() / 2 + 91;
			int top = res.getScaledHeight() - 49 - 8;//- right_height;
			
			float r = 1F;
			float b = 1F;
			float g = 1F;

		
			if (p.isInsideOfMaterial(Material.WATER))
			{
				while (air > 0)
				{
					r += 1F;
					b -= .25F;
					g += .25F;
					GL11.glColor3f(r, g, b);
					int drawAir = Math.min(300, air);
					int full = MathHelper.ceiling_double_int((double)(drawAir - 2) * 10.0D / 300.0D);
					int partial = MathHelper.ceiling_double_int((double)drawAir * 10.0D / 300.0D) - full;
			
					for (int i = 0; i < full + partial; ++i)
					{
						ClientUtils.drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
					}
					
					
					air -= 300;
					top -= 8;
				}
			}
		
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			//GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
	}
	
	@SubscribeEvent
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)))
		{
			ItemStack stack = CyberwareAPI.getCyberware(e, new ItemStack(this, 1, 0));
			int air = getAir(stack);
			if (e.getAir() < 300 && air > 0)
			{
				int toAdd = Math.min(300 - e.getAir(), air);
				e.setAir(e.getAir() + toAdd);
				stack.getTagCompound().setInteger("air", air - toAdd);
			}
			else if (e.getAir() == 300 && air < 900)
			{
				stack.getTagCompound().setInteger("air", air + 1);
			}
		}
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 0)) && (e.isSprinting() || e instanceof EntityMob) && !e.isInWater() && e.onGround)
		{
			e.moveRelative(0F, .5F, 0.075F);
		}
	}

	private int getAir(ItemStack stack)
	{
		if (!stack.hasTagCompound())
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("air", 900);
			stack.setTagCompound(tag);
		}
		return stack.getTagCompound().getInteger("air");
	}
}
