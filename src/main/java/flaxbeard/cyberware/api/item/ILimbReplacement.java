package flaxbeard.cyberware.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ILimbReplacement
{

	public boolean isLimbActive(ItemStack stack);
	
	default boolean canInteract(ItemStack stack) { return isLimbActive(stack); };
	
	default boolean canHoldItems(ItemStack stack) { return isLimbActive(stack); };

	default boolean canMine(ItemStack stack) { return isLimbActive(stack); };

	public ResourceLocation getTexture(ItemStack stack);

	public Object getModel(ItemStack itemStack, boolean wideArms, Object baseWide, Object baseSkinny, EntityPlayer player);

	default boolean showAsOffhandIfMainhandEmpty(ItemStack stack) { return false; }
}
