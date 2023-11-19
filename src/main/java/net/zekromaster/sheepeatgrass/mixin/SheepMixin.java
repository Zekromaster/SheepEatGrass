package net.zekromaster.sheepeatgrass.mixin;

import net.zekromaster.minecraft.sheepeatgrass.api.SheepEatingRegistry;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.BlockReference;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.EatingLocation;
import net.zekromaster.sheepeatgrass.interfaces.EatingSheep;
import net.minecraft.block.BlockBase;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.animal.AnimalBase;
import net.minecraft.entity.animal.Sheep;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import net.minecraft.util.maths.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin extends AnimalBase implements EatingSheep {
	@Shadow public abstract void setSheared(boolean arg);

	@Unique
	private int sheepTimer = 0;

	protected SheepMixin(Level arg) {
		super(arg);
	}

	@Inject(at = @At("HEAD"), method = "damage")
	private void damage(EntityBase arg, int i, CallbackInfoReturnable<Boolean> cir) {
		if (arg instanceof PlayerBase p) {
			this.interact(p);
		}
	}

	@Override
	protected boolean method_640() {
		return this.sheepTimer > 0;
	}

	@Unique
	private boolean tryEating(
		int x, int y, int z,
		EatingLocation location
	) {
		final var block = new BlockReference(this.level.getTileId(x, y, z), this.level.getTileMeta(x, y, z));
		final var eatable = SheepEatingRegistry.getInstance().get(location, block);
		eatable.ifPresent(
			eatableBlock -> {
				this.level.playLevelEvent(2001, x, y, z, block.id());
				if (eatableBlock.meta() == 0) {
					this.level.setTile(x, y, z, eatableBlock.id());
				} else {
					this.level.setTileWithMetadata(x, y, z, eatableBlock.id(), eatableBlock.meta());
				}
			}
		);

		return eatable.isPresent();
	}

	@Override
	protected void tickHandSwing() {
		super.tickHandSwing();
		int x;
		int y;
		int z;
		if(!this.method_633() && this.sheepTimer <= 0 && this.rand.nextInt(1000) == 0) {
			x = MathHelper.floor(this.x);
			y = MathHelper.floor(this.y);
			z = MathHelper.floor(this.z);
			if(this.level.getTileId(x, y, z) == BlockBase.TALLGRASS.id && this.level.getTileMeta(x, y, z) == 1 || this.level.getTileId(x, y - 1, z) == BlockBase.GRASS.id) {
				this.sheepTimer = 40;
				this.level.method_185(this, (byte)10);
			}
		} else if(this.sheepTimer == 4) {
			x = MathHelper.floor(this.x);
			y = MathHelper.floor(this.y);
			z = MathHelper.floor(this.z);

			boolean hasEaten = tryEating(x, y, z, EatingLocation.SAME_BLOCK);
			if (!hasEaten) {
				hasEaten = tryEating(x, y - 1, z, EatingLocation.UNDERNEATH);
			}

			if (hasEaten) {
				this.setSheared(false);
			}
		}
	}

	@Override
	public void updateDespawnCounter() {
		super.updateDespawnCounter();
		if(this.sheepTimer > 0) {
			--this.sheepTimer;
		}
	}

	@Override
	public void handleStatus(byte arg) {
		if(arg == 10) {
			this.sheepTimer = 40;
		} else {
			super.handleStatus(arg);
		}
	}

	public float sheepEatGrass$getHeadRotationPointY(float arg) {
		if (this.sheepTimer <= 0) {
			return 0.0F;
		}

		if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
			return 1.0F;
		}

		if (this.sheepTimer < 4) {
			return (this.sheepTimer - arg) / 4.0F;
		}

		return -((this.sheepTimer - 40) - arg) / 4.0F;
	}

	public float sheepEatGrass$getHeadRotationAngleX(float arg) {
		if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
			float val = ((this.sheepTimer - 4) - arg) / 32.0F;
			return (float)Math.PI * 0.2F + 0.2199115F * MathHelper.sin(val * 28.7F);
		}

		if (this.sheepTimer > 0) {
			return (float)Math.PI * 0.2F;
		}

		return this.pitch / (180.0F / (float)Math.PI);
	}

}
