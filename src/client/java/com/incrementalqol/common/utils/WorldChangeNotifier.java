package com.incrementalqol.common.utils;

import com.incrementalqol.common.data.World;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class WorldChangeNotifier {
    public static final Logger LOGGER = LoggerFactory.getLogger(WorldChangeNotifier.class);
    private static World lastWorld = null;

    private static final List<Function<Pair<World, Boolean>, CompletableFuture<Boolean>>> listeners = new CopyOnWriteArrayList<>();

    public static World getLastWorld() {
        return lastWorld;
    }

    /**
     * Register a listener to world change
     *
     * @param listener Callback when a world changes. The return value is the World Identifier, a Boolean parameter which is True if there were change between Nightmare and Normal world
     */
    public static boolean Register(Function<Pair<World, Boolean>, CompletableFuture<Boolean>> listener) {
        return listeners.add(listener);
    }

    public static boolean IsActualWorldNightmare() {
        return lastWorld != null && lastWorld.getRealm() == World.Realm.Nightmare;
    }

    private static synchronized void afterWorldChange(MinecraftClient client, ClientWorld world) {
        ConfiguredLogger.LogInfo(LOGGER, "Client switched to world: " + world.getRegistryKey().getValue());
        var worldId = world.getRegistryKey().getValue();
        var worldDefinition = World.findById(worldId);
        if (worldDefinition.isPresent() && (worldDefinition.get().getRealm() == World.Realm.Nightmare || worldDefinition.get().getRealm() == World.Realm.Normal)){
            if (!listeners.isEmpty()){
                var isNightmare = worldDefinition.get().getRealm() == World.Realm.Nightmare;
                callListenerCore(0, worldDefinition.get(), lastWorld == null || (isNightmare && lastWorld.getRealm() != World.Realm.Nightmare) || (!isNightmare && lastWorld.getRealm() == World.Realm.Nightmare));
            }
            lastWorld = worldDefinition.get();
        }
    }

    private static synchronized void callListenerCore(int index, World world, boolean isChangedRealm) {
        if (listeners.size() > index) {
            listeners.get(index).apply(new Pair<>(
                    world,
                    isChangedRealm
            )).thenAccept(o -> {
                if (listeners.size() > index + 1) {
                    callListenerCore(index + 1, world, isChangedRealm);
                }
            });
        }
    }

    static {

        // When joining a world (Singleplayer or Multiplayer)
        //ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
        //    afterWorldChange(client, null);
        //});

        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(WorldChangeNotifier::afterWorldChange);
    }
}
