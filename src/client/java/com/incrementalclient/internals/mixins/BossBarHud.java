package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.BossBarObservable;
import com.incrementalclient.services.HudManager;
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
    private static final Supplier<BossBarObservable> bossBarReader = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(BossBarObservable.class));

    @Unique
    private static final Supplier<HudManager> hudManager = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(HudManager.class));

    /**
     * Modify boss bar position to render below the top bar when top bar is active
     * Boss bars normally render at y=12, we need to move them down by top bar height (22) + padding
     */
    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void render(DrawContext ctx, CallbackInfo ci) {
        // Update boss bar observable
        bossBarReader.get().bossBarUpdate(bossBars);

        // Only apply translation when top bar is active
        if (hudManager.get().getConfiguration().getBarScoreboardReplacement()) {
            // Top bar height is 22 pixels, add some padding (4 pixels) for spacing
            int padding = 4;
            int offsetY = hudManager.get().getBarHeight() + padding;

            // Translate matrix to move boss bars down
            var matrices = ctx.getMatrices();
            matrices.push();
            matrices.translate(0, offsetY, 0);
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/client/gui/DrawContext;)V",
        at = @At("RETURN")
    )
    private void onRenderBossBarEnd(DrawContext context, CallbackInfo ci) {
        // Only restore matrix when top bar is active
        if (!hudManager.get().getConfiguration().getBarScoreboardReplacement()) {
            return;
        }

        // Restore matrix state
        var matrices = context.getMatrices();
        matrices.pop();
    }
}
