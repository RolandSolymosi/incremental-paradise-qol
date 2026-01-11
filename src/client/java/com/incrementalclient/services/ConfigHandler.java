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

        // 1. Flatten all options from all services
        List<ConfigPiece> allPieces = Arrays.stream(configurableServices)
                .filter(Configurable::hasOption)
                .flatMap(service -> service.getOption().stream().map(option -> new ConfigPiece(option, service)))
                .toList();

        // 2. Validate and Sort Category Names by their predefined CategoryOrder
        List<String> sortedCategoryNames = allPieces.stream()
                .map(p -> p.optionPiece().Category())
                .distinct()
                .sorted(Comparator.comparingInt(catName -> findFirst(allPieces, catName).CategoryOrder()))
                .toList();

        // Validation: Strict Category Order check
        for (int i = 0; i < sortedCategoryNames.size() - 1; i++) {
            var p1 = findFirst(allPieces, sortedCategoryNames.get(i));
            var p2 = findFirst(allPieces, sortedCategoryNames.get(i + 1));
            if (p1.CategoryOrder() == p2.CategoryOrder()) {
                throw new IllegalStateException(String.format(
                        "Tab Order Collision: '%s' and '%s' both have CategoryOrder %d",
                        p1.Category(), p2.Category(), p1.CategoryOrder()));
            }
        }

        // 3. Build the Categories (Tabs)
        for (String catName : sortedCategoryNames) {
            var catPieces = allPieces.stream()
                    .filter(p -> p.optionPiece().Category().equals(catName))
                    .toList();

            var firstInCat = catPieces.get(0).optionPiece();

            ConfigCategory.Builder catBuilder = ConfigCategory.createBuilder()
                    .name(Text.of(catName))
                    .tooltip(Text.of(firstInCat.CategoryDescription()));

            // 4. Group items:
            // We group by (GroupName + GroupOrder) to allow multiple "Direct" slots
            // at different positions without them merging and causing "Inconsistent GroupOrder" errors.
            Map<String, List<ConfigPiece>> groups = catPieces.stream()
                    .collect(Collectors.groupingBy(p -> {
                        String gName = p.optionPiece().Group();
                        return gName.isEmpty() ? "DIRECT_SLOT_" + p.optionPiece().GroupOrder() : gName;
                    }));

            // Sort the Group Keys by the GroupOrder of the pieces inside
            List<String> sortedGroupKeys = groups.keySet().stream()
                    .sorted(Comparator.comparingInt(key -> groups.get(key).get(0).optionPiece().GroupOrder()))
                    .toList();

            // Validation: Strict Group Order check within this tab
            for (int i = 0; i < sortedGroupKeys.size() - 1; i++) {
                var g1Key = sortedGroupKeys.get(i);
                var g2Key = sortedGroupKeys.get(i + 1);
                int o1 = groups.get(g1Key).get(0).optionPiece().GroupOrder();
                int o2 = groups.get(g2Key).get(0).optionPiece().GroupOrder();

                if (o1 == o2) {
                    String name1 = groups.get(g1Key).get(0).optionPiece().Group();
                    String name2 = groups.get(g2Key).get(0).optionPiece().Group();
                    throw new IllegalStateException(String.format(
                            "Group Order Collision in Tab '%s': '%s' and '%s' both have GroupOrder %d",
                            catName, name1.isEmpty() ? "Direct Item" : name1, name2.isEmpty() ? "Direct Item" : name2, o1));
                }
            }

            // 5. Build the UI Groups and Options
            for (String gKey : sortedGroupKeys) {
                var groupPieces = groups.get(gKey);
                var sortedOptions = groupPieces.stream()
                        .sorted(Comparator.comparingInt(p -> p.optionPiece().Order()))
                        .toList();

                // Validation: Strict Option Order check within this specific group
                for (int i = 0; i < sortedOptions.size() - 1; i++) {
                    var p1 = sortedOptions.get(i).optionPiece();
                    var p2 = sortedOptions.get(i + 1).optionPiece();
                    if (p1.Order() == p2.Order()) {
                        String gName = p1.Group().isEmpty() ? "Direct" : p1.Group();
                        throw new IllegalStateException(String.format(
                                "Option Order Collision in %s -> %s: Order %d used twice",
                                catName, gName, p1.Order()));
                    }
                }

                String actualGroupName = groupPieces.get(0).optionPiece().Group();

                if (!actualGroupName.isEmpty()) {
                    // This is a visible Group Box
                    var groupBuilder = OptionGroup.createBuilder()
                            .name(Text.of(actualGroupName))
                            .description(OptionDescription.of(Text.of(sortedOptions.get(0).optionPiece().GroupDescription())));

                    for (var p : sortedOptions) {
                        groupBuilder.option(p.optionPiece().Option());
                    }
                    catBuilder.group(groupBuilder.build());
                } else {
                    // These are Direct items (no box)
                    for (var p : sortedOptions) {
                        catBuilder.option(p.optionPiece().Option());
                    }
                }
            }
            screenBuilder.category(catBuilder.build());
        }
    }

    private Configurable.OptionPiece findFirst(List<ConfigPiece> pieces, String catName) {
        return pieces.stream()
                .filter(p -> p.optionPiece().Category().equals(catName))
                .findFirst()
                .map(ConfigPiece::optionPiece)
                .orElseThrow();
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
