package com.incrementalclient.common.utils.typeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.awt.Color;

public class OldColorAdapter extends TypeAdapter<Color> {
    public Color read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String colorString = reader.nextString();
        int colorInt = Integer.parseInt(colorString);
        return new Color(colorInt);
    }
    public void write(JsonWriter writer, Color color) {
        // We are never writing colors
        return;
    }
}
