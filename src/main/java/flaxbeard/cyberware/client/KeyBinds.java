package flaxbeard.cyberware.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

public class KeyBinds
{
	public static KeyBinding zoom;
	
	public static void init()
	{
		zoom = new KeyBinding("cyberware.keybinds.zoom", Keyboard.KEY_Z, "cyberware.keybinds.category");
		ClientRegistry.registerKeyBinding(zoom);
	}

}
