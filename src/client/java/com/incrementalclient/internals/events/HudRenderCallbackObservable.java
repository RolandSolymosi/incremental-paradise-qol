package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderCallbackObservable extends ObservableBase<Observer<HudRenderCallbackObservable.Event>, HudRenderCallbackObservable.Event> {

    public HudRenderCallbackObservable(){
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> notifyObservers(new Event(drawContext, renderTickCounter)));
    }

    public record Event(DrawContext drawContext, RenderTickCounter counter){
    };
}
