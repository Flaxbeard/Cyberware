package flaxbeard.cyberware.api.tablet;

public interface IScrollWheel
{
	public void setScrollAmount(int amount);
	public int getScrollAmount();
	public int getHeight(int height, int ticksOpen, float partialTicks);
}
