package flaxbeard.cyberware.client;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ClientUtils
{
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
}
