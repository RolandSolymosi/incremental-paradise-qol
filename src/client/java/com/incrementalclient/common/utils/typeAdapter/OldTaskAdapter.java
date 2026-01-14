package com.incrementalclient.common.utils.typeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import com.incrementalclient.common.data.tasks.Task;

public class OldTaskAdapter extends TypeAdapter<Task>{
    public Task read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String taskString = reader.nextString();

        try {
            return Task.valueOf(taskString);
        } catch (IllegalArgumentException e) {
            Task returnTask = switch (taskString) {
                case "Items" -> Task.SellItems;
                case "Dice" -> Task.EarnTicketDice;
                case "Pixelpop" -> Task.EarnTicketPixelpop;
                case "Matcher" -> Task.EarnTicketMatcher;
                default -> null;
            };
            System.out.println("Non-matching task: " + taskString);
            return returnTask; // or return a default value
        }
    }
    public void write(JsonWriter writer, Task task) {
        // We are never writing tasks (in this case)
        return;
    }
}
