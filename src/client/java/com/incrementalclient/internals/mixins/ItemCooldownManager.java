package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.ItemCooldownWrapper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(net.minecraft.entity.player.ItemCooldownManager.class)
public abstract class ItemCooldownManager {

    @Unique
    private static final Supplier<ItemCooldownWrapper> cooldownWrapper = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ItemCooldownWrapper.class));

    @Shadow
    public abstract float getCooldownProgress(ItemStack stack, float tickProgress);

    @Unique
    private Float lastCooldownProgress = 0f;

    @Inject(method = "getCooldownProgress", at = @At("RETURN"), cancellable = true)
    public void getCooldownProgress(ItemStack stack, float tickDelta, CallbackInfoReturnable<Float> cir) {
        lastCooldownProgress = cir.getReturnValue();
        var wrapperCooldown = cooldownWrapper.get().getItemCooldown(stack);
        wrapperCooldown.ifPresent(cir::setReturnValue);
    }

    @Inject(method = "isCoolingDown", at = @At("HEAD"), cancellable = true)
    public void isCoolingDown(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Really, this is overriding EVERY call to isCoolingDown with this code.
        this.getCooldownProgress(stack, 0);
        if(lastCooldownProgress != null) {
            cir.setReturnValue(lastCooldownProgress > 0);
        }
    }
}
