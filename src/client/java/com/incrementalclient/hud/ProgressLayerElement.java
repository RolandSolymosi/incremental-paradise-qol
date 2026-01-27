package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.data.ProgressLayer;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ProgressLayerElement extends HudElement<ProgressLayerElement.Configuration> {
    private static final int SPACING = 5; // Spacing between layers in the same row

    // Layer name colors
    private static final int COLOR_LEVEL_NAME = 0x00A800;
    private static final int COLOR_PRESTIGE_NAME = 0x00A8A8;
    private static final int COLOR_ASCENSION_NAME = 0xA80000;
    private static final int COLOR_TRANSCENDENCE_NAME = 0xFCA800;
    private static final int COLOR_NIGHTMARE_NAME = 0x540054;

    // Layer value colors
    private static final int COLOR_LEVEL_VALUE = 0x54FC54;
    private static final int COLOR_PRESTIGE_VALUE = 0x54FCFC;
    private static final int COLOR_ASCENSION_VALUE = 0xFC5454;
    private static final int COLOR_TRANSCENDENCE_VALUE = 0xFCFC54;
    private static final int COLOR_NIGHTMARE_VALUE = 0xA800A8;

    private final GameInfoMonitor gameInfoMonitor;

    private final ProgressLayerElement.Configuration configuration = new ProgressLayerElement.Configuration();

    private final Supplier<List<OptionPiece>> options;

    public ProgressLayerElement(
            GameInfoMonitor gameInfoMonitor,
            MinecraftClientAccessor uiAccessor,
            HudManager hudManager
    ) {
        super(uiAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;

        options = Suppliers.memoize(() -> List.of(
                Configurable.Categories.Hud.General.createConfig(1000,
                        Option.<Double>createBuilder()
                                .name(Text.of("Progress Layer HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the progress layer HUD background.")))
                                .binding(configuration.progressLayerHudBackgroundOpacity, () -> configuration.progressLayerHudBackgroundOpacity, newVal -> configuration.progressLayerHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build())
        ));
    }

    @Override
    public void render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();
        if (!isElementEnabled()) {
            return;
        }

        if (mcAccessor.getWindow().isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Get current layer data from persistent progress data
        var progressData = gameInfoMonitor.getPersistentProgressData();
        if (progressData == null) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        EnumMap<ProgressLayer, Text> layers = progressData.getAllLayers();
        if (layers.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Collect layer entries and sort by ProgressLayer enum order
        List<Map.Entry<ProgressLayer, Text>> layerEntries = new ArrayList<>(layers.entrySet());
        layerEntries.sort(Comparator.comparing(entry -> entry.getKey()));
        Collections.reverse(layerEntries);

        // Build styled layer texts with colors
        List<Text> layerTexts = new ArrayList<>();
        for (var entry : layerEntries) {
            ProgressLayer layerType = entry.getKey();
            Text layerText = entry.getValue();
            if (layerText != null && !layerText.getString().isEmpty()) {
                // Parse and style the layer text (format: "LayerName Value")
                Text styledText = buildLayerText(layerType, layerText.getString());
                layerTexts.add(styledText);
            }
        }

        if (layerTexts.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        var window = mcAccessor.getWindow();
        if (window.isEmpty()) {
            return;
        }

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        // Calculate layout: 2 layers per row
        int itemsPerRow = 2;
        int rowCount = (layerTexts.size() + itemsPerRow - 1) / itemsPerRow; // Ceiling division
        int lineHeight = 9;
        int rowSpacing = 2;

        // Calculate row widths (width of each row: item0 width + SPACING + item1 width)
        int[] rowWidths = new int[rowCount];
        for (int i = 0; i < layerTexts.size(); i++) {
            int row = i / itemsPerRow;
            int width = textRenderer.get().getWidth(layerTexts.get(i));
            if (i % itemsPerRow == 0) {
                // First item in row
                rowWidths[row] = width;
            } else {
                // Second item in row
                rowWidths[row] += SPACING + width;
            }
        }

        // Find max row width for bounding box
        int maxRowWidth = 0;
        for (int rowWidth : rowWidths) {
            maxRowWidth = Math.max(maxRowWidth, rowWidth);
        }

        int totalHeight = rowCount * lineHeight + (rowCount - 1) * rowSpacing;

        // Get position from anchor point and delta position
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        // Draw background
        int bgOpacity = getHudBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + maxRowWidth + HudConstants.BACKGROUND_PADDING, y + totalHeight + HudConstants.TEXT_PADDING_Y, color);
        }

//        // Draw layers in 2-column layout (each row's items positioned relative to each other)
//        int startX = x + HudConstants.TEXT_PADDING_X;
//        int currentY = y + 1;
//        for (int i = 0; i < layerTexts.size(); i++) {
//            int col = i % itemsPerRow;
//            Text layerText = layerTexts.get(i);
//
//            int currentX = startX;
//            if (col == 1) {
//                // Position second item right after first item in the same row
//                Text firstItemText = layerTexts.get(i - 1);
//                int firstItemWidth = textRenderer.get().getWidth(firstItemText);
//                currentX = startX + firstItemWidth + SPACING;
//            }
//
//            context.drawText(textRenderer.get(), layerText, currentX, currentY, 0xFFFFFFFF, true);
//
//            // Move to next row after 2 items
//            if (col == 1) {
//                currentY += lineHeight + rowSpacing;
//            }
        MutableText layerText = Text.literal("");

        for (int i = 0; i < layerTexts.size(); i++) {
            Text currentLayerText = layerTexts.get(i);
            layerText.append(currentLayerText).append(" ");
        }

        renderBarText(context, textRenderer.get(), layerText, x, y);
//        context.drawText(textRenderer.get(), layerText, x, y, 0xFFFFFFFF, true);
    }


    /**
     * Builds a styled Text for a layer with appropriate colors for name and value.
     * Parses "LayerName Value" format and applies colors.
     */
    private Text buildLayerText(ProgressLayer layerType, String layerString) {
        // Parse the layer string (format: "LayerName Value" or "Pr 3" etc.)
        // Find the last space to separate name from value
        int lastSpaceIndex = layerString.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            // No space found, just return the text with name color
            return Text.literal(layerString).styled(s -> s.withColor(getLayerNameColor(layerType)));
        }

        String layerName = layerString.substring(0, lastSpaceIndex);
        String layerValue = layerString.substring(lastSpaceIndex + 1);

        Text nameText = Text.literal(layerName).styled(s -> s.withColor(getLayerNameColor(layerType)));
        Text valueText = Text.literal(layerValue).styled(s -> s.withColor(getLayerValueColor(layerType)));

        return Text.literal("").append(nameText).append(" ").append(valueText);
    }

    /**
     * Gets the color for a layer name based on the layer type.
     */
    private int getLayerNameColor(ProgressLayer layerType) {
        return switch (layerType) {
            case LEVEL -> COLOR_LEVEL_NAME;
            case PRESTIGE -> COLOR_PRESTIGE_NAME;
            case ASCENSION -> COLOR_ASCENSION_NAME;
            case TRANSCENDENCE -> COLOR_TRANSCENDENCE_NAME;
            case NIGHTMARE -> COLOR_NIGHTMARE_NAME;
        };
    }

    /**
     * Gets the color for a layer value based on the layer type.
     */
    private int getLayerValueColor(ProgressLayer layerType) {
        return switch (layerType) {
            case LEVEL -> COLOR_LEVEL_VALUE;
            case PRESTIGE -> COLOR_PRESTIGE_VALUE;
            case ASCENSION -> COLOR_ASCENSION_VALUE;
            case TRANSCENDENCE -> COLOR_TRANSCENDENCE_VALUE;
            case NIGHTMARE -> COLOR_NIGHTMARE_VALUE;
        };
    }

    private int getHudBackgroundOpacity() {
        return (int) (getConfiguration().progressLayerHudBackgroundOpacity * 255);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        int placeholderWidth = HudConstants.PLACEHOLDER_WIDTH_MEDIUM;
        int placeholderHeight = 15;

        int bgOpacity = getHudBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + placeholderWidth, y + placeholderHeight, color);
        }

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }
        context.drawText(textRenderer.get(), Text.literal("Progress Layer Tracker (Preview)"),
                x + HudConstants.TEXT_PADDING_X,
                y + 1,
                0xFFFFFFFF,
                true);
    }

    @Override
    public Vector2f getBoundingBox() {
        if (!isElementEnabled()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var window = mcAccessor.getWindow();
        if (window.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var progressData = gameInfoMonitor.getPersistentProgressData();
        if (progressData == null) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        EnumMap<ProgressLayer, Text> layers = progressData.getAllLayers();
        if (layers.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        // Collect layer entries and sort by ProgressLayer enum order
        List<Map.Entry<ProgressLayer, Text>> layerEntries = new ArrayList<>(layers.entrySet());
        layerEntries.sort(Comparator.comparing(entry -> entry.getKey()));

        // Build styled layer texts
        List<Text> styledLayerTexts = new ArrayList<>();
        for (var entry : layerEntries) {
            ProgressLayer layerType = entry.getKey();
            Text layerText = entry.getValue();
            if (layerText != null && !layerText.getString().isEmpty()) {
                Text styledText = buildLayerText(layerType, layerText.getString());
                styledLayerTexts.add(styledText);
            }
        }

        // Calculate layout: 2 layers per row
        int itemsPerRow = 2;
        int rowCount = (styledLayerTexts.size() + itemsPerRow - 1) / itemsPerRow; // Ceiling division
        int lineHeight = 9;
        int rowSpacing = 2;

        // Calculate row widths (width of each row: item0 width + SPACING + item1 width)
        int[] rowWidths = new int[rowCount];
        for (int i = 0; i < styledLayerTexts.size(); i++) {
            int row = i / itemsPerRow;
            int width = textRenderer.get().getWidth(styledLayerTexts.get(i));
            if (i % itemsPerRow == 0) {
                // First item in row
                rowWidths[row] = width;
            } else {
                // Second item in row
                rowWidths[row] += SPACING + width;
            }
        }

        // Find max row width for bounding box
        int maxRowWidth = 0;
        for (int rowWidth : rowWidths) {
            maxRowWidth = Math.max(maxRowWidth, rowWidth);
        }

        int totalHeight = rowCount * lineHeight + (rowCount - 1) * rowSpacing;

        return new Vector2f(maxRowWidth + HudConstants.BACKGROUND_PADDING, totalHeight + HudConstants.TEXT_PADDING_Y);
    }

    @Override
    public String getDisplayName() {
        return "Progress Layer Tracker";
    }

    @Override
    public String getJsonSection() {
        return "progressLayerHud";
    }

    @Override
    public ProgressLayerElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return options.get();
    }

    public static class Configuration extends HudElement.ConfigurationBase {
        @SerialEntry
        public double progressLayerHudBackgroundOpacity = 0.3;
    }
}
