package com.incrementalqol.config;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.incrementalqol.modules.OptionsModule.MOD_ID;

public class ConfigHandler {



    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("incremental-qol" + ".json5");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Static instance of the configuration
    private static Config configInstance;

    // Load the configuration and initialize the static instance
    static {
        try {
            configInstance = loadOptions();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize configuration: {}", e.getMessage(), e);
            configInstance = new Config(true, 0, 0, false,1,150,false, false); // Fallback to default config
        }
    }

    // Save the configuration to a file
    public static void saveOptions() {
        try {
            DataResult<JsonElement> result = Config.CODEC.encodeStart(JsonOps.INSTANCE, configInstance);

            JsonElement json = result.resultOrPartial(LOGGER::error).orElseThrow(() ->
                    new IOException("Failed to encode Config to JSON")
            );

            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, json.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            LOGGER.info("Configuration saved successfully to {}", CONFIG_PATH);

        } catch (IOException e) {
            LOGGER.error("Failed to save configuration: {}", e.getMessage(), e);
        }
    }

    // Load the configuration from a file
    private static Config loadOptions() throws IOException {
        if (Files.exists(CONFIG_PATH)) {
            String json = Files.readString(CONFIG_PATH);

            JsonElement jsonElement = JsonParser.parseString(json);

            DataResult<Config> result = Config.CODEC.parse(JsonOps.INSTANCE, jsonElement);

            return result.resultOrPartial(LOGGER::error).orElseThrow(() ->
                    new IOException("Failed to decode Config from JSON")
            );
        } else {
            LOGGER.warn("Configuration file not found. Creating default configuration.");
            Config defaultConfig = new Config(true, 0, 0, false,1,150,false, false);
            saveDefaultConfig(defaultConfig);
            return defaultConfig;
        }
    }

    // Save the default configuration to a file
    private static void saveDefaultConfig(Config defaultConfig) throws IOException {
        DataResult<JsonElement> result = Config.CODEC.encodeStart(JsonOps.INSTANCE, defaultConfig);

        JsonElement json = result.resultOrPartial(LOGGER::error).orElseThrow(() ->
                new IOException("Failed to encode default Config to JSON")
        );

        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, json.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        LOGGER.info("Default configuration saved to {}", CONFIG_PATH);
    }

    // Getter for the static config instance
    public static Config getConfig() {
        return configInstance;
    }

    // Update and save the configuration
    public static void updateConfig(Config newConfig) {
        configInstance = newConfig;
        saveOptions();
    }
}