package flaxbeard.cyberware.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class EntityThrownBlock extends Entity implements IProjectile
{
	public int fallTime;
	public boolean shouldDropItem = true;
	private boolean canSetAsBlock;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private int lastAttackTime = -10;
	private float fallHurtAmount = 2.0F;
	public NBTTagCompound tileEntityData;
	protected static final DataParameter<Integer> SHOOTER = EntityDataManager.<Integer>createKey(EntityThrownBlock.class, DataSerializers.VARINT);
	protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityThrownBlock.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Optional<IBlockState>> FALLTILE = EntityDataManager.<Optional<IBlockState>>createKey(EntityThrownBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);

	public EntityThrownBlock(World worldIn)
	{
		super(worldIn);
	}

	public EntityThrownBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState, EntityLivingBase shooter)
	{
		super(worldIn);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.setPosition(x, y + (double)((1.0F - this.height) / 2.0F), z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.setOrigin(new BlockPos(this));
		this.setFallTile(fallingBlockState);
		this.setShooter(shooter.getEntityId());
	}

	private void setShooter(int i)
	{
		this.dataManager.set(SHOOTER, i);
	}

	public void setOrigin(BlockPos p_184530_1_)
	{
		this.dataManager.set(ORIGIN, p_184530_1_);
	}
	
	public void setFallTile(IBlockState state)
	{
		this.dataManager.set(FALLTILE, Optional.of(state));
	}

	@SideOnly(Side.CLIENT)
	public BlockPos getOrigin()
	{
		return (BlockPos)this.dataManager.get(ORIGIN);
	}
	
	public IBlockState getFallTile()
	{
		Optional<IBlockState> option = this.dataManager.get(FALLTILE);
		if (option.isPresent())
		{
			return option.get();
		}
		return null;
	}
	
	public int getShooter()
	{
		return this.dataManager.get(SHOOTER);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	protected void entityInit()
	{
		this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
		this.dataManager.register(FALLTILE, Optional.absent());
		this.dataManager.register(SHOOTER, 0);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		IBlockState fallTile = getFallTile();
		if (fallTile != null)
		{
			Block block = fallTile.getBlock();
	
			if (fallTile.getMaterial() == Material.AIR)
			{
				this.setDead();
			}
			else
			{
				this.prevPosX = this.posX;
				this.prevPosY = this.posY;
				this.prevPosZ = this.posZ;
	
				if (this.fallTime++ == 0)
				{
					BlockPos blockpos = new BlockPos(this);
	
					if (this.worldObj.getBlockState(blockpos).getBlock() == block)
					{
						this.worldObj.setBlockToAir(blockpos);
					}
					else if (!this.worldObj.isRemote)
					{
						this.setDead();
						return;
					}
				}
	
				if (!this.func_189652_ae())
				{
					this.motionY -= 0.03999999910593033D;
				}
	
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.9800000190734863D;
				this.motionY *= 0.9800000190734863D;
				this.motionZ *= 0.9800000190734863D;
	
				if (!this.worldObj.isRemote)
				{
					BlockPos blockpos1 = new BlockPos(this);
	
					if (this.onGround)
					{
						IBlockState iblockstate = this.worldObj.getBlockState(blockpos1);
	
						if (this.worldObj.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) //Forge: Don't indent below.
						if (BlockFalling.canFallThrough(this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))))
						{
							this.onGround = false;
							return;
						}
	
						this.motionX *= 0.699999988079071D;
						this.motionZ *= 0.699999988079071D;
						this.motionY *= -0.5D;
	
						if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION)
						{
							this.setDead();
	
							if (!this.canSetAsBlock)
							{
								if (this.worldObj.canBlockBePlaced(block, blockpos1, true, EnumFacing.UP, (Entity)null, (ItemStack)null) && !BlockFalling.canFallThrough(this.worldObj.getBlockState(blockpos1.down())) && this.worldObj.setBlockState(blockpos1, fallTile, 3))
								{
									List<Entity> list = Lists.newArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(.5, .5, .5)));
									for (Entity entity : list)
									{
										entity.setPosition(entity.posX, entity.posY + 1.1, entity.posZ);
									}
									if (block instanceof BlockFalling)
									{
										((BlockFalling)block).onEndFalling(this.worldObj, blockpos1);
									}
	
									if (this.tileEntityData != null && block instanceof ITileEntityProvider)
									{
										TileEntity tileentity = this.worldObj.getTileEntity(blockpos1);
	
										if (tileentity != null)
										{
											NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
	
											for (String s : this.tileEntityData.getKeySet())
											{
												NBTBase nbtbase = this.tileEntityData.getTag(s);
	
												if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
												{
													nbttagcompound.setTag(s, nbtbase.copy());
												}
											}
	
											tileentity.readFromNBT(nbttagcompound);
											tileentity.markDirty();
										}
									}
								}
								else if (!worldObj.isRemote)
								{
									int num = block.quantityDropped(fallTile, 0, worldObj.rand);
									Item item = block.getItemDropped(fallTile, worldObj.rand, 0);
									this.entityDropItem(new ItemStack(item, num, block.damageDropped(fallTile)), 0.0F);		
								}
							}
						}
					}
					else if (this.fallTime > 100 && !this.worldObj.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)
					{
						
						int num = block.quantityDropped(fallTile, 0, worldObj.rand);
						Item item = block.getItemDropped(fallTile, worldObj.rand, 0);
						this.entityDropItem(new ItemStack(item, num, block.damageDropped(fallTile)), 0.0F);
					

						this.setDead();
					}
				}
				
				if (fallTime > 2)
				{
				
					List<Entity> list = Lists.newArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(.5, .5, .5)));
					DamageSource damagesource = DamageSource.fallingBlock;
					int count = 0;
					for (Entity entity : list)
					{
						if (entity instanceof EntityLivingBase && entity.getEntityId() != getShooter())
						{
							count++;
							float damage = 2F;
							Material mat = fallTile.getMaterial();
							if ((ticksExisted - lastAttackTime) > 10)
							{
								entity.motionX += motionX * 0.6;
								entity.motionZ += motionZ * 0.6;
							}
							if (mat == Material.CACTUS
								|| mat == Material.ROCK
								|| mat == Material.IRON
								|| mat == Material.ANVIL
								|| mat == Material.ICE
								|| mat == Material.PACKED_ICE)
							{
								damage *= 2F;
							}
							else if (mat == Material.VINE
								|| mat == Material.PLANTS
								|| mat == Material.LEAVES)
							{
								continue;
							}
							entity.attackEntityFrom(damagesource, damage);
						}
					}
					
					if (count > 0 && (ticksExisted - lastAttackTime) > 10)
					{
						lastAttackTime = this.ticksExisted;
						worldObj.playSound(posX, posY, posZ, block.getSoundType().getBreakSound(), SoundCategory.AMBIENT, 1, 1, false);
						
					}
					

				}
			}
		}
	}

	public void fall(float distance, float damageMultiplier)
	{
		IBlockState fallTile = getFallTile();
		Block block = fallTile.getBlock();



	}

	public static void func_189741_a(DataFixer p_189741_0_)
	{
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		Block block = this.getFallTile() != null ? this.getFallTile().getBlock() : Blocks.AIR;
		ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(block);
		compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
		compound.setByte("Data", (byte)block.getMetaFromState(this.getFallTile()));
		compound.setInteger("Time", this.fallTime);
		compound.setBoolean("DropItem", this.shouldDropItem);
		compound.setBoolean("HurtEntities", this.hurtEntities);
		compound.setFloat("FallHurtAmount", this.fallHurtAmount);
		compound.setInteger("FallHurtMax", this.fallHurtMax);
		compound.setInteger("Shooter", this.getShooter());

		if (this.tileEntityData != null)
		{
			compound.setTag("TileEntityData", this.tileEntityData);
		}
		
		return compound;
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		int i = compound.getByte("Data") & 255;

		if (compound.hasKey("Block", 8))
		{
			this.setFallTile(Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i));
		}
		else if (compound.hasKey("TileID", 99))
		{
			this.setFallTile(Block.getBlockById(compound.getInteger("TileID")).getStateFromMeta(i));
		}
		else
		{
			this.setFallTile(Block.getBlockById(compound.getByte("Tile") & 255).getStateFromMeta(i));
		}

		this.fallTime = compound.getInteger("Time");
		Block block = this.getFallTile().getBlock();

		if (compound.hasKey("HurtEntities", 99))
		{
			this.hurtEntities = compound.getBoolean("HurtEntities");
			this.fallHurtAmount = compound.getFloat("FallHurtAmount");
			this.fallHurtMax = compound.getInteger("FallHurtMax");
		}
		else if (block == Blocks.ANVIL)
		{
			this.hurtEntities = true;
		}

		if (compound.hasKey("DropItem", 99))
		{
			this.shouldDropItem = compound.getBoolean("DropItem");
		}

		if (compound.hasKey("TileEntityData", 10))
		{
			this.tileEntityData = compound.getCompoundTag("TileEntityData");
		}

		if (block == null || block.getDefaultState().getMaterial() == Material.AIR)
		{
			this.setFallTile(Blocks.SAND.getDefaultState());
		}
		
		this.setShooter(compound.getInteger("Shooter"));
		
		this.getDataManager().setDirty(FALLTILE);
		this.getDataManager().setDirty(SHOOTER);

	}

	public void setHurtEntities(boolean p_145806_1_)
	{
		this.hurtEntities = p_145806_1_;
	}

	public void addEntityCrashInfo(CrashReportCategory category)
	{
		super.addEntityCrashInfo(category);

		if (this.getFallTile() != null)
		{
			Block block = this.getFallTile().getBlock();
			category.addCrashSection("Immitating block ID", Integer.valueOf(Block.getIdFromBlock(block)));
			category.addCrashSection("Immitating block data", Integer.valueOf(block.getMetaFromState(this.getFallTile())));
		}
	}

	@SideOnly(Side.CLIENT)
	public World getWorldObj()
	{
		return this.worldObj;
	}

	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire()
	{
		return false;
	}

	@Nullable
	public IBlockState getBlock()
	{
		return this.getFallTile();
	}

	public boolean ignoreItemEntityData()
	{
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{

		this.readFromNBT(compound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{


		this.writeToNBT(compound);
	}

	@Override
	public void setThrowableHeading(double x, double y, double z,
			float velocity, float inaccuracy)
	{
		Vec3d vec = new Vec3d(x, y, z);
		this.motionX = vec.xCoord * velocity;
		this.motionY = vec.yCoord * velocity;
		this.motionZ = vec.zCoord * velocity;

	}
}