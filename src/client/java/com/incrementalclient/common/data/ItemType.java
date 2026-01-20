package com.incrementalclient.common.data;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum ItemType {
    // Default value (available so that NotNull functions can still return something)
    UNKNOWN,
    // All tools
    TOOL_SWORD,
    TOOL_PICK,
    TOOL_AXE,
    TOOL_HOE,
    TOOL_SPEAR,
    TOOL_TONIC,
    TOOL_BOW,
    TOOL_BRUSH;
    // Note: All are prefixed with "tool" incase a non-tool (crate keys? consumables? pets?) end up being wanted

    // Map from [Internal name of item type -> ItemType object]
    private static final Map<String, ItemType> itemTypeMap = Map.ofEntries(
            // technically, unknown isn't necessary, but Optional.getString() can use this as a fallback
            // (Helps speed things up, slightly. I think.)
            Map.entry("unknown", UNKNOWN),

            Map.entry("sword", TOOL_SWORD),
            Map.entry("pickaxe", TOOL_PICK),
            Map.entry("axe", TOOL_AXE),
            Map.entry("hoe", TOOL_HOE),
            Map.entry("fishingSpear", TOOL_SPEAR),
            Map.entry("tonic", TOOL_TONIC),
            Map.entry("ranged", TOOL_BOW),
            Map.entry("brush", TOOL_BRUSH)
    );

    public static @NotNull ItemType getItemType(ItemStack stack) {
        var components = stack.getComponents();
        var customData = components.getOrDefault(DataComponentTypes.CUSTOM_DATA, null);
        if(customData == null) {
            return UNKNOWN;
        }
        var customCompound = customData.copyNbt();
        var bukkitValuesOptional = customCompound.getCompound("PublicBukkitValues");
        if(bukkitValuesOptional.isEmpty()) {
            return UNKNOWN;
        }
        var bukkitValues = bukkitValuesOptional.get();
        var itemTypeString = bukkitValues.getString("prisoncore:itemtype", "unknown");
        return itemTypeMap.getOrDefault(itemTypeString, UNKNOWN);
    }
}
