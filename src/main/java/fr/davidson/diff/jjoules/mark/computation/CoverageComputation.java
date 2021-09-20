package fr.davidson.diff.jjoules.mark.computation;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.testrunner.EntryPoint;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

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
            final Coverage coverage = cloverReader.read(pathToVersion);
            for (String testMethodName : testsListName.get(testClassName)) {
                coveragePerTestMethodName.put(
                        Utils.toFullQualifiedName(testClassName, testMethodName),
                        coverage
                );
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
       cloverExecutor.runInstrumentedTest(
                pathToVersion,
                new HashMap<String, List<String>>() {
                    {
                        put(testClassName, new ArrayList<>());
                        get(testClassName).add(testMethodName);
                    }
                }
        );
        final Coverage coverage = new CloverReader().read(pathToVersion);
        LOGGER.info("Read From {}, {}", pathToVersion, coverage.testClassCoverage.size());
        coveragePerTestMethodName.put(
                Utils.toFullQualifiedName(testClassName, testMethodName),
                coverage
        );
    }

}
