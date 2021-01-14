package fr.davidson.diff.jjoules.localization.output;

import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JSONReportTest {

    @BeforeEach
    void setUp() {
        new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SELECTED_TESTS).delete();
        new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SUSPECT_LINES).delete();
    }

    @Test
    void testOutputSelectedTests() {
        assertFalse(new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SELECTED_TESTS).exists());
        final JSONReport jsonReport = new JSONReport("target/trash/");
        final Map<String, List<String>> selection = new HashMap<>();
        selection.put("com.google.gson.functional.DefaultTypeAdaptersTest", new ArrayList<>());
        selection.get("com.google.gson.functional.DefaultTypeAdaptersTest").add("testDateSerializationWithPattern");
        final double globalDelta = 20936.0D;
        final Map<String, Double> deltaPerTest = new HashMap<>();
        deltaPerTest.put("com.google.gson.DefaultDateTypeAdapterTest-testDateDeserializationISO8601", -61.0);
        //jsonReport.outputSelectedTests(selection, globalDelta, deltaPerTest); TODO
        assertTrue(new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SELECTED_TESTS).exists());
        final Map<String, List<String>>  actual =
                JSONUtils.read("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SELECTED_TESTS, selection.getClass());
        assertEquals(1, actual.size());
        assertTrue(actual.containsKey("com.google.gson.functional.DefaultTypeAdaptersTest"));
        assertEquals(1, actual.get("com.google.gson.functional.DefaultTypeAdaptersTest").size());
        assertEquals("testDateSerializationWithPattern", actual.get("com.google.gson.functional.DefaultTypeAdaptersTest").get(0));
    }

    @Test
    void testOutputSuspectLines() throws FileNotFoundException {
        assertFalse(new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SUSPECT_LINES).exists());
        final JSONReport jsonReport = new JSONReport("target/trash/");
        final Map<String, List<Integer>> suspectLines = new HashMap<>();
        suspectLines.put("className1", new ArrayList<>());
        suspectLines.get("className1").addAll(Arrays.asList(1, 3, 6));
        suspectLines.put("className2", new ArrayList<>());
        suspectLines.get("className2").addAll(Arrays.asList(16, 31, 63));
        jsonReport.outputSuspectLines(suspectLines);
        assertTrue(new File("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SUSPECT_LINES).exists());
        final Map<String, List<Double>> actual =
                JSONUtils.read("target/trash/" + JSONReport.OUTPUT_PATH_NAME_SUSPECT_LINES, suspectLines.getClass());
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
