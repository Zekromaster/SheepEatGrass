package net.zekromaster.sheepeatgrass.mixin;


import net.zekromaster.sheepeatgrass.interfaces.ISheep;
import net.minecraft.client.render.entity.model.AnimalQuadrupedModelBase;
import net.minecraft.client.render.entity.model.SheepShorn;
import net.minecraft.entity.Living;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SheepShorn.class)
public abstract class ShornSheepModelMixin extends AnimalQuadrupedModelBase {
    @Unique
    private float headRotationAngleX;

    protected ShornSheepModelMixin(int i, float f) {
        super(i, f);
    }

    @Override
    public void animateModel(Living var1, float var2, float var3, float var4) {
        super.animateModel(var1, var2, var3, var4);
        this.cuboid1.rotationPointY = 6.0F + ((ISheep) var1).sheepEatGrass$getHeadRotationPointY(var4) * 9.0F;
        this.headRotationAngleX = ((ISheep)var1).sheepEatGrass$getHeadRotationAngleX(var4);
    }

    @Override
    public void setAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
        super.setAngles(var1, var2, var3, var4, var5, var6);
        this.cuboid1.pitch = this.headRotationAngleX;
    }
}
