package com.incrementalclient.internals.mixins;

import com.incrementalclient.Main;
import com.incrementalclient.services.BossBarReader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;


@Mixin(net.minecraft.client.gui.hud.BossBarHud.class)
public class BossBarHud {

    @Final
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void render(DrawContext ctx, CallbackInfo ci) {
        Main.SERVICE_PROVIDER.getService(BossBarReader.class).bossBarUpdate(bossBars);
    }
}
