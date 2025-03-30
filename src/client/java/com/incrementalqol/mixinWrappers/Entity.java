package com.incrementalqol.mixinWrappers;

import com.incrementalqol.modules.Glowing;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Entity {

    public static void isGlowing(net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Glowing.shouldGlow(entity)) {
            cir.setReturnValue(true);
        }
    }
}

