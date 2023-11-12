package net.zekromaster.sheepeatgrass.mixin;

import net.zekromaster.sheepeatgrass.interfaces.ISheep;
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
public abstract class SheepMixin extends AnimalBase implements ISheep {
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
			boolean hasEaten = false;
			if(this.level.getTileId(x, y, z) == BlockBase.TALLGRASS.id && this.level.getTileMeta(x, y, z) == 1) {
				this.level.playLevelEvent(2001, x, y, z, BlockBase.TALLGRASS.id);
				this.level.setTile(x, y, z, 0);
				hasEaten = true;
			} else if(this.level.getTileId(x, y - 1, z) == BlockBase.GRASS.id) {
				this.level.playLevelEvent(2001, x, y - 1, z, BlockBase.GRASS.id);
				this.level.setTile(x, y - 1, z, BlockBase.DIRT.id);
				hasEaten = true;
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
