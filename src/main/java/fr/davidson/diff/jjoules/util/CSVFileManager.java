package fr.davidson.diff.jjoules.util;

import fr.davidson.diff.jjoules.selection.SelectionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class CSVFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileManager.class);

    public static final String CSV_SEPARATOR = ";";

    public static List<String> formatTestListsToCSVLines(Map<String, Set<String>> testsList) {
        return testsList.keySet()
                .stream()
                .map(testClassName -> testClassName + CSV_SEPARATOR + String.join(CSV_SEPARATOR, testsList.get(testClassName)))
                .collect(Collectors.toList());
    }
    public static void writeFile(String path, List<String> lines) {
        LOGGER.info("Writing CSV file to {}", path);
        try (final FileWriter writer = new FileWriter(path)) {
            writer.write(Constants.joinLines(lines));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Set<String>> readFile(String path) {
        final HashMap<String, Set<String>> result = new HashMap<>();
        if (path == null || path.isEmpty()) {
            return result;
        }
        try (final BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.lines().forEach(line -> {
                final String[] split = line.split(";");
                if (!split[0].toLowerCase(Locale.ROOT).contains("concurrency")) {
                    result.put(split[0], new HashSet<>(Arrays.asList(split).subList(1, split.length)));
                }
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
