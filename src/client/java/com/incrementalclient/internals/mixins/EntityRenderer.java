package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.EntityRendererObservable;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.render.entity.EntityRenderer.class)
public class EntityRenderer<T extends Entity, S extends EntityRenderState> {

    @Unique
    private static final Supplier<EntityRendererObservable> entityRenderObservable = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(EntityRendererObservable.class));

    @Inject(method = "shouldRender", at = @At("HEAD"))
    public void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        entityRenderObservable.get().entityRendered(entity);
    }
}
