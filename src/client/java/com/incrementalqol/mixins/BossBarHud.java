package com.incrementalqol.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
import java.util.UUID;


@Mixin(net.minecraft.client.gui.hud.BossBarHud.class)
public class BossBarHud {

    @Final
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void render(DrawContext ctx, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.BossBarHud.render(bossBars, ctx, ci);
    }
}
