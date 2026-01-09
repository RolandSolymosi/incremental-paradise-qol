package com.incrementalclient.common.data;

import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Optional;

public enum World {
    Hub("Hub", "Hub",Identifier.of("minecraft", "worldhub"), Realm.Hub),
    Overworld("Overworld","Overworld", Identifier.of("minecraft", "overworld"), Realm.Unknown),
    BossArenas("Boss Arena","Boss", Identifier.of("minecraft", "bossarenas"), Realm.Normal),
    CrabIsland("Crab Island","Crab", Identifier.of("minecraft", "hermitworld"), Realm.Normal),
    World1("World 1", "W1",Identifier.of("minecraft", "world1"), Realm.Normal),
    World2("World 2", "W2",Identifier.of("minecraft", "world2"), Realm.Normal),
    World3("World 3", "W3",Identifier.of("minecraft", "world3"), Realm.Normal),
    World4("World 4", "W4",Identifier.of("minecraft", "world4"), Realm.Normal),
    WorldNightmare1("Nightmare 1","N1", Identifier.of("minecraft", "worldnightmare"), Realm.Nightmare);


    private final String name;
    private final String shortName;
    private final Identifier id;
    private final Realm realm;

    World(String name,  String shortName, Identifier id, Realm realm) {
        this.name = name;
        this.shortName = shortName;
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

    public String getShortName() {
        return shortName;
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