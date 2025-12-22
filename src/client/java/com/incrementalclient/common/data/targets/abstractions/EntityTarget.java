package com.incrementalclient.common.data.targets.abstractions;

import com.incrementalclient.common.data.Region;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.function.Predicate;

public final class EntityTarget implements ITarget {
    private final String name;
    private final Predicate<Entity> condition;
    private final EntityType<?> type;
    private final Region region;

    public EntityTarget(String name, Region region, Predicate<Entity> condition, EntityType<?> type) {
        this.name = name;
        this.condition = condition;
        this.type = type;
        this.region = region;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean matches(Entity entity) {
        return (region != null && region.isInRegion(entity.getEntityWorld(), entity.getBlockPos()))
                && entity.getType() == type && condition.test(entity);
    }

    @Override
    public void highlight(MinecraftClient client) {
        // Example: set glowing, or your ghost entity renderer logic
    }

    public EntityType<?> getType() {
        return type;
    }
}