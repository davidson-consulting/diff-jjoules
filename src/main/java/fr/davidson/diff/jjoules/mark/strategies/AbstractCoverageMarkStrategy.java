package fr.davidson.diff.jjoules.mark.strategies;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.ClassCoverage;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.LineCoverage;
import fr.davidson.diff.jjoules.selection.NewCoverage;
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

    protected  int nbCoveredLine = 0;

    protected NewCoverage getCoverage(String rootAbsolutePath) {
        new MavenWrapper().clean(rootAbsolutePath);
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

}
