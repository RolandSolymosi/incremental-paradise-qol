package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.OverlayMessageObservable;
import com.incrementalclient.internals.ScoreboardChangedListenable;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public abstract class InGameHud {

    @Unique
    private static final Supplier<ScoreboardChangedListenable> scoreboardListener = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ScoreboardChangedListenable.class));

    @Unique
    private static final Supplier<OverlayMessageObservable> overlayMessageObservable = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(OverlayMessageObservable.class));

    @Unique
    private static final Supplier<MinecraftClientAccessor> mcAccessor = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(MinecraftClientAccessor.class));
    @Unique

    private static final Supplier<HudManager> hudManager = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(HudManager.class));

    @Shadow
    private Text overlayMessage;
    
    @Shadow
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter) {}

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().getBarScoreboardReplacement()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().isHideVanillaHearts()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void renderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().isHideVanillaFood()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(DrawContext context, PlayerEntity player, int int2, int int3, int int4, int x, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().isHideVanillaArmor()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().getBarScoreboardReplacement()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void renderOverlayMessage(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Capture overlay message before potentially cancelling render
        if (overlayMessage != null) {
            overlayMessageObservable.get().messageUpdate(overlayMessage);
        }

        if (hudManager.get().getConfiguration().isHideVanillaOverlayMessage()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().isHideVanillaExperienceBar()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void renderExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (hudManager.get().getConfiguration().isHideVanillaExperienceLevel()) {
            ci.cancel();
        }
    }
    
    /**
     * Modify hotbar position to be within the bottom bar (right side)
     * Using @Inject to modify matrix before renderHotbar is called
     * Only applies translation when activeBarMode is BOTTOM
     */
    @Inject(
        method = "renderHotbar",
        at = @At("HEAD")
    )
    private void onRenderHotbarStart(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Only apply translation when bottom bar is active
        if (hudManager.get().getConfiguration().getActiveBarMode() != HudManager.Configuration.ActiveBarMode.BOTTOM) {
            return;
        }
        
        var window = mcAccessor.get().getWindow();
        if (window.isEmpty()) {
            return;
        }
        
        // Calculate new x position (within bottom bar, right side)
        // Default hotbar is centered at bottom, we want it on the right side of bottom bar
        int screenWidth = window.get().getScaledWidth();
        int hotbarWidth = 182; // Standard hotbar width
        int defaultX = screenWidth / 2 - hotbarWidth / 2; // Default centered position
        int padding = 4; // Padding from right edge
        int newX = screenWidth - hotbarWidth - padding; // Right side with padding
        
        // Translate matrix to move hotbar to the right
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(newX - defaultX, 0, 0);
    }
    
    @Inject(
        method = "renderHotbar",
        at = @At("RETURN")
    )
    private void onRenderHotbarEnd(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Only restore matrix when bottom bar is active
        if (hudManager.get().getConfiguration().getActiveBarMode() != HudManager.Configuration.ActiveBarMode.BOTTOM) {
            return;
        }
        
        var window = mcAccessor.get().getWindow();
        if (window.isEmpty()) {
            return;
        }
        
        // Restore matrix state
        var matrices = context.getMatrices();
        matrices.pop();
    }
}
