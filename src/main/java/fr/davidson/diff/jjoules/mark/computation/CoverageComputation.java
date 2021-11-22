package fr.davidson.diff.jjoules.mark.computation;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.Coverage;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class CoverageComputation {

    public static Coverage computeCoverageForGivenVersionOfTests(
            final Map<String, Set<String>> testsListName,
            String pathToVersion
    ) {
        final Coverage coverage = new Coverage();
        final CloverExecutor cloverExecutor = new CloverExecutor();
        final CloverReader cloverReader = new CloverReader();
        for (String testClassName: testsListName.keySet()) {
            cloverExecutor.instrumentAndRunGivenTest(
                    pathToVersion,
                    new HashMap<String, List<String>>() {
                        {
                            put(testClassName, new ArrayList<>());
                            get(testClassName).addAll(testsListName.get(testClassName));
                        }
                    }
            );
            final Coverage current = cloverReader.read(pathToVersion);
            coverage.merge(current);
        }
        return coverage;
    }

}
