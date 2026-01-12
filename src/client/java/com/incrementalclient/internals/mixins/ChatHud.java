package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.services.ChatHandler;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public class ChatHud {

    @Unique
    private static final Supplier<ChatHandler> chatHandler = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ChatHandler.class));

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "HEAD"), cancellable = true)
    public void addMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        if (filterMessage(message)) {
            ci.cancel();
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At(value = "HEAD"), cancellable = true)
    public void addMessage(Text message, CallbackInfo ci) {
        if (filterMessage(message)) {
            ci.cancel();
        }
    }

    private boolean filterMessage(Text message) {
        for (var filter : chatHandler.get().getFilters()){
            if (filter.isEnabled()){
                if (filter.isFilterPlayerMessage() || message.getSiblings().stream().noneMatch(s -> s.getStyle().getClickEvent() instanceof ClickEvent.RunCommand(String command) && command.startsWith("/stats "))){
                    if (filter.getRegex().matcher(message.getString()).find()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
