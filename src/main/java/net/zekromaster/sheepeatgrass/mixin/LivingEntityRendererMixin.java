package net.zekromaster.sheepeatgrass.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelBase;
import net.minecraft.entity.Living;
import net.minecraft.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer {

    @Shadow protected EntityModelBase model;

    @Inject(method = "method_822", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModelBase;render(FFFFFF)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderModel(
        Living aL1, double ad1, double ad2, double ad3,
        float af1, float af2, CallbackInfo ci,
        float lf1, float lf2, float lf3,
        float lf4, float lf5, float lf6,
        float lf8
    ) {
        if (aL1 instanceof Sheep) {
            this.model.animateModel(aL1, lf8, lf6, af2);
        }
    }

}
