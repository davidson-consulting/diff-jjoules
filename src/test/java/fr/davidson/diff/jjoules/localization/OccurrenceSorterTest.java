package fr.davidson.diff.jjoules.localization;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OccurrenceSorterTest {


    @Test
    void testGetOccurrencesPerSuspectLinesPerClassName() {
        Map<String, List<Integer>> suspectLines = new LinkedHashMap<>();
        suspectLines.put("className1", new ArrayList<>());
        suspectLines.get("className1").add(1);
        suspectLines.get("className1").add(1);
        suspectLines.get("className1").add(2);
        suspectLines.get("className1").add(3);
        suspectLines.get("className1").add(3);
        suspectLines.get("className1").add(3);
        suspectLines.put("className2", new ArrayList<>());
        suspectLines.get("className2").add(1);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(3);
        suspectLines.get("className2").add(3);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        final Map<String, Map<Integer, Integer>> occurrencePerSuspectLinesPerClassName =
                new OccurrenceSorter().getOccurrencePerSuspectLinesPerClassName(suspectLines);
        final ArrayList<Integer> className1 = new ArrayList<>(occurrencePerSuspectLinesPerClassName.get("className1").keySet());
        assertEquals(3, (int)className1.get(0));
        assertEquals(1, (int)className1.get(1));
        assertEquals(2, (int)className1.get(2));
        final ArrayList<Integer> className2 = new ArrayList<>(occurrencePerSuspectLinesPerClassName.get("className2").keySet());
        assertEquals(4, (int)className2.get(0));
        assertEquals(2, (int)className2.get(1));
        assertEquals(3, (int)className2.get(2));
        assertEquals(1, (int)className2.get(3));
    }

    @Test
    void testSort() {
        Map<String, List<Integer>> suspectLines = new LinkedHashMap<>();
        suspectLines.put("className1", new ArrayList<>());
        suspectLines.get("className1").add(1);
        suspectLines.get("className1").add(2);
        suspectLines.get("className1").add(2);
        suspectLines.get("className1").add(2);
        suspectLines.get("className1").add(3);
        suspectLines.get("className1").add(3);
        suspectLines.put("className2", new ArrayList<>());
        suspectLines.get("className2").add(1);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(2);
        suspectLines.get("className2").add(3);
        suspectLines.get("className2").add(3);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        suspectLines.get("className2").add(4);
        final Map<String, List<Integer>> sortedSuspectLines = new OccurrenceSorter().sortFaultyLines(suspectLines);
        final List<String> classNames = new ArrayList<>(sortedSuspectLines.keySet());
        System.out.println(sortedSuspectLines);
        assertEquals("className2", classNames.get(0));
        assertEquals("className1", classNames.get(1));
        final List<Integer> className1 = sortedSuspectLines.get("className1");
        assertEquals(2, (int)className1.get(0));
        assertEquals(3, (int)className1.get(1));
        assertEquals(1, (int)className1.get(2));
        final List<Integer> className2 = sortedSuspectLines.get("className2");
        assertEquals(4, (int)className2.get(0));
        assertEquals(2, (int)className2.get(1));
        assertEquals(3, (int)className2.get(2));
        assertEquals(1, (int)className2.get(3));
    }
}
