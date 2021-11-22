package fr.davidson.diff.jjoules.selection;

import eu.stamp_project.testrunner.listener.Coverage;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.listener.impl.CoverageDetailed;
import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class CoverageComputationTest extends AbstractDiffJJoulesStepTest {

    @Test
    void testGetCoverage() {
        final Configuration configuration = this.getConfiguration();
        final CoveredTestResultPerTestMethod coverage = CoverageComputation.getCoverage(
                configuration.pathToFirstVersion,
                configuration.getClasspathV1AsString(),
                configuration.junit4
        );
        assertEquals(6, coverage.getCoverageResultsMap().size());
        final Coverage testCoverage = coverage.getCoverageResultsMap().get("fr.davidson.diff_jjoules_demo.InternalListTest#testMapEmptyList");
        assertNotNull(testCoverage);
        assertTrue(testCoverage instanceof CoverageDetailed);
        assertFalse(((CoverageDetailed) testCoverage).getDetailedCoverage().isEmpty());
    }

    @Test
    void testConvert() {
        final Configuration configuration = this.getConfiguration();
        final CoveredTestResultPerTestMethod coverage = CoverageComputation.getCoverage(
                configuration.pathToFirstVersion,
                configuration.getClasspathV1AsString(),
                configuration.junit4
        );
        final eu.stamp_project.diff_test_selection.coverage.Coverage convert = CoverageComputation.convert(coverage);
        assertEquals(1, convert.testClassCoverage.size());
        assertEquals(6, convert.testClassCoverage.get("fr.davidson.diff_jjoules_demo.InternalListTest").testMethodsCoverage.size());
        assertEquals(8, convert.testClassCoverage.get("fr.davidson.diff_jjoules_demo.InternalListTest").testMethodsCoverage.get("testMapOneElement").classCoverageList.get("fr.davidson.diff_jjoules_demo.InternalList").coverages.size());
    }

}
