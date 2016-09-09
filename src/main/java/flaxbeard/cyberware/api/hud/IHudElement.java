package flaxbeard.cyberware.api.hud;

import flaxbeard.cyberware.client.gui.hud.HudNBTData;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;

public interface IHudElement
{
	public static enum EnumAnchorHorizontal
	{
		LEFT,
		RIGHT;
	}
	
	public static enum EnumAnchorVertical
	{
		TOP,
		BOTTOM;
	}
	
	public void render(EntityPlayer player, ScaledResolution resolution, boolean hudjackAvailable, boolean isConfigOpen, float partialTicks);
	
	public boolean canMove();
	
	public void setX(int x);
	public void setY(int y);
	public int getX();
	public int getY();
	
	public int getWidth();
	public int getHeight();
	
	public boolean canHide();
	public void setHidden(boolean hidden);
	public boolean isHidden();
	
	public EnumAnchorHorizontal getHorizontalAnchor();
	public void setHorizontalAnchor(EnumAnchorHorizontal anchor);
	
	public EnumAnchorVertical getVerticalAnchor();
	public void setVerticalAnchor(EnumAnchorVertical anchor);

	public void reset();
	
	public String getUniqueName();

	public void save(IHudSaveData data);
	public void load(IHudSaveData data);
	
}
