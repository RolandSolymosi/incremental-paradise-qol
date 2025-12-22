package com.incrementalqol.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.Click;

@Mixin(net.minecraft.client.gui.screen.ingame.HandledScreen.class)
public class HandledScreen {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        // if (ScreenInteraction.ScreenInteractionManager.anyActiveInteractionOngoing())
        // {
        // cir.setReturnValue(false); // Prevent all mouse clicks inside container
        // screens
        // }
    }
}