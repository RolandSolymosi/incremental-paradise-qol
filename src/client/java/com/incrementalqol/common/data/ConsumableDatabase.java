package com.incrementalqol.common.data;

import java.util.HashMap;
import java.util.Map;

public class ConsumableDatabase {
    // Database: consumable name -> duration in seconds
    private static final Map<String, Integer> database = new HashMap<>();

    static {
        ConsumableDatabase.addConsumable("Carrot Cake", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Fruit Basket", 300); // 5 minutes
        ConsumableDatabase.addConsumable("Pecan Pie", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Peking Duck", 300); // 5 minutes
        ConsumableDatabase.addConsumable("Potato Chips", 300); // 5 minutes
        ConsumableDatabase.addConsumable("Cheerios", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Cheese Burger", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Cooked Fish", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Ice Cream", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Pancakes", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Poppy Seed Bagel", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Taco", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Bread Basket", 900); // 15 minutes
        ConsumableDatabase.addConsumable("Coconut Milk", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Miso Soup", 900); // 15 minutes
        ConsumableDatabase.addConsumable("PB & J", 900); // 15 minutes
        ConsumableDatabase.addConsumable("Rabbit Stew", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Stuffed Bell Pepper", 900); // 15 minutes
        ConsumableDatabase.addConsumable("Twinkie", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Omelet", 600); // 10 minutes
        ConsumableDatabase.addConsumable("Snow Cone", 1200); // 20 minutes
        ConsumableDatabase.addConsumable("Sriracha Hot Sauce", 900); // 15 minutes
        ConsumableDatabase.addConsumable("Donut", 1200); // 20 minutes
    }

    public static void addConsumable(String name, int durationSeconds) {
        database.put(name, durationSeconds);
    }

    public static Integer getDuration(String name) {
        return database.get(name);
    }

    public static Map<String, Integer> getDatabase() {
        return database;
    }
}

