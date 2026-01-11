package com.incrementalclient.featues.Tasking;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.Task;
import com.incrementalclient.config.controllers.ComplexTypeController;
import com.incrementalclient.config.InsertableListOption;
import com.incrementalclient.interfaces.ComplexConfigurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.MinecraftClientAccessor;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

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

    private List<OptionPiece> createScreen(){
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
                        .textProvider(opt -> Text.of((opt.task == null ? "N/A" : opt.task.name())))
                        .screenFactory(opt -> YetAnotherConfigLib.createBuilder()
                                .title(Text.of("Edit Override Settings"))
                                .category(ConfigCategory.createBuilder()
                                        .name(Text.of("Overrides"))
                                        .option(Option.<Task>createBuilder()
                                                .name(Text.of("Task"))
                                                .binding(opt.task, () -> opt.task, val -> opt.task = val)
                                                .controller(t -> EnumDropdownControllerBuilder.create(t)
                                                        .formatValue(v -> Text.of(v.getDescriptor().names().getFirst() + (!v.getDescriptor().constraints().isEmpty()
                                                                ? "[ "+ v.getDescriptor().constraints().stream().map(Constraint::getName).collect(Collectors.joining(", "))+ " ]"
                                                                : "")))
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
                                        .option(Option.<Integer>createBuilder()
                                                .name(Text.of("Tool slot"))
                                                .binding(opt.toolSlotId, () -> opt.toolSlotId, val -> opt.toolSlotId = val)
                                                .controller(i -> IntegerSliderControllerBuilder.create(i).step(1).range(-1, 7))
                                                .build())
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Text.of("Invert Ticket task skip rule"))
                                                .binding(opt.skipTicket, () -> opt.skipTicket, val -> opt.skipTicket = val)
                                                .controller(BooleanControllerBuilder::create)
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

    @Override
    public String getJsonSection() {
        return "taskingOverrides";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public HashMap<Task, Configuration.Override> getOverrides(){
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
                        (existing, replacement)-> existing,
                        HashMap::new
                ));
    }

    static public class Configuration {

        @SerialEntry
        public List<Override> overrides = new java.util.ArrayList<>();

        public static class Override{
            @SerialEntry
            public Task task = Task.SellItems;
            @SerialEntry
            public String wardrobe = "";
            @SerialEntry
            public String pet = "";
            @SerialEntry
            public String warp = "";
            @SerialEntry
            public int toolSlotId = -1;
            @SerialEntry
            public boolean skipTicket = false;
        }
    }
}
