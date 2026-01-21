package com.incrementalclient.featues.Tasking;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.common.data.Tool;
import com.incrementalclient.common.data.tasks.Task;
import com.incrementalclient.common.utils.TextUtils;
import com.incrementalclient.config.InsertableListOption;
import com.incrementalclient.config.controllers.ComplexTypeController;
import com.incrementalclient.interfaces.ComplexConfigurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.MinecraftClientAccessor;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TaskingOverrides extends ListenableBase<Listener> implements ComplexConfigurable<TaskingOverrides.Configuration> {

    private final Configuration configuration = new Configuration();
    private final Supplier<List<OptionPiece>> options = Suppliers.memoize(this::createScreen);
    private final MinecraftClientAccessor screenAccessor;

    private HashMap<Task, TaskingOverrides.Configuration.Override> overrides = new HashMap<>();

    public TaskingOverrides(
            MinecraftClientAccessor screenAccessor
    ) {
        this.screenAccessor = screenAccessor;
    }

    private List<OptionPiece> createScreen() {
        return List.of(Categories.Tasking.createConfig(10000,
                InsertableListOption.<Configuration.Override>createBuilder()
                        .name(Text.literal("Task Specific Overrides"))
                        .binding(
                                configuration.overrides,
                                () -> configuration.overrides,
                                v -> configuration.overrides = v
                        )
                        .description(OptionDescription.of(Text.of("Options to override the default behaviour per task")))
                        .insertEntriesAtEnd(false)
                        .customController(o -> ComplexTypeController.create(o, screenAccessor)
                                .textProvider(opt -> (opt.task == null ? Text.of("Invalid Task Override") : getTextProvider(opt)))
                                .screenFactory(opt -> YetAnotherConfigLib.createBuilder()
                                        .title(Text.of("Edit Override Settings"))
                                        .category(ConfigCategory.createBuilder()
                                                .name(Text.of("Overrides"))
                                                .option(Option.<Task>createBuilder()
                                                        .name(Text.of("Task"))
                                                        .binding(opt.task, () -> opt.task, val -> opt.task = val)
                                                        .controller(t -> EnumDropdownControllerBuilder.create(t)
                                                                .formatValue(v -> Text.of(v.getDescriptor().displayName()))
                                                        )
                                                        .build())
                                                .option(Option.<String>createBuilder()
                                                        .name(Text.of("Warp"))
                                                        .binding(opt.warp, () -> opt.warp, val -> opt.warp = val)
                                                        .controller(StringControllerBuilder::create)
                                                        .build())
                                                .option(Option.<String>createBuilder()
                                                        .name(Text.of("Wardrobe"))
                                                        .binding(opt.wardrobe, () -> opt.wardrobe, val -> opt.wardrobe = val)
                                                        .controller(StringControllerBuilder::create)
                                                        .build())
                                                .option(Option.<String>createBuilder()
                                                        .name(Text.of("Pet"))
                                                        .binding(opt.pet, () -> opt.pet, val -> opt.pet = val)
                                                        .controller(StringControllerBuilder::create)
                                                        .build())
                                                .option(Option.<Tool>createBuilder()
                                                        .name(Text.of("Tool"))
                                                        .binding(opt.tool, () -> opt.tool, val -> opt.tool = val)
                                                        .controller(t -> EnumDropdownControllerBuilder.create(t)
                                                                .formatValue(v -> Text.of(v.name()))
                                                        )
                                                        .build())
                                                .option(Option.<TicketTaskOverride>createBuilder()
                                                        .name(Text.of("Ticket task Skip Behavior"))
                                                        .binding(opt.skipTicket, () -> opt.skipTicket, val -> opt.skipTicket = val)
                                                        .controller(t -> EnumControllerBuilder.create(t)
                                                                .enumClass(TicketTaskOverride.class)
                                                                .formatValue(v -> Text.of(v.getName()))
                                                        )
                                                        .build())
                                                .build())
                                        .save(this::notifyListeners))
                                .build()
                        )
                        .initial(Configuration.Override::new)
                        .collapsed(true)
                        .build()
        ));
    }

    public TicketTaskOverride getTicketTaskOverride(Task task) {
        return overrides.get(task) != null ? overrides.get(task).skipTicket : TicketTaskOverride.Default;
    }

    @Override
    public String getJsonSection() {
        return "taskingOverrides";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public HashMap<Task, Configuration.Override> getOverrides() {
        return overrides;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {
        overrides = getConfiguration().overrides.stream()
                .collect(Collectors.toMap(
                        o -> o.task,
                        o -> o,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    static public class Configuration {

        @SerialEntry
        public List<Override> overrides = new java.util.ArrayList<>();

        public static class Override {
            @SerialEntry
            public Task task = Task.SellItems;
            @SerialEntry
            public String wardrobe = "";
            @SerialEntry
            public String pet = "";
            @SerialEntry
            public String warp = "";
            @SerialEntry
            public Tool tool = Tool.Default;
            @SerialEntry
            public TicketTaskOverride skipTicket = TicketTaskOverride.Default;
        }
    }

    private Text getTextProvider(Configuration.Override override) {
        MutableText textProvider = Text.literal("");

        // TODO: Figure out better colors
        getConditionText(textProvider, true, "Task", override.task.getDescriptor().displayName(), 0xFA3B1, 0x12c4d5);
        getConditionText(textProvider, !override.wardrobe.isEmpty(), "Wardrobe", override.wardrobe, 0x8C351A, 0xb04221);
        getConditionText(textProvider, !override.warp.isEmpty(), "Warp", override.warp, 0x009A21, 0x00be29);
        getConditionText(textProvider, !override.pet.isEmpty(), "Pet", override.pet, 0x949F00, 0xb5c300);
        getConditionText(textProvider, override.tool != Tool.Default, "Tool", override.tool.name(), 0x7F8593,0x9da5b7);
        getConditionText(textProvider, override.skipTicket != TicketTaskOverride.Default, "TT", override.skipTicket.getName(), 0x8845D1, 0x9e50f5);

        return textProvider;
    }

    private void getConditionText(MutableText textProvider, boolean condition, String category, String valueIfTrue, int colorText, int colorValue) {
        if (condition) {
            textProvider.append(TextUtils.textColor(category + ": ", colorText));
            textProvider.append(TextUtils.textColor(valueIfTrue + " ", colorValue));
        }
    }

}
