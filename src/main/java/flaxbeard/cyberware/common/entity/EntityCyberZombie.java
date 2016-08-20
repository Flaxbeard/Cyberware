package flaxbeard.cyberware.common.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;
import flaxbeard.cyberware.common.item.ItemArmorCyberware;
import flaxbeard.cyberware.common.lib.LibConstants;

public class EntityCyberZombie extends EntityZombie
{
	private static final DataParameter<Integer> CYBER_VARIANT = EntityDataManager.<Integer>createKey(EntityCyberZombie.class, DataSerializers.VARINT);

	public boolean hasWare;
	private CyberwareUserDataImpl cyberware;
	
	public EntityCyberZombie(World worldIn)
	{
		super(worldIn);
		cyberware = new CyberwareUserDataImpl();
		hasWare = false;
		//CyberwareDataHandler.addRandomCyberware(this);
	}
	
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(CYBER_VARIANT, Integer.valueOf(0));
	}
	
	@Override
	public boolean isVillager()
	{
		return false;
	}
	
	@Override
	public void onLivingUpdate()
	{
		if (!this.hasWare && !this.worldObj.isRemote)
		{
			if (!isBrute() && this.worldObj.rand.nextFloat() < (LibConstants.NATURAL_BRUTE_CHANCE / 100F))
			{
				this.setBrute();
			}
			CyberwareDataHandler.addRandomCyberware(this, isBrute());
			if (isBrute())
			{
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Brute Bonus", 6D, 0));
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier("Brute Bonus", 1D, 0));
			}
			this.setHealth(this.getMaxHealth());
			hasWare = true;
		}
		if (isBrute() && this.height != (1.95F * 1.2F))
		{
			this.setSizeNormal(0.6F * 1.2F, 1.95F * 1.2F);
		}
		super.onLivingUpdate();
	}
	
	
	protected void setSizeNormal(float width, float height)
	{
		if (width != this.width || height != this.height)
		{
			float f = this.width;
			this.width = width;
			this.height = height;
			AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
			this.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)this.width, axisalignedbb.minY + (double)this.height, axisalignedbb.minZ + (double)this.width));

			if (this.width > f && !this.firstUpdate && !this.worldObj.isRemote)
			{
				this.moveEntity((double)(f - this.width), 0.0D, (double)(f - this.width));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		compound.setBoolean("hasRandomWare", hasWare);
		compound.setBoolean("brute", isBrute());

		if (hasWare)
		{
			NBTTagCompound comp = cyberware.serializeNBT();
			compound.setTag("ware", comp);
		}
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		boolean brute = compound.getBoolean("brute");
		if (brute)
		{
			this.setBrute();
		}
		this.hasWare = compound.getBoolean("hasRandomWare");
		if (compound.hasKey("ware"))
		{
			cyberware.deserializeNBT(compound.getCompoundTag("ware"));
		}
	}
	
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
	{

		if (capability == CyberwareAPI.CYBERWARE_CAPABILITY)
		{
			return (T) cyberware;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
	{
		return capability == CyberwareAPI.CYBERWARE_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
	{
		super.dropEquipment(wasRecentlyHit, lootingModifier);
		
		if (CyberwareConfig.KATANA && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() == CyberwareContent.katana)
		{
			
			ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).copy();
			if (itemstack.isItemStackDamageable())
			{
				int i = Math.max(itemstack.getMaxDamage() - 25, 1);
				int j = itemstack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(i) + 1);

				if (j > i)
				{
					j = i;
				}

				if (j < 1)
				{
					j = 1;
				}

				itemstack.setItemDamage(j);
			}

			this.entityDropItem(itemstack, 0.0F);
		}
		
		
		if (hasWare)
		{
			float rarity = Math.min(100, CyberwareConfig.DROP_RARITY + lootingModifier * 5F);
			if (worldObj.rand.nextFloat() < (rarity / 100F))
			{
				List<ItemStack> allWares = new ArrayList<ItemStack>();
				for (EnumSlot slot : EnumSlot.values())
				{
					ItemStack[] stuff = cyberware.getInstalledCyberware(slot);
					
					allWares.addAll(Arrays.asList(stuff));
				}
				
				allWares.removeAll(Collections.singleton(null));

				ItemStack drop = null;
				int count = 0;
				while (count < 50 && (drop == null || drop.getItem() == CyberwareContent.creativeBattery || drop.getItem() == CyberwareContent.bodyPart))
				{
					int random = worldObj.rand.nextInt(allWares.size());
					drop = ItemStack.copyItemStack(allWares.get(random));
					drop = CyberwareAPI.sanitize(drop);
					drop = CyberwareAPI.getCyberware(drop).setQuality(drop, CyberwareAPI.QUALITY_SCAVENGED);
					drop.stackSize = 1;
					count++;
				}

				if (count < 50)
				{
					this.entityDropItem(drop, 0.0F);
				}
			}
		}
	}
	
	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		super.setEquipmentBasedOnDifficulty(difficulty);
		
		if (CyberwareConfig.KATANA && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() == Items.IRON_SWORD)
		{
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(CyberwareContent.katana));
			this.setDropChance(EntityEquipmentSlot.MAINHAND, 0F);
		}
	}

	public boolean isBrute()
	{
		return ((Integer)this.dataManager.get(CYBER_VARIANT)).intValue() == 1;
	}
	
	public boolean setBrute()
	{
		this.setChild(false);
		this.dataManager.set(CYBER_VARIANT, Integer.valueOf(1));

		if (!this.hasWare)
		{
			return true;
		}
		return false;
	}
}
