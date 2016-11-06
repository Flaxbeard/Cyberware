package flaxbeard.cyberware.api.tablet;

import flaxbeard.cyberware.client.gui.GuiTablet;

public interface IScrollWheel
{
	public void setScrollAmount(int amount);
	public int getScrollAmount();
	public int getHeight(GuiTablet guiTablet, int width, int height, int ticksOpen, float partialTicks);
}
