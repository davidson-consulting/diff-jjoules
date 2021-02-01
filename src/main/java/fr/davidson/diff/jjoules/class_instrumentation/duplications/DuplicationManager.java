package fr.davidson.diff.jjoules.class_instrumentation.duplications;

import java.util.*;

import static fr.davidson.diff.jjoules.class_instrumentation.duplications.XMLReader.readAllXML;

public class DuplicationManager {

    private final int timeOfExecutionToReachInMs;

    public DuplicationManager(int timeOfExecutionToReachInMs) {
        this.timeOfExecutionToReachInMs = timeOfExecutionToReachInMs;
    }

    public Map<String, Integer> computeNumberOfDuplicationRequired(
            final Map<String, List<String>> testsList,
            final Map<String, Double> timePerTest
            ) {
        final HashMap<String, Integer> nbDuplicationPerTest = new HashMap<>();
        for (String testClassName : testsList.keySet()) {
            for (String testMethodName : testsList.get(testClassName)) {
                final String testName = testClassName + "#" + testMethodName;
                final Double timeInSForTest = timePerTest.get(testName);
                final double timeInMsForTest = Math.max(1, timeInSForTest * 1000);
                final int nbDuplication = (int) (timeOfExecutionToReachInMs / timeInMsForTest) + 1;
                nbDuplicationPerTest.put(testName, nbDuplication);
            }
        }
        return nbDuplicationPerTest;
    }

}
