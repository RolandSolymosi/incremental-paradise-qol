package com.incrementalclient.internals.mixins;

import com.incrementalclient.internals.interfaces.ReentryPacket;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(net.minecraft.network.packet.Packet.class)
public interface Packet extends ReentryPacket {

}