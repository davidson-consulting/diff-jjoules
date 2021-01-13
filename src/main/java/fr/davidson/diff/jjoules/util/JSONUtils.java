package fr.davidson.diff.jjoules.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class JSONUtils {

    public static <T> T read(final String path, Class<T> clazz) {
        try {
            return new Gson().fromJson(new FileReader(path), clazz);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(final String path, Object toBeWritten) {
        write(path, toBeWritten, false);
    }

    public static void write(final String path, Object toBeWritten, boolean append) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (final FileWriter fileWriter = new FileWriter(path, append)) {
            fileWriter.write(gson.toJson(toBeWritten));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
