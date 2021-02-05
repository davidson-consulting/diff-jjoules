package fr.davidson.diff.jjoules.class_instrumentation.sorter;

import java.util.*;

public class Sorter implements TestMethodsSorter {

    @Override
    public Map<String, List<String>> sort(int numberOfMethodToProcess,
                                          Map<String, Integer> numberOfDuplicationRequired,
                                          Map<String, List<String>> testsList) {
        final Map<String, List<String>> newTestsList = new HashMap<>();
        final List<String> keys = new ArrayList<>(numberOfDuplicationRequired.keySet());
        keys.sort(Comparator.comparingInt(numberOfDuplicationRequired::get));
        for (String testToBeKept : keys.subList(0, numberOfMethodToProcess)) {
            final String[] split = testToBeKept.split("#");
            if (testsList.containsKey(split[0]) && testsList.get(split[0]).contains(split[1])) {
                if (!newTestsList.containsKey(split[0])) {
                    newTestsList.put(split[0], new ArrayList<>());
                }
                newTestsList.get(split[0]).add(split[1]);
            }
        }
        return newTestsList;
    }

}
