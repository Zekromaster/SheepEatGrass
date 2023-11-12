package net.zekromaster.sheepeatgrass.mixin;

import net.minecraft.block.BlockBase;
import net.zekromaster.sheepeatgrass.SimpleBlockReference;
import net.zekromaster.sheepeatgrass.SheepEatingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBase.class)
public abstract class BlockBaseMixin {

    private BlockBaseMixin() {}

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        SheepEatingRegistry.INSTANCE.add(
            new SimpleBlockReference(BlockBase.GRASS.id, SimpleBlockReference.METADATA_WILDCARD),
            SheepEatingRegistry.EatingLocation.UNDERNEATH,
            new SimpleBlockReference(BlockBase.DIRT.id, 0)
        );
        SheepEatingRegistry.INSTANCE.add(
            new SimpleBlockReference(BlockBase.TALLGRASS.id, 1),
            SheepEatingRegistry.EatingLocation.SAME_BLOCK,
            new SimpleBlockReference(0, 0)
        );
    }
}
