package flaxbeard.cyberware.common.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;

public class EntityCyberZombie extends EntityZombie
{
	
	public boolean hasWare;
	private CyberwareUserDataImpl cyberware;
	
	public EntityCyberZombie(World worldIn)
	{
		super(worldIn);
		cyberware = new CyberwareUserDataImpl();
		hasWare = false;
		//CyberwareDataHandler.addRandomCyberware(this);
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
			CyberwareDataHandler.addRandomCyberware(this);
			hasWare = true;
		}
		super.onLivingUpdate();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		compound.setBoolean("hasRandomWare", hasWare);

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
		System.out.println(capability);
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
	{
		return capability == CyberwareAPI.CYBERWARE_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		
		if (hasWare)
		{
			if (worldObj.rand.nextFloat() < (CyberwareConfig.DROP_RARITY / 100F))
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
}
