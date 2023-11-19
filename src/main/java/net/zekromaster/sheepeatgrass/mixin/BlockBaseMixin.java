package net.zekromaster.sheepeatgrass.mixin;

import net.minecraft.block.BlockBase;
import net.zekromaster.minecraft.sheepeatgrass.api.SheepEatingRegistry;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.BlockReference;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.EatingLocation;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.matchers.BlockReferenceMatcher;
import net.zekromaster.minecraft.sheepeatgrass.api.blocks.matchers.BlockWithAnyMetaMatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBase.class)
public abstract class BlockBaseMixin {

    private BlockBaseMixin() {}

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        SheepEatingRegistry.getInstance().add(
            new BlockWithAnyMetaMatcher(BlockBase.GRASS.id),
            EatingLocation.UNDERNEATH,
            new BlockReference(BlockBase.DIRT.id, 0)
        );
        SheepEatingRegistry.getInstance().add(
            new BlockReferenceMatcher(BlockBase.TALLGRASS.id, 1),
            EatingLocation.SAME_BLOCK,
            new BlockReference(0, 0)
        );
    }
}
