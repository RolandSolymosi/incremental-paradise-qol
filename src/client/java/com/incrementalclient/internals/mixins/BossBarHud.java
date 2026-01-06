package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.BossBarReader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;


@Mixin(net.minecraft.client.gui.hud.BossBarHud.class)
public class BossBarHud {

    @Final
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    @Unique
    private static final Supplier<BossBarReader> bossBarReader = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(BossBarReader.class));

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void render(DrawContext ctx, CallbackInfo ci) {
        bossBarReader.get().bossBarUpdate(bossBars);
    }
}
