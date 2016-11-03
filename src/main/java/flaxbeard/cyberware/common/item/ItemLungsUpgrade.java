package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;

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
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemLungsUpgrade extends ItemCyberware
{

	public ItemLungsUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() == ElementType.AIR)
		{
			EntityPlayer p = Minecraft.getMinecraft().thePlayer;
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0)) && !p.isCreative())
			{
				GlStateManager.pushMatrix();
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
						GlStateManager.color(r, g, b);
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
			
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				//GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}
	
	private Map<Integer, Boolean> lastOxygen = new HashMap<Integer, Boolean>();
	
	@SubscribeEvent
	public void handleLivingUpdate(CyberwareUpdateEvent event)
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
				CyberwareAPI.getCyberwareNBT(stack).setInteger("air", air - toAdd);
			}
			else if (e.getAir() == 300 && air < 900)
			{
				CyberwareAPI.getCyberwareNBT(stack).setInteger("air", air + 1);
			}
		}
		


		ItemStack test = new ItemStack(this, 1, 1);
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{
			if ((e.isSprinting() || e instanceof EntityMob) && !e.isInWater() && e.onGround)
			{
				boolean last = getLastOxygen(e);

				int ranks = CyberwareAPI.getCyberwareRank(e, test);
				test.stackSize = ranks;
				boolean powerUsed = e.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)) : last;
				
				if (powerUsed)
				{
					e.moveRelative(0F, .2F * ranks, 0.075F);
				}
				
				lastOxygen.put(e.getEntityId(), powerUsed);
			}
		}
	}
	
	private boolean getLastOxygen(EntityLivingBase e)
	{
		if (!lastOxygen.containsKey(e.getEntityId()))
		{
			lastOxygen.put(e.getEntityId(), true);
		}
		return lastOxygen.get(e.getEntityId());
	}
	
	@Override
	public int installedStackSize(ItemStack stack)
	{
		return stack.getItemDamage() == 1 ? 3 : 1;
	}

	private int getAir(ItemStack stack)
	{
		NBTTagCompound data = CyberwareAPI.getCyberwareNBT(stack);
		if (!data.hasKey("air"))
		{
			data.setInteger("air", 900);
		}
		return data.getInteger("air");
	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 1 ? LibConstants.HYPEROXYGENATION_CONSUMPTION * stack.stackSize : 0;
	}
	
	@Override
	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		if (stack.getItemDamage() == 1)
		{
			switch (stack.stackSize)
			{
				case 1:
					return 2;
				case 2:
					return 4;
				case 3:
					return 5;
			}
		}
		return super.getUnmodifiedEssenceCost(stack);
	}
}
