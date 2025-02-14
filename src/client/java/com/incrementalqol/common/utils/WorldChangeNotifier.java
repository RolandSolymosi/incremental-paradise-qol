package com.incrementalqol.common.utils;

import com.incrementalqol.common.data.Worlds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class WorldChangeNotifier {
    public static final Logger LOGGER = LoggerFactory.getLogger(WorldChangeNotifier.class);
    private static RegistryKey<World> lastWorldKey = null;

    private static final List<Function<Pair<Identifier, Boolean>, CompletableFuture<Boolean>>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Register a listener to world change
     *
     * @param listener Callback when a world changes. The return value is the World Identifier and a Boolean parameter which is True of Nightmare World, otherwise False.
     */
    public static boolean Register(Function<Pair<Identifier, Boolean>, CompletableFuture<Boolean>> listener) {
        return listeners.add(listener);
    }

    public static boolean IsActualWorldNightmare() {
        return lastWorldKey != null && lastWorldKey.getValue().equals(Worlds.WorldNightmare);
    }

    private static synchronized void afterWorldChange(MinecraftClient client, ClientWorld world) {
        ConfiguredLogger.LogInfo(LOGGER, "Client switched to world: " + world.getRegistryKey().getValue());
        var worldId = world.getRegistryKey().getValue();
        if (!worldId.equals(Worlds.WorldHub)){
            if (!listeners.isEmpty()){
                callListenerCore(0, worldId);
            }
            lastWorldKey = world.getRegistryKey();
        }
    }

    private static synchronized void callListenerCore(int index, Identifier world) {
        if (listeners.size() > index) {
            listeners.get(index).apply(new Pair<>(world, world.equals(Worlds.WorldNightmare))).thenAccept(o -> {
                if (listeners.size() > index + 1) {
                    callListenerCore(index + 1, world);
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
