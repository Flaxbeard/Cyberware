/*package flaxbeard.cyberware.common.integration.botania;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.subtile.ISubTileContainer;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.item.ItemTwigWand;
import vazkii.botania.common.item.ModItems;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class ItemManaLens extends ItemCyberware
{

	private static Method renderCircle = ReflectionHelper.findMethod(SubTileRadiusRenderHandler.class, null, new String[] { "renderCircle" }, BlockPos.class, Double.class);
	private static Method renderRectangle = ReflectionHelper.findMethod(SubTileRadiusRenderHandler.class, null, new String[] { "renderRectangle" }, AxisAlignedBB.class);

	public ItemManaLens(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public ItemStack[][] required(ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			return new ItemStack[0][0];
		}
		
		return new ItemStack[][] { new ItemStack[] { new ItemStack(CyberwareContent.cybereyes) } };
	}

	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return stack.getItemDamage() == 0 ? other.getItem() == CyberwareContent.cybereyes : false;
	}
	
	private boolean hasLensNotMonocle(EntityPlayer p)
	{
		return !Botania.proxy.isClientPlayerWearingMonocle() && CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this)) || CyberwareAPI.isCyberwareInstalled(p, new ItemStack(this, 1, 1));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		
		if (hasLensNotMonocle(p))
		{
			//ItemMonocle.renderHUD(event.getResolution(), p);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult pos = mc.objectMouseOver;

		if(!hasLensNotMonocle(mc.thePlayer) || pos == null || pos.entityHit != null)
			return;
		BlockPos bPos = pos.getBlockPos();

		ItemStack stackHeld = PlayerHelper.getFirstHeldItem(mc.thePlayer, ModItems.twigWand);
		if(stackHeld != null && ItemTwigWand.getBindMode(stackHeld))
		{
			BlockPos coords = ItemTwigWand.getBoundTile(stackHeld);
			if(coords.getY() != -1)
			{
				bPos = coords;
			}
		}

		TileEntity tile = mc.theWorld.getTileEntity(bPos);
		if(tile == null || !(tile instanceof ISubTileContainer))
			return;
		ISubTileContainer container = (ISubTileContainer) tile;
		SubTileEntity subtile = container.getSubTile();
		if(subtile == null)
			return;
		RadiusDescriptor descriptor = subtile.getRadius();
		if(descriptor == null)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		try
		{
			if(descriptor.isCircle())
					renderCircle.invoke(descriptor.getSubtileCoords(), descriptor.getCircleRadius());
			else renderRectangle.invoke(descriptor.getAABB());
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}
}
*/