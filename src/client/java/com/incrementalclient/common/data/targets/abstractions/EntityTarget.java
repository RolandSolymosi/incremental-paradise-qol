package com.incrementalclient.common.data.targets.abstractions;

import com.incrementalclient.common.data.Region;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.List;
import java.util.function.Predicate;

public final class EntityTarget implements ITarget {
    private final String name;
    private final String displayName;
    private final Predicate<Entity> condition;
    private final EntityType<?> type;
    private final List<Region> regions;

    public EntityTarget(String name, String displayName, List<Region> regions, Predicate<Entity> condition, EntityType<?> type) {
        this.name = name;
        this.displayName = displayName;
        this.condition = condition;
        this.type = type;
        this.regions = regions == null ? List.of() : regions;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override public String name() { return name; }

    @Override
    public boolean matches(Entity entity) {
        return (!regions.isEmpty() && regions.stream().anyMatch(r -> r.isInRegion(entity.getWorld(), entity.getBlockPos()))) && entity.getType() == type && condition.test(entity);
    }

    public EntityType<?> getType() { return type; }
}