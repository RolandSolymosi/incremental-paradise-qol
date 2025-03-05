package com.incrementalqol.mixins;

import com.incrementalqol.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class NoLeashOrBalloonMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "shouldRender", at = @At("HEAD"))
    public void cancelLeashRendering(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.HANDLER.instance().getBalloonRopeEnabled()){
            if (entity instanceof SilverfishEntity silverfishEntity) {
                if (silverfishEntity.getLeashHolder() instanceof PlayerEntity player) {
                    if (player == MinecraftClient.getInstance().player) {
                        silverfishEntity.detachLeash();
                    }
                }
            }
        }
    }
}

