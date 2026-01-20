package com.incrementalclient.internals;

import com.incrementalclient.common.data.ItemType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This is ALMOST a wrapper for ItemCooldownManager since ItemCooldownManager pretty much instantly and only
 * calls this class.
 * This is ALMOST a high-level service because of all the abstraction away I do using ItemTypes.
 */
public class ItemCooldownWrapper {

    private final Map<ItemType, Float> itemTypeCooldown = new HashMap<>();

    public ItemCooldownWrapper() {
        
    }

    public Optional<Float> getItemCooldown(ItemStack stack) {
        var itemType = ItemType.getItemType(stack);
        return Optional.ofNullable(itemTypeCooldown.getOrDefault(itemType, null));
    }

    public void setItemCooldown(ItemType type, float cooldown) {
        itemTypeCooldown.put(type, cooldown);
    }

    public void clearItemCooldown(ItemType type) {
        itemTypeCooldown.remove(type);
    }
}
