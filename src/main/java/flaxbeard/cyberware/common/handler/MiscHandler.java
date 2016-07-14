package flaxbeard.cyberware.common.handler;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware;

public class MiscHandler
{
	public static final MiscHandler INSTANCE = new MiscHandler();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public void handleCyberwareTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		if (CyberwareAPI.isCyberware(stack))
		{
			ICyberware ware = CyberwareAPI.getCyberware(stack);
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			if (settings.isKeyDown(settings.keyBindSneak))
			{
				List<String> info = ware.getInfo(stack);
				if (info != null)
				{
					event.getToolTip().addAll(info);
				}
				
				ItemStack[] reqs = ware.required(stack);
				if (reqs.length > 0)
				{
					String joined = I18n.format(reqs[0].getUnlocalizedName() + ".name");
					for (int i = 1; i < reqs.length; i++)
					{
						joined += I18n.format("cyberware.tooltip.joiner") + " " + I18n.format(reqs[i].getUnlocalizedName() + ".name");
					}
					event.getToolTip().add(ChatFormatting.AQUA + I18n.format("cyberware.tooltip.requires") + " "
							+ joined);
				}
				event.getToolTip().add(ChatFormatting.RED + I18n.format("cyberware.slot." + ware.getSlot(stack).getName()));
			}
			else
			{
				event.getToolTip().add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.shiftPrompt"));
			}
		}
	}
}
