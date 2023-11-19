package net.zekromaster.sheepeatgrass.mixin;

import net.zekromaster.sheepeatgrass.interfaces.EatingSheep;
import net.minecraft.client.render.entity.model.AnimalQuadrupedModelBase;
import net.minecraft.client.render.entity.model.Sheep;
import net.minecraft.entity.Living;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Sheep.class)
public abstract class SheepModelMixin extends AnimalQuadrupedModelBase {

    @Unique
    private float headRotationAngleX;

    protected SheepModelMixin(int i, float f) {
        super(i, f);
    }

    @Override
    public void animateModel(Living var1, float var2, float var3, float var4) {
        super.animateModel(var1, var2, var3, var4);
        this.cuboid1.rotationPointY = 6.0F + ((EatingSheep)var1).sheepEatGrass$getHeadRotationPointY(var4) * 9.0F;
        this.headRotationAngleX = ((EatingSheep)var1).sheepEatGrass$getHeadRotationAngleX(var4);
    }

    @Override
    public void setAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
        super.setAngles(var1, var2, var3, var4, var5, var6);
        this.cuboid1.pitch = this.headRotationAngleX;
    }

}
