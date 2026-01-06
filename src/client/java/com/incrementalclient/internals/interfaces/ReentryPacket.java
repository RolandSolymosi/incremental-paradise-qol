package com.incrementalclient.internals.interfaces;

import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public interface ReentryPacket {
    Map<Object, Boolean> REENTRY_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    default void setReentered(boolean val) {
        REENTRY_CACHE.put(this, val);
    }

    default boolean isReentered() {
        return REENTRY_CACHE.getOrDefault(this, false);
    }

    static boolean shouldCancel(Packet<?> packet, CallbackInfo ci) {
        ReentryPacket duckTypedPacket = (ReentryPacket) packet;

        if (duckTypedPacket.isReentered()) {
            return false; // Don't cancel, we are re-entering
        }
        ci.cancel();
        duckTypedPacket.setReentered(true);
        return true; // We just cancelled it for the first time
    }
}
