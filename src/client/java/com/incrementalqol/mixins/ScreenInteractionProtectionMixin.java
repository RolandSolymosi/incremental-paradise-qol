package com.incrementalqol.mixins;

import com.incrementalqol.common.utils.ScreenInteraction;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class ScreenInteractionProtectionMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        //if (ScreenInteraction.ScreenInteractionManager.anyActiveInteractionOngoing()) {
        //    cir.setReturnValue(false); // Prevent all mouse clicks inside container screens
        //}
    }
}