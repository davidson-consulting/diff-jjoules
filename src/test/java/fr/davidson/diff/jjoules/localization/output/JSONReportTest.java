package fr.davidson.diff.jjoules.localization.output;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONReportTest {

    @Test
    void testOutput() throws FileNotFoundException {
        final JSONReport jsonReport = new JSONReport("target/trash/");
        final Map<String, List<Integer>> faultyLines = new HashMap<>();
        faultyLines.put("className1", new ArrayList<>());
        faultyLines.get("className1").addAll(Arrays.asList(1, 3, 6));
        faultyLines.put("className2", new ArrayList<>());
        faultyLines.get("className2").addAll(Arrays.asList(16, 31, 63));
        jsonReport.output(faultyLines);
        assertTrue(new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME).exists());
        final Map<String, List<Double>> actual =
                new Gson().fromJson(new FileReader("target/trash/" + JSONReport.OUTPUT_PATH_NAME), faultyLines.getClass());
        assertTrue(actual.containsKey("className1"));
        assertTrue(actual.get("className1").contains(1.0));
        assertTrue(actual.get("className1").contains(3.0));
        assertTrue(actual.get("className1").contains(6.0));
        assertEquals(3, actual.get("className1").size());
        assertTrue(actual.containsKey("className2"));
        assertTrue(actual.get("className2").contains(16.0));
        assertTrue(actual.get("className2").contains(31.0));
        assertTrue(actual.get("className2").contains(63.0));
        assertEquals(3, actual.get("className2").size());
    }
}
