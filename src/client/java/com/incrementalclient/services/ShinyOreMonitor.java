package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.targets.Target;
import com.incrementalclient.common.data.targets.abstractions.BlockTarget;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ShinyOreMonitor extends ObservableBase<Observer<ShinyOreMonitor.Event>, ShinyOreMonitor.Event> {
    private static final double PROXIMITY_THRESHOLD = 0.1f;
    private static final double TOLERANCE = 0.001f; // Allow for slight floating point errors
    private final ConcurrentHashMap<Long, ShinyOre> shinyOres = new ConcurrentHashMap<>();

    private final WorldMonitor worldMonitor;
    private final MinecraftClientAccessor mcAccessor;

    public ShinyOreMonitor(
            WorldMonitor worldMonitor,
            ClientPlayConnectionObservable clientPlayConnectionObservable,
            MinecraftClientAccessor mcAccessor
    ) {
        this.worldMonitor = worldMonitor;
        this.mcAccessor = mcAccessor;
        worldMonitor.subscribe(e -> clearAllShiny());
        clientPlayConnectionObservable.subscribe((e) -> {
            if (e == ClientPlayConnectionObservable.EventKind.Disconnect) {
                clearAllShiny();
            }
        });
    }

    public Map<BlockPos, ShinyOre> getShinyOres() {
        return shinyOres.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> BlockPos.fromLong(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    private void clearAllShiny() {
        for (var shiny : shinyOres.entrySet()){
            notifyObservers(new Event(shiny.getValue(), EventKind.Removed));
            shinyOres.remove(shiny.getKey());
        }
    }

    public void addOrUpdateShinyBecauseOfParticle(ParticleEffect parameters, double x, double y, double z) {
        var world = mcAccessor.getWorld();
        if (world.isPresent() && (worldMonitor.currentWorld().getRealm() == World.Realm.Nightmare || worldMonitor.currentWorld().getRealm() == World.Realm.Normal)) {
            var particlePos = new BlockPos((int) x, (int) y, (int) z);
            var particleType = parameters.getType();
            var particle = Registries.PARTICLE_TYPE.getId(particleType);

            if (particle != null && (particle.equals(Identifier.ofVanilla("electric_spark")) || particle.equals(Identifier.ofVanilla("scrape")))) {
                var xDistance = x - Math.floor(x);
                var yDistance = y - Math.floor(y);
                var zDistance = z - Math.floor(z);
                var sparkSurfaceBlock = Math.abs(xDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.west()
                        : Math.abs(xDistance - (1.0f - PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.east()
                        : Math.abs(yDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.down()
                        : Math.abs(yDistance - (1.0f - PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.up()
                        : Math.abs(zDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.north()
                        : Math.abs(zDistance - (1.0f - PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.south()
                        : null;

                //sparkSurfaceBlock = particlePos;
                if (sparkSurfaceBlock != null) {
                    if (x < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.west();
                    }
                    if (y < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.down();
                    }
                    if (z < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.north();
                    }

                    if (parameters instanceof SimpleParticleType) {
                        var target = Target.find(worldMonitor.currentWorld(), world.get().getBlockState(sparkSurfaceBlock).getBlock());
                        if (target.isPresent()){
                            var newShiny = new ShinyOre(
                                    sparkSurfaceBlock,
                                    target.get(),
                                    particle.equals(Identifier.ofVanilla("electric_spark"))
                                            ? ShinyKind.Glared
                                            : ShinyKind.Normal
                            );
                            var positionId = sparkSurfaceBlock.asLong();
                            var actual = shinyOres.get(positionId);
                            if (actual != null && actual.kind != newShiny.kind) {
                                shinyOres.put(positionId, newShiny);
                                notifyObservers(new ShinyOreMonitor.Event(newShiny, EventKind.Updated));
                            } else if (actual == null) {
                                shinyOres.put(positionId, newShiny);
                                notifyObservers(new ShinyOreMonitor.Event(newShiny, EventKind.Added));
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeShinyBecauseOfStateChange(BlockPos pos, BlockState state) {
        var shinyOre = shinyOres.get(pos.asLong());
        if (shinyOre != null){
            shinyOres.remove(pos.asLong());
            notifyObservers(new ShinyOreMonitor.Event(shinyOre, EventKind.Removed));
        }
    }

    public record Event(ShinyOre shinyOre, EventKind kind){
    }

    public record ShinyOre(BlockPos position, BlockTarget target, ShinyKind kind) {
    }

    public enum ShinyKind {
        Normal,
        Glared,
    }

    public enum EventKind {
        Added,
        Updated,
        Removed,
    }
}
