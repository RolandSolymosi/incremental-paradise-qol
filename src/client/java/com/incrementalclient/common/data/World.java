package com.incrementalclient.common.data;

import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Optional;

public enum World {
    Hub("Hub", Identifier.of("minecraft", "worldhub"), Realm.Hub),
    Overworld("Overworld", Identifier.of("minecraft", "overworld"), Realm.Unknown),
    BossArenas("Boss Arena", Identifier.of("minecraft", "bossarenas"), Realm.Normal),
    CrabIsland("Crab Island",Identifier.of("minecraft", "hermitworld"), Realm.Normal),
    World1("World 1", Identifier.of("minecraft", "world1"), Realm.Normal),
    World2("World 2", Identifier.of("minecraft", "world2"), Realm.Normal),
    World3("World 3", Identifier.of("minecraft", "world3"), Realm.Normal),
    World4("World 4", Identifier.of("minecraft", "world4"), Realm.Normal),
    WorldNightmare1("Nightmare 1", Identifier.of("minecraft", "worldnightmare"), Realm.Nightmare);


    private final String name;
    private final Identifier id;
    private final Realm realm;

    World(String name, Identifier id, Realm realm) {
        this.name = name;
        this.id = id;
        this.realm = realm;
    }

    public Identifier getId() {
        return id;
    }

    public Realm getRealm() {
        return realm;
    }

    public String getName() {
        return name;
    }

    public enum Realm {
        Unknown,
        Hub,
        Normal,
        Nightmare,
    }

    public static Optional<World> findById(Identifier id) {
        return Arrays.stream(values())
                .filter(e -> e.id.equals(id))
                .findFirst();
    }

    public static Optional<World> find(net.minecraft.world.World world) {
        return Arrays.stream(values())
                .filter(e -> e.id.equals(world.getRegistryKey().getValue()))
                .findFirst();
    }
}