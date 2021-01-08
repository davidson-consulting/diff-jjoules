package fr.davidson.diff.jjoules.localization;

import java.util.*;

public class OccurrenceSorter implements Sorter {

    public Map<String, Map<Integer, Integer>> getOccurrencePerSuspectLinesPerClassName(
            Map<String, List<Integer>> suspectLines
    ) {
        final Map<String, Map<Integer, Integer>> occurrencePerSuspectLinesPerClassName = new LinkedHashMap<>();
        for (String key : suspectLines.keySet()) {
            occurrencePerSuspectLinesPerClassName.put(key, new LinkedHashMap<>());
            final Map<Integer, Integer> occurrencePerSuspectLines = new LinkedHashMap<>();
            for (Integer line : suspectLines.get(key)) {
                if (!occurrencePerSuspectLines.containsKey(line)) {
                    occurrencePerSuspectLines.put(line, 1);
                } else {
                    occurrencePerSuspectLines.put(line, occurrencePerSuspectLines.get(line) + 1);
                }
            }
            final ArrayList<Integer> sortedLines = new ArrayList<>(occurrencePerSuspectLines.keySet());
            sortedLines.sort( (l1, l2) -> occurrencePerSuspectLines.get(l2) - occurrencePerSuspectLines.get(l1));
            sortedLines.forEach(line ->
                    occurrencePerSuspectLinesPerClassName.get(key).put(
                            line, occurrencePerSuspectLines.get(line)
                    )
            );
        }
        return occurrencePerSuspectLinesPerClassName;
    }

    @Override
    public Map<String, List<Integer>> sortFaultyLines(Map<String, List<Integer>> suspectLines) {
        final Map<String, Map<Integer, Integer>> occurrencePerSuspectLinesPerClassName = this.getOccurrencePerSuspectLinesPerClassName(suspectLines);
        final List<String> keys = new ArrayList<>(suspectLines.keySet());
        keys.sort((className1, className2) -> {
            final int totalNbOccurrences1 = occurrencePerSuspectLinesPerClassName.get(className1)
                    .keySet()
                    .stream()
                    .reduce((acc, line) -> {
                                return acc + occurrencePerSuspectLinesPerClassName.get(className1).get(line);
                            }
                    ).get();
            final int totalNbOccurrences2 = occurrencePerSuspectLinesPerClassName.get(className2)
                    .keySet()
                    .stream()
                    .reduce((acc, line) ->
                            acc + occurrencePerSuspectLinesPerClassName.get(className2).get(line)
                    ).get();
            return totalNbOccurrences2 - totalNbOccurrences1;
        });
        final Map<String, List<Integer>> sortedSuspectLines = new LinkedHashMap<>();
        for (String key : keys) {
            sortedSuspectLines.put(key, new ArrayList<>());
            for (Integer line : occurrencePerSuspectLinesPerClassName.get(key).keySet()) {
                sortedSuspectLines.get(key).add(line);
            }
        }
        return sortedSuspectLines;
    }

}
