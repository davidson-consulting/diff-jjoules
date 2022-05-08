package fr.davidson.diff.jjoules.mark.strategies;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.ClassCoverage;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.LineCoverage;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.selection.NewCoverage;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.wrapper.maven.MavenWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public abstract class AbstractCoverageMarkStrategy implements MarkStrategy {

    protected NewCoverage coverageV1;
    protected NewCoverage coverageV2;

    protected void initCoverages(Configuration configuration) {
        if (configuration.getCoverageV1().isEmpty()) {
            this.coverageV1 = this.getCoverage(configuration.getPathToFirstVersion());
            JSONUtils.write(configuration.getOutput() + "/coverage_v1.json", this.coverageV1);
        } else {
            this.coverageV1 = configuration.getCoverageV1();
        }
        if (configuration.getCoverageV2().isEmpty()) {
            this.coverageV2 = this.getCoverage(configuration.getPathToSecondVersion());
            JSONUtils.write(configuration.getOutput() + "/coverage_v2.json", this.coverageV2);
        } else {
            this.coverageV2 = configuration.getCoverageV1();
        }
    }

    protected NewCoverage getCoverage(String rootAbsolutePath) {
        new MavenWrapper().clean(rootAbsolutePath);
        new CloverExecutor().instrumentAndRunTest(rootAbsolutePath);
        final Coverage cloverCoverage = new CloverReader().read(rootAbsolutePath);
        final NewCoverage newCoverage = new NewCoverage();
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
                    }
                }
            }
        }
        return newCoverage;
    }

}
