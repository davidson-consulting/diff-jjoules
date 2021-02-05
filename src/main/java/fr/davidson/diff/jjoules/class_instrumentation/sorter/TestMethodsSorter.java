package fr.davidson.diff.jjoules.class_instrumentation.sorter;

import java.util.List;
import java.util.Map;

public interface TestMethodsSorter {

    Map<String, List<String>> sort(
            int numberOfMethodToProcess,
            final Map<String, Integer> numberOfDuplicationRequired,
            final Map<String, List<String>> testsList
    );

}
