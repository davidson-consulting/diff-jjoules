package fr.davidson.diff.jjoules.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class CSVFileManager {

    public static Map<String, List<String>> readFile(String path) {
        final HashMap<String, List<String>> result = new HashMap<>();
        if (path == null || path.isEmpty()) {
            return result;
        }
        try (final BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.lines().forEach(line -> {
                final String[] split = line.split(";");
                if (!split[0].toLowerCase(Locale.ROOT).contains("concurrency")) {
                    result.put(split[0], new ArrayList<>(Arrays.asList(split).subList(1, split.length)));
                }
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
