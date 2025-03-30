package com.incrementalqol.mixinWrappers;

import com.incrementalqol.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EntityRenderer {
    public static void shouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
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
