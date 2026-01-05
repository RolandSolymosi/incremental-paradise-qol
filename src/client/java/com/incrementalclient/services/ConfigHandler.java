package com.incrementalclient.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import com.incrementalclient.interfaces.Configurable;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigHandler {
    private final MinecraftScreenAccessor screenAccessor;
    private final Configurable<?, ?>[] configurableServices;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("incremental-qol-v2.json5");
    private final Gson gson = new GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .setPrettyPrinting()
            .create();
    private final YetAnotherConfigLib.Builder screenBuilder;

    public ConfigHandler(MinecraftScreenAccessor screenAccessor, KeyBindMonitor keyBindMonitor, Configurable<?, ?>[] configurableServices){
        this.screenAccessor = screenAccessor;
        this.configurableServices = configurableServices;
        var keyBind = new KeyBinding(
                "Options Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "Incremental QOL"
        );
        keyBindMonitor.subscribe(new KeyBindMonitor.KeyBindListener(keyBind, this::open));
        KeyBindingHelper.registerKeyBinding(keyBind);

        screenBuilder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Incremental Qol"))
                .save(this::save);

        Map<String, List<Configurable<?, ?>>> grouped = Arrays.stream(configurableServices)
                .collect(Collectors.groupingBy(
                        Configurable::getCategory,
                        TreeMap::new,
                        Collectors.toList()
                ));

        for (var entry : grouped.entrySet()) {
            ConfigCategory.Builder catBuilder = ConfigCategory.createBuilder()
                    .name(Text.of(entry.getKey()));

            List<Configurable<?, ?>> sortedOptions = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(c -> ((Configurable<?, ?>)c).getOrder())
                            .thenComparing(c -> ((Configurable<?, ?>)c).getConfiguration().getClass().getSimpleName()))
                    .toList();

            for (Configurable<?, ?> conf : sortedOptions) {
                catBuilder.option(conf.getOption());
            }

            screenBuilder.category(catBuilder.build());
        }

        load();
    }

    private void open(){
        screenAccessor.setScreen(screenBuilder.build().generateScreen(screenAccessor.getScreen().isPresent() ? screenAccessor.getScreen().get() : null));
    }

    private void save() {
        JsonObject root = new JsonObject();
        for (Configurable<?, ?> conf : configurableServices) {
            root.add(conf.getJsonSection(), gson.toJsonTree(conf.getConfiguration()));
            conf.optionChanged();
        }

        try {
            Files.writeString(configPath, gson.toJson(root));
        } catch (IOException e) {
            System.err.println("[Config] Failed to save config: " + e.getMessage());
        }
    }

    private void load() {
        if (!Files.exists(configPath)) return;

        try {
            String content = Files.readString(configPath);
            JsonObject root = gson.fromJson(content, JsonObject.class);
            if (root == null) return;

            for (Configurable<?, ?> conf : configurableServices) {
                String section = conf.getJsonSection();
                if (root.has(section)) {
                    Object loadedData = gson.fromJson(root.get(section), conf.getConfiguration().getClass());
                    conf.copyFrom(loadedData);
                }
            }
        } catch (Exception e) {
            System.err.println("[Config] Failed to load config: " + e.getMessage());
        }
    }
}
