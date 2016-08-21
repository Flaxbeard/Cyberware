package flaxbeard.cyberware.client;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.item.ItemArmorCyberware.ModelTrenchcoat;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.TriggerActiveAbilityPacket;

public class ClientUtils
{
	@SideOnly(Side.CLIENT)
	public static final ModelBiped armor = new ModelBiped(0.51F);
	
	@SideOnly(Side.CLIENT)
	public static final ModelBiped trench = new ModelTrenchcoat(0.51F);
	
	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
	{
		float zLevel = -10;
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
		tessellator.draw();
	}
	
	private static HashMap<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();

	public static void bindTexture(String string)
	{
		if (!textures.containsKey(string))
		{
			textures.put(string, new ResourceLocation(string));
			System.out.println("[Cyberware] Registering new ResourceLocation: " + string);
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(textures.get(string));
	}
	
	public static void drawHoveringText(GuiScreen gui, List<String> textLines, int x, int y, FontRenderer font)
	{
		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, gui.width, gui.height, -1, font);
	}
	
	
	public static void useActiveItemClient(Entity entity, ItemStack stack)
	{
		CyberwarePacketHandler.INSTANCE.sendToServer(new TriggerActiveAbilityPacket(stack));
		CyberwareAPI.useActiveItem(entity, stack);
	}
}
