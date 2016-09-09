package flaxbeard.cyberware.api.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CyberwareHudDataEvent extends Event
{
	private List<IHudElement> elements = new ArrayList<IHudElement>();

	public CyberwareHudDataEvent()
	{
		super();
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
