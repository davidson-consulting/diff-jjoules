package fr.davidson.diff.jjoules.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static final String TEST_FOLDER_PATH = "src/test/java/";

    public static String readClasspathFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining(":"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String toFullQualifiedName(String className, String methodName) {
        return className + "#" + methodName;
    }

    public static <T> void addToGivenMap(final String key, T value, Map<String, List<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new ArrayList<>());
        }
        givenMap.get(key).add(value);
    }
}
