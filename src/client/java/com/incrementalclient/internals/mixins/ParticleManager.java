package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.services.ShinyOreMonitor;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.particle.ParticleManager.class)
public class ParticleManager {

    @Unique
    private static final Supplier<ShinyOreMonitor> shinyOreMonitor = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ShinyOreMonitor.class));

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"))
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        //shinyOreMonitor.get().onAddParticle(parameters, x, y, z);
    }
}