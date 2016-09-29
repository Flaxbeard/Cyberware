package flaxbeard.cyberware.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;

public class CyberwareUpdateEvent extends EntityEvent
{
	private final EntityLivingBase entityLiving;
	
	public CyberwareUpdateEvent(EntityLivingBase entity)
	{
		super(entity);
		entityLiving = entity;
	}

	public EntityLivingBase getEntityLiving()
	{
		return entityLiving;
	}
}
