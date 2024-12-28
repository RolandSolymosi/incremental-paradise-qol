package com.example.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarMixin {
    @Shadow
    private Map<UUID, BossBar> bossBars;

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    public void onRender(DrawContext ctx, CallbackInfo ci) {
        bossBars.values().forEach(bar -> {
            System.out.println("Boss Bar: " + bar.getName().getString());
            System.out.println("Color: " + bar.getColor());
            System.out.println("Style: " + bar.getStyle());
            System.out.println("Progress: " + bar.getPercent());
        });
    }
}
