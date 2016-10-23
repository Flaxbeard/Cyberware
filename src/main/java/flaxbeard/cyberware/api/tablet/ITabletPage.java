package flaxbeard.cyberware.api.tablet;

import flaxbeard.cyberware.client.gui.GuiTablet;

public interface ITabletPage
{
	public void render(GuiTablet tablet, int width, int height, int mouseX, int mouseY, int ticksOpen, float partialTicks);
	public int getWidth(int defaultWidth, int ticksOpen, float partialTicks);
	public ITabletPage getParent();
	public boolean leftButtonOn(int ticksOpen, float partialTicks);
	public boolean rightButtonOn(int ticksOpen, float partialTicks);
}
