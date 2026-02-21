package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.incrementalclient.services.GameInfoMonitor;
import net.minecraft.text.Text;

import java.util.*;

public class ProgressLayerText {

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

    public static List<Text> getProgressLayerText(GameInfoMonitor.PlayerProgressData progressData) {

        EnumMap<com.incrementalclient.common.data.ProgressLayer, Text> layers = progressData.getAllLayers();

        // Collect layer entries and sort by ProgressLayer enum order
        List<Map.Entry<com.incrementalclient.common.data.ProgressLayer, Text>> layerEntries = new ArrayList<>(layers.entrySet());
        layerEntries.sort(Comparator.comparing(entry -> entry.getKey()));
        Collections.reverse(layerEntries);

        // Build styled layer texts with colors
        List<Text> layerTexts = new ArrayList<>();
        for (var entry : layerEntries) {
            com.incrementalclient.common.data.ProgressLayer layerType = entry.getKey();
            Text layerText = entry.getValue();
            if (layerText != null && !layerText.getString().isEmpty()) {
                // Parse and style the layer text (format: "LayerName Value")
                Text styledText = buildLayerText(layerType, layerText.getString());
                layerTexts.add(styledText);
            }
        }

        return layerTexts;
    }

    /**
     * Builds a styled Text for a layer with appropriate colors for name and value.
     * Parses "LayerName Value" format and applies colors.
     */
    private static Text buildLayerText(com.incrementalclient.common.data.ProgressLayer layerType, String layerString) {
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
    private static int getLayerNameColor(com.incrementalclient.common.data.ProgressLayer layerType) {
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
    private static int getLayerValueColor(com.incrementalclient.common.data.ProgressLayer layerType) {
        return switch (layerType) {
            case LEVEL -> COLOR_LEVEL_VALUE;
            case PRESTIGE -> COLOR_PRESTIGE_VALUE;
            case ASCENSION -> COLOR_ASCENSION_VALUE;
            case TRANSCENDENCE -> COLOR_TRANSCENDENCE_VALUE;
            case NIGHTMARE -> COLOR_NIGHTMARE_VALUE;
        };
    }
}
