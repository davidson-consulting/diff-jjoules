package fr.davidson.diff.jjoules.util;

import com.atlassian.clover.recorder.ActivePerTestRecorderOne;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class CSVReader {

    public static Map<String, List<String>> readFile(String path) {
        final HashMap<String, List<String>> result = new HashMap<>();
        if (path == null || path.isEmpty()) {
            return result;
        }
        try (final BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.lines().forEach(line -> {
                final String[] split = line.split(";");
                if (!split[0].toLowerCase(Locale.ROOT).contains("concurrency")) {
                    result.put(split[0], new ArrayList<>());
                    for (int i = 1; i < split.length; i++) {
                        result.get(split[0]).add(split[i]);
                    }
                }
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
