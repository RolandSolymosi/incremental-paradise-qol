package com.incrementalqol.common.data;

import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Optional;

public enum World {
    Hub(Identifier.of("minecraft", "worldhub"), Realm.Hub),
    Overworld(Identifier.of("minecraft", "overworld"), Realm.Unknown),
    World1(Identifier.of("minecraft", "world1"), Realm.Normal),
    World2(Identifier.of("minecraft", "world2"), Realm.Normal),
    World3(Identifier.of("minecraft", "world3"), Realm.Normal),
    WorldNightmare(Identifier.of("minecraft", "worldnightmare"), Realm.Nightmare);


    private final Identifier id;
    private final Realm realm;

    World(Identifier id, Realm realm) {
        this.id = id;
        this.realm = realm;
    }

    public Identifier getId() {
        return id;
    }

    public Realm getRealm() {
        return realm;
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
}