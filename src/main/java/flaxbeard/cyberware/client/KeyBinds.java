package flaxbeard.cyberware.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import org.lwjgl.input.Keyboard;

import flaxbeard.cyberware.client.gui.GuiCyberwareMenu;

public class KeyBinds
{
	public static KeyBinding menu;

	public static void init()
	{
		menu = new KeyBinding("cyberware.keybinds.menu", Keyboard.KEY_R, "cyberware.keybinds.category");
		ClientRegistry.registerKeyBinding(menu);
	}


}
