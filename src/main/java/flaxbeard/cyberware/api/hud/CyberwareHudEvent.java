package flaxbeard.cyberware.api.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CyberwareHudEvent extends Event
{
	private List<IHudElement> elements = new ArrayList<IHudElement>();
	private boolean hudjackAvailable;
	private ScaledResolution scaledResolution;

	public CyberwareHudEvent(ScaledResolution scaledResolution, boolean hudjackAvailable)
	{
		super();
		this.scaledResolution = scaledResolution;
		this.hudjackAvailable = hudjackAvailable;
	}
	
	public ScaledResolution getResolution()
	{
		return scaledResolution;
	}
	
	public boolean isHudjackAvailable()
	{
		return hudjackAvailable;
	}
	
	public void setHudjackAvailable(boolean hudjackAvailable)
	{
		this.hudjackAvailable = hudjackAvailable;
	}
	
	public List<IHudElement> getElements()
	{
		return elements;
	}
	
	public void addElement(IHudElement element)
	{
		elements.add(element);
	}
}
