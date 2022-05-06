package fr.davidson.diff.jjoules.mark.strategies.code_coverage;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.ClassCoverage;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.LineCoverage;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategy;
import fr.davidson.diff.jjoules.selection.NewCoverage;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class CodeCoverageMarkStrategy implements MarkStrategy {

    private int nbCoveredLine = 0;

    private NewCoverage getCoverage(String rootAbsolutePath) {
        new CloverExecutor().instrumentAndRunTest(rootAbsolutePath);
        final Coverage cloverCoverage = new CloverReader().read(rootAbsolutePath);
        final NewCoverage newCoverage = new NewCoverage();
        this.nbCoveredLine = 0;
        for (String testClassName : cloverCoverage.getTestClasses()) {
            newCoverage.put(testClassName, new HashMap<>());
            for (String testMethodName : cloverCoverage.getTestMethodsForTestClassName(testClassName)) {
                newCoverage.get(testClassName).put(testMethodName, new HashMap<>());
                final Map<String, ClassCoverage> testMethodCoverage = cloverCoverage.getTestMethodCoverageForClassName(testClassName, testMethodName);
                for (String className : testMethodCoverage.keySet()) {
                    newCoverage.get(testClassName).get(testMethodName).put(className, new ArrayList<>());
                    final ClassCoverage classCoverage = testMethodCoverage.get(className);
                    for (LineCoverage lineCoverage : classCoverage.getCoverages()) {
                        newCoverage.get(testClassName).get(testMethodName).get(className).add(lineCoverage.line);
                        this.nbCoveredLine++;
                    }
                }
            }
        }
        return newCoverage;
    }

    @Override
    public boolean applyStrategy(
            Configuration configuration,
            Datas dataV1,
            Datas dataV2,
            Deltas deltaPerTestMethodName,
            MethodNamesPerClassNames consideredTest) {
        final NewCoverage coverageV1 = this.getCoverage(configuration.getPathToFirstVersion());
        final int nbCoveredLineV1 = this.nbCoveredLine;
        final NewCoverage coverageV2 = this.getCoverage(configuration.getPathToSecondVersion());
        final int nbCoveredLineV2 = this.nbCoveredLine;
        Data deltaOmega = new Data();
        for (String testClassName : consideredTest.keySet()) {
            for (String testMethodName : consideredTest.get(testClassName)) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testMethodName);
                for (String coveredSrcClassName : coverageV1.get(testClassName).get(testMethodName).keySet()) {
                    final double weightV1 = ((double) coverageV1.get(testClassName).get(testMethodName).get(coveredSrcClassName).size()) / ((double) nbCoveredLineV1);
                    final double weightV2 = ((double) coverageV2.get(testClassName).get(testMethodName).get(coveredSrcClassName).size()) / ((double) nbCoveredLineV2);
                    deltaOmega = deltaOmega.add(deltaPerTestMethodName.get(fullQualifiedName.toString()), Math.max(weightV1, weightV2));
                }
            }
        }
        return deltaOmega.cycles <= 0;
    }
}
