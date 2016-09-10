package flaxbeard.cyberware.common.item;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.CyberwareContent;

public class ItemArmorCyberware extends ItemArmor implements IDeconstructable
{
	public static class ModelTrenchcoat extends ModelBiped
	{
		public ModelRenderer bottomThing;
		
		public ModelTrenchcoat(float modelSize)
		{
			super(modelSize);
			this.bottomThing = new ModelRenderer(this, 16, 0);
			this.bottomThing.addBox(-4.0F, 0F, -1.7F, 8, 12, 4, modelSize);
			this.bottomThing.setRotationPoint(0, 12.0F, 0.0F);
		}
		
		
		@Override
		public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
		{
			super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
			
			this.bottomThing.setRotationPoint(0, this.bipedLeftLeg.rotationPointY, this.bipedLeftLeg.rotationPointZ);
			this.bottomThing.rotateAngleX = Math.max(this.bipedLeftLeg.rotateAngleX, this.bipedRightLeg.rotateAngleX) + .05F * 1.1F;
		}
		
		@Override
		public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
		{
			super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.pushMatrix();

			if (this.isChild)
			{
				float f = 2.0F;
				GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
				GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
				this.bottomThing.render(scale);
		
			}
			else
			{
				if (entityIn.isSneaking())
				{
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}

				this.bottomThing.render(scale);
			}

			GlStateManager.popMatrix();
		}
	}

	
	public ItemArmorCyberware(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(materialIn, renderIndexIn, equipmentSlotIn);
		
		this.setRegistryName(name);
		GameRegistry.register(this);
		this.setUnlocalizedName(Cyberware.MODID + "." + name);
		
		this.setCreativeTab(Cyberware.creativeTab);
				

		CyberwareContent.items.add(this);
	}

	@Override
	public boolean canDestroy(ItemStack stack)
	{
		return true;
	}

	@Override
	public ItemStack[] getComponents(ItemStack stack)
	{
		Item i = stack.getItem();
		
		if (i == CyberwareContent.trenchcoat)
		{
			return new ItemStack[]
					{
						new ItemStack(CyberwareContent.component, 2, 2),
						new ItemStack(Items.LEATHER, 12, 0),
						new ItemStack(Items.DYE, 1, 0)
					};
		}
		else if (i == CyberwareContent.jacket)
		{
			return new ItemStack[]
					{
						new ItemStack(CyberwareContent.component, 1, 2),
						new ItemStack(Items.LEATHER, 8, 0),
						new ItemStack(Items.DYE, 1, 0)
					};
		}
		return new ItemStack[]
				{
					new ItemStack(Blocks.STAINED_GLASS, 4, 15),
					new ItemStack(CyberwareContent.component, 1, 4)
				};
	}



	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
	{
		ClientUtils.trench.setModelAttributes(_default);
		ClientUtils.armor.setModelAttributes(_default);
		ClientUtils.trench.bipedRightArm.isHidden = !(entityLiving instanceof EntityPlayer) && !(entityLiving instanceof EntityArmorStand);
		ClientUtils.trench.bipedLeftArm.isHidden = !(entityLiving instanceof EntityPlayer) && !(entityLiving instanceof EntityArmorStand);
		ClientUtils.armor.bipedRightArm.isHidden = ClientUtils.trench.bipedRightArm.isHidden;
		ClientUtils.armor.bipedLeftArm.isHidden = ClientUtils.trench.bipedLeftArm.isHidden;

		if (itemStack != null && itemStack.getItem() == CyberwareContent.trenchcoat) return ClientUtils.trench;
		
		return ClientUtils.armor;
	}
	
	public boolean hasColor(ItemStack stack)
	{
		if (this.getArmorMaterial() != CyberwareContent.trenchMat)
		{
			return false;
		}
		else
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound();
			return nbttagcompound != null && nbttagcompound.hasKey("display", 10) ? nbttagcompound.getCompoundTag("display").hasKey("color", 3) : false;
		}
	}
	
	public int getColor(ItemStack stack)
	{
		if (this.getArmorMaterial() != CyberwareContent.trenchMat)
		{
			return 16777215;
		}
		else
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound();

			if (nbttagcompound != null)
			{
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

				if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3))
				{
					return nbttagcompound1.getInteger("color");
				}
			}

			return 0x333333; // 0x664028
		}
	}

	public void removeColor(ItemStack stack)
	{
		if (this.getArmorMaterial() == CyberwareContent.trenchMat)
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound();

			if (nbttagcompound != null)
			{
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

				if (nbttagcompound1.hasKey("color"))
				{
					nbttagcompound1.removeTag("color");
				}
			}
		}
	}

	public void setColor(ItemStack stack, int color)
	{
		if (this.getArmorMaterial() != CyberwareContent.trenchMat)
		{
			throw new UnsupportedOperationException("Can\'t dye non-leather!");
		}
		else
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound();

			if (nbttagcompound == null)
			{
				nbttagcompound = new NBTTagCompound();
				stack.setTagCompound(nbttagcompound);
			}

			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

			if (!nbttagcompound.hasKey("display", 10))
			{
				nbttagcompound.setTag("display", nbttagcompound1);
			}

			nbttagcompound1.setInteger("color", color);
		}
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if (this.getArmorMaterial() == CyberwareContent.trenchMat)
		{
			super.getSubItems(item, tab, list);
			ItemStack brown = new ItemStack(this);
			this.setColor(brown, 0x664028);
			list.add(brown);
			ItemStack white = new ItemStack(this);
			this.setColor(white, 0xEAEAEA);
			list.add(white);
		}
		else
		{
			super.getSubItems(item, tab, list);
		}
	}
}
