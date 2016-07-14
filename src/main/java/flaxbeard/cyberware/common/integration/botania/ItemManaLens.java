package flaxbeard.cyberware.common.integration.botania;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.subtile.ISubTileContainer;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.client.core.handler.ClientMethodHandles;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.item.ItemTwigWand;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemMonocle;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberware;

public class ItemManaLens extends ItemCyberware
{

	public ItemManaLens(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public ItemStack[] required(ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			return new ItemStack[0];
		}
		
		return new ItemStack[] { new ItemStack(CyberwareContent.cybereyes) };
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
			ItemMonocle.renderHUD(event.getResolution(), p);
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

		if(descriptor.isCircle())
			renderCircle(descriptor.getSubtileCoords(), descriptor.getCircleRadius());
		else renderRectangle(descriptor.getAABB());

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}
	
	private void renderRectangle(AxisAlignedBB aabb)
	{
		double renderPosX, renderPosY, renderPosZ;

		try
		{
			renderPosX = (double) ClientMethodHandles.renderPosX_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
			renderPosY = (double) ClientMethodHandles.renderPosY_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
			renderPosZ = (double) ClientMethodHandles.renderPosZ_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
		}
		catch (Throwable t)
		{
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(aabb.minX - renderPosX, aabb.minY - renderPosY, aabb.minZ - renderPosZ);
		int color = Color.HSBtoRGB(ClientTickHandler.ticksInGame % 200 / 200F, 0.6F, 1F);

		Color colorRGB = new Color(color);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 32);

		double f = 1F / 16F;
		double x = aabb.maxX - aabb.minX - f;
		double z = aabb.maxZ - aabb.minZ - f;

		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		tessellator.getBuffer().pos(x, f, f).endVertex();
		tessellator.getBuffer().pos(f, f, f).endVertex();
		tessellator.getBuffer().pos(f, f, z).endVertex();
		tessellator.getBuffer().pos(x, f, z).endVertex();
		tessellator.draw();

		x += f;
		z += f;
		double f1 = f + f / 4F;
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 64);
		tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		tessellator.getBuffer().pos(x, f1, 0).endVertex();
		tessellator.getBuffer().pos(0, f1, 0).endVertex();
		tessellator.getBuffer().pos(0, f1, z).endVertex();
		tessellator.getBuffer().pos(x, f1, z).endVertex();
		tessellator.draw();

		GL11.glColor4ub(((byte) 255), ((byte) 255), ((byte) 255), ((byte) 255));
		GlStateManager.popMatrix();
	}

	private void renderCircle(BlockPos center, double radius)
	{
		double renderPosX, renderPosY, renderPosZ;

		try
		{
			renderPosX = (double) ClientMethodHandles.renderPosX_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
			renderPosY = (double) ClientMethodHandles.renderPosY_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
			renderPosZ = (double) ClientMethodHandles.renderPosZ_getter.invokeExact(Minecraft.getMinecraft().getRenderManager());
		}
		catch (Throwable t)
		{
			return;
		}

		GlStateManager.pushMatrix();
		double x = center.getX() + 0.5;
		double y = center.getY();
		double z = center.getZ() + 0.5;
		GlStateManager.translate(x - renderPosX, y - renderPosY, z - renderPosZ);
		int color = Color.HSBtoRGB(ClientTickHandler.ticksInGame % 200 / 200F, 0.6F, 1F);

		Color colorRGB = new Color(color);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 32);

		double f = 1F / 16F;

		int totalAngles = 360;
		int drawAngles = 360;
		int step = totalAngles / drawAngles;

		radius -= f;
		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
		tessellator.getBuffer().pos(0, f, 0).endVertex();
		for (int i = 0; i < totalAngles + 1; i += step)
		{
			double rad = (totalAngles - i) * Math.PI / 180.0;
			double xp = Math.cos(rad) * radius;
			double zp = Math.sin(rad) * radius;
			tessellator.getBuffer().pos(xp, f, zp).endVertex();
		}
		tessellator.getBuffer().pos(0, f, 0).endVertex();
		tessellator.draw();

		radius += f;
		double f1 = f + f / 4F;
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 64);
		tessellator.getBuffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
		tessellator.getBuffer().pos(0, f1, 0).endVertex();
		for (int i = 0; i < totalAngles + 1; i += step)
		{
			double rad = (totalAngles - i) * Math.PI / 180.0;
			double xp = Math.cos(rad) * radius;
			double zp = Math.sin(rad) * radius;
			tessellator.getBuffer().pos(xp, f1, zp).endVertex();
		}
		tessellator.getBuffer().pos(0, f1, 0).endVertex();
		tessellator.draw();
		GL11.glColor4ub(((byte) 255), ((byte) 255), ((byte) 255), ((byte) 255));
		GlStateManager.popMatrix();
	}
}
