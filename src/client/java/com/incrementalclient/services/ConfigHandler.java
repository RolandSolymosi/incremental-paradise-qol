package com.incrementalclient.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import com.incrementalclient.interfaces.ComplexConfigurable;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.ExternalConfigurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.MinecraftClientAccessor;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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
    private final MinecraftClientAccessor screenAccessor;
    private final Configurable<?>[] configurableServices;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("incremental-qol-v2.json5");
    private final Gson gson = new GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .setPrettyPrinting()
            .create();
    private final YetAnotherConfigLib.Builder screenBuilder;

    public ConfigHandler(MinecraftClientAccessor screenAccessor, KeyBindMonitor keyBindMonitor, Configurable<?>[] configurableServices) {
        this.screenAccessor = screenAccessor;
        this.configurableServices = configurableServices;

        load();

        new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Options Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "Incremental QOL"
        ), this::open, true);

        screenBuilder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Incremental Qol"))
                .save(this::save);

        var listener = new Listener.DefaultListener(this::save);
        for (Configurable<?> conf : configurableServices) {
            if (conf instanceof ExternalConfigurable<?> externalConfigurable) {
                externalConfigurable.subscribe(listener);
            }
            if (conf instanceof ComplexConfigurable<?> complexConfigurable) {
                complexConfigurable.subscribe(listener);
            }
        }

        var groupedData = Arrays.stream(configurableServices)
                .filter(Configurable::hasOption)
                .flatMap(service -> service.getOption().stream().map(option -> new ConfigPiece(option, service)))
                .collect(Collectors.groupingBy(
                        o -> o.optionPiece.Category(),
                        TreeMap::new,
                        Collectors.groupingBy(
                                o -> o.optionPiece.Group(),
                                TreeMap::new,
                                Collectors.toList()
                        )
                ));

        for (var category : groupedData.entrySet()) {
            ConfigCategory.Builder catBuilder = ConfigCategory.createBuilder()
                    .name(Text.of(category.getKey()));

            var sortedGroups = category.getValue().entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String, List<ConfigPiece>>, Integer>comparing(group ->
                                    group.getValue().stream()
                                            .mapToInt(sp -> sp.optionPiece().GroupOrder())
                                            .min()
                                            .orElse(0))
                            .thenComparing(Map.Entry::getKey))
                    .toList();

            for (var group : sortedGroups) {
                var sortedOptions = group.getValue().stream()
                        .sorted(Comparator.comparingInt(sp -> sp.optionPiece.Order()))
                        .toList();

                if (!group.getKey().isEmpty()) {
                    var groupBuilder = OptionGroup.createBuilder()
                            .name(Text.of(group.getKey()))
                            .description(OptionDescription.of(Text.of(group.getValue().stream().map(g -> g.optionPiece.GroupDescription()).filter(g -> !g.isEmpty()).collect(Collectors.joining("\n")))));

                    for (var option : sortedOptions) {
                        groupBuilder.option(option.optionPiece().Option());
                    }

                    catBuilder.group(groupBuilder.build());
                } else {
                    for (var option : sortedOptions) {
                        catBuilder.option(option.optionPiece().Option());
                    }
                }
            }

            screenBuilder.category(catBuilder.build());
        }
    }

    private void open() {
        screenAccessor.setScreen(screenBuilder.build().generateScreen(screenAccessor.getScreen().isPresent() ? screenAccessor.getScreen().get() : null));
    }

    public void save() {
        JsonObject root = new JsonObject();
        for (Configurable<?> conf : configurableServices) {
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

            for (Configurable<?> conf : configurableServices) {
                String section = conf.getJsonSection();
                if (root.has(section)) {
                    var classes = conf.getConfiguration().getClass();
                    Object loadedData = gson.fromJson(root.get(section), conf.getConfiguration().getClass());
                    conf.copyFrom(loadedData);
                }
            }
        } catch (Exception e) {
            System.err.println("[Config] Failed to load config: " + e.getMessage());
        }
    }

    private record ConfigPiece(Configurable.OptionPiece optionPiece, Configurable<?> configurable) {
    }
}
