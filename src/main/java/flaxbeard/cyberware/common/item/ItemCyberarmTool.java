package flaxbeard.cyberware.common.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb;
import flaxbeard.cyberware.api.item.ILimbReplacement;
import flaxbeard.cyberware.client.render.ModelDrill;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;

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
		
		if (ware instanceof ISidedLimb)
		{
			return ware.isEssential(other) && ((ISidedLimb) ware).getSide(other) == this.getSide(stack);
		}
		return false;
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
		
		for (int i = 0; i < 4; i++)
		{
			ItemStack test = new ItemStack(this, 1, i);
			ItemStack installed = CyberwareAPI.getCyberware(e, test);
			if (e.ticksExisted % 20 == 0 && installed != null)
			{
				boolean used = CyberwareAPI.getCapability(e).usePower(installed, getPowerConsumption(installed));
				
				CyberwareAPI.getCyberwareNBT(installed).setBoolean("active", used);
			}
		}

	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return LibConstants.LIMB_CONSUMPTION;
	}
	
	@Override
	public boolean isActive(ItemStack stack)
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
	
	@SideOnly(Side.CLIENT)
	private static ModelPlayer model = new ModelDrill();
	
	@SideOnly(Side.CLIENT)
	private static final ResourceLocation texture = new ResourceLocation(Cyberware.MODID + ":textures/models/playerDrillArm.png");
	
	@Override
	public ResourceLocation getTexture(ItemStack stack)
	{
		return texture;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelPlayer getModel(ItemStack itemStack, boolean wideArms, ModelPlayer baseWide, ModelPlayer baseSkinny)
	{
		if (Minecraft.getMinecraft().thePlayer.isSneaking())
		{
			model = new ModelDrill();

		}
		if (Minecraft.getMinecraft().thePlayer.getSwingProgress(Minecraft.getMinecraft().getRenderPartialTicks()) > 0)
		{
			((ModelDrill)model).rBit1.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).rBit2.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).rBit3.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).rBit4.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).lBit1.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).lBit2.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).lBit3.rotateAngleY = 0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
			((ModelDrill)model).lBit4.rotateAngleY = -0.5F * (Minecraft.getMinecraft().thePlayer.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks());
		}
		return model;
	}
	
	@Override
	public boolean showAsOffhandIfMainhandEmpty(ItemStack stack)
	{
		return true;
	}
	
	boolean proc = false;
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleBreakSpeed(PlayerEvent.BreakSpeed event)
	{
		if (!proc)
		{
			EntityPlayer p = event.getEntityPlayer();
			int numInstalled = 0;
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0))) numInstalled++;
			if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 1))) numInstalled++;
			if (numInstalled == 2 || (numInstalled > 0 && p.getHeldItemMainhand() == null))
			{
				proc = true;

				ItemStack store = p.getHeldItemMainhand();
				p.inventory.mainInventory[p.inventory.currentItem] = new ItemStack(Items.APPLE);
				float bspeed = p.getDigSpeed(event.getState(), event.getPos());
				p.inventory.mainInventory[p.inventory.currentItem] = new ItemStack(Items.IRON_PICKAXE);
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
	
	@SubscribeEvent
	public void handleMining(HarvestCheck event)
	{
		EntityPlayer p = event.getEntityPlayer();
		int numInstalled = 0;
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 0))) numInstalled++;
		if (CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 1))) numInstalled++;
		if (numInstalled == 2 || (numInstalled > 0 && p.getHeldItemMainhand() == null))
		{
			ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
			if (pick.canHarvestBlock(event.getTargetBlock()))
			{
				event.setCanHarvest(true);
			}
		}
	}

}
