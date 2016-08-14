package flaxbeard.cyberware.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.client.KeyBinds;
import flaxbeard.cyberware.client.gui.GuiCyberwareMenu;

public class CyberwareMenuHandler
{
	public static final CyberwareMenuHandler INSTANCE = new CyberwareMenuHandler();
	private Minecraft mc = Minecraft.getMinecraft();

	int wasInScreen = 0;
	int lastHotbarSlot = 0;
	public static boolean wasSprinting = false;
	boolean[] hotbarSlots = new boolean[10];
	
	@SubscribeEvent
	public void tick(ClientTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			if (!mc.gameSettings.isKeyDown(KeyBinds.menu) && mc.currentScreen == null && wasInScreen > 0)
			{
				KeyConflictContext inGame = KeyConflictContext.IN_GAME;
				mc.gameSettings.keyBindForward.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindLeft.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindBack.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindRight.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindJump.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindSneak.setKeyConflictContext(inGame);
				mc.gameSettings.keyBindSprint.setKeyConflictContext(inGame);
				
				if (wasSprinting)
				{
					mc.thePlayer.setSprinting(wasSprinting);
				}
				wasInScreen--;
			}

		}
		if(event.phase == Phase.END)
		{
			
			if (mc.thePlayer != null)
			{

				for (int i = 0; i < mc.gameSettings.keyBindsHotbar.length; i++)
				{
					KeyBinding kb = mc.gameSettings.keyBindsHotbar[i];
					boolean pressed = kb.isKeyDown();
					if (mc.gameSettings.keyBindSneak.isKeyDown())
					{
						ItemStack hki = CyberwareAPI.getCapability(mc.thePlayer).getHotkey(i);
						if (pressed && !hotbarSlots[i] && hki != null)
						{
							ReflectionHelper.setPrivateValue(EntityLivingBase.class, mc.thePlayer, 100, 22);
							mc.thePlayer.inventory.currentItem = lastHotbarSlot;
							ClientUtils.useActiveItemClient(mc.thePlayer, hki);
						}
					}
					hotbarSlots[i] = pressed;
				}
			
				
				lastHotbarSlot = mc.thePlayer.inventory.currentItem;
			}
			
			if (mc.thePlayer != null && CyberwareAPI.getCapability(mc.thePlayer).getNumActiveItems() > 0 && mc.gameSettings.isKeyDown(KeyBinds.menu) && mc.currentScreen == null)
			{

				KeyConflictContext gui = KeyConflictContext.GUI;
				mc.gameSettings.keyBindForward.setKeyConflictContext(gui);
				mc.gameSettings.keyBindLeft.setKeyConflictContext(gui);
				mc.gameSettings.keyBindBack.setKeyConflictContext(gui);
				mc.gameSettings.keyBindRight.setKeyConflictContext(gui);
				mc.gameSettings.keyBindJump.setKeyConflictContext(gui);
				mc.gameSettings.keyBindSneak.setKeyConflictContext(gui);
				mc.gameSettings.keyBindSprint.setKeyConflictContext(gui);
				
				mc.displayGuiScreen(new GuiCyberwareMenu());
				wasInScreen = 5;
			}
			else if (wasInScreen > 0 && mc.currentScreen instanceof GuiCyberwareMenu)
			{
				wasSprinting = mc.thePlayer.isSprinting();
			}
			
		}
	}
}
