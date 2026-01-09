package com.incrementalclient.internals;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.minecraft.entity.Entity;

public class EntityRendererObservable extends ObservableBase<Observer<EntityRendererObservable.EntityRender>, EntityRendererObservable.EntityRender> {

    public EntityRendererObservable() {
    }

    public void entityRendered(Entity entity) {
        notifyObservers(new EntityRender(entity));
    }

    public record EntityRender(Entity entity) {
    }
}