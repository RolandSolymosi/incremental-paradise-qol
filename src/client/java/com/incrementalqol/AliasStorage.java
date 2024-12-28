package com.incrementalqol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AliasStorage {
    private static final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "incremental_paradise_aliases.json");
    private static final Gson parser = new Gson();
    private static Map<String, String> aliases = new HashMap<>();

    public static void load() {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> loadedAliases = parser.fromJson(reader, type);
                if (loadedAliases != null) {
                    aliases = loadedAliases; // store loaded data in our field
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void save() {
        try (FileWriter writer = new FileWriter(file)) {
            parser.toJson(aliases, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAlias(String alias, String commands) {
        aliases.put(alias, commands);
        save();
    }

    public static String getCommands(String alias) {
        return aliases.get(alias);
    }

    public static Map<String, String> getAliases() {
        return aliases;
    }
}
