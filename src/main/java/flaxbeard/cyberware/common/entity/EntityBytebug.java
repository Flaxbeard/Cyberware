package flaxbeard.cyberware.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberwareBase;

public class EntityBytebug extends EntityMob
{
	private int lifetime = 0;

	public EntityBytebug(World worldIn)
	{
		super(worldIn);
		this.experienceValue = 3;
		this.setSize(0.4F, 0.3F);
	}

	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	}

	public float getEyeHeight()
	{
		return 0.1F;
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
	}

	protected SoundEvent getHurtSound()
	{
		return SoundEvents.ENTITY_ENDERMITE_HURT;
	}

	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_ENDERMITE_DEATH;
	}

	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
	}

	
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.lifetime = compound.getInteger("Lifetime");
	}


	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setInteger("Lifetime", this.lifetime);
	}

	public void onUpdate()
	{
		this.renderYawOffset = this.rotationYaw;
		super.onUpdate();
	}

	public double getYOffset()
	{
		return 0.3D;
	}

	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!this.worldObj.isRemote)
		{
			if (!this.isNoDespawnRequired())
			{
				++this.lifetime;
			}

			if (this.lifetime >= 2400)
			{
				this.setDead();
			}
		}
	}

	protected boolean isValidLightLevel()
	{
		return true;
	}

	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.UNDEFINED;
	}
	
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);

		if (cause.getEntity() instanceof EntityPlayer)
		{
			this.entityDropItem(new ItemStack(CyberwareContent.component, 1, worldObj.rand.nextInt(((ItemCyberwareBase) CyberwareContent.component).subnames.length)), 0F);
		}
	}

}