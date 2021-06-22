package fr.davidson.diff.jjoules.mark.computation;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class CoverageComputation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoverageComputation.class);

    public static Map<String, Coverage> computeCoverageForGivenVersionOfTests(
            final Map<String, List<String>> testsListName,
            String pathToVersion
    ) {
        final Map<String, Coverage> coveragePerTestMethodName = new HashMap<>();
        final CloverExecutor cloverExecutor = new CloverExecutor();
        for (String testClassName: testsListName.keySet()) {
            for (String testMethodName: testsListName.get(testClassName)) {
                computeAndCollectCoverage(pathToVersion, coveragePerTestMethodName, cloverExecutor, testClassName, testMethodName);
            }
        }
        return coveragePerTestMethodName;
    }

    private static void computeAndCollectCoverage(
            String pathToVersion,
            Map<String, Coverage> coveragePerTestMethodName,
            CloverExecutor cloverExecutor,
            String testClassName,
            String testMethodName
    ) {
        cloverExecutor.instrumentAndRunGivenTest(
                pathToVersion,
                new HashMap<String, List<String>>() {
                    {
                        put(testClassName, new ArrayList<>());
                        get(testClassName).add(testMethodName);
                    }
                }
        );
        final Coverage coverage = new CloverReader().read(pathToVersion);
        LOGGER.info("Read From {}, {}", pathToVersion, coverage.toString());
        coveragePerTestMethodName.put(
                Utils.toFullQualifiedName(testClassName, testMethodName),
                coverage
        );
    }

}
