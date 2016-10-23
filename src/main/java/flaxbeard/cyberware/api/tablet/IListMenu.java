package flaxbeard.cyberware.api.tablet;

import java.util.List;

import flaxbeard.cyberware.client.gui.GuiTablet;
import net.minecraft.client.gui.FontRenderer;

public interface IListMenu
{
	public static interface IListMenuItem
	{
		public void render(GuiTablet tablet, int x, int y, boolean hovered);

		public void renderText(GuiTablet tablet, int x, int y, boolean hovered);
	}
	
	public List<IListMenuItem> getItems();
	public void addItem(IListMenuItem item);
	public void removeItem(IListMenuItem item);
}
