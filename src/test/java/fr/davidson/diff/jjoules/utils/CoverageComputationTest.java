package fr.davidson.diff.jjoules.utils;

import eu.stamp_project.testrunner.listener.Coverage;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.listener.impl.CoverageDetailed;
import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.coverage.CoverageComputation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class CoverageComputationTest extends AbstractDiffJJoulesStepTest {

    @Test
    void testGetCoverageForSpecificTestMethods() {
        final Configuration configuration = this.getConfiguration();
        final CoveredTestResultPerTestMethod coverage = CoverageComputation.getCoverage(
                configuration.pathToFirstVersion,
                configuration.getClasspathV1AsString(),
                configuration.junit4,
                Collections.singletonList("fr.davidson.diff_jjoules_demo.InternalListTest"),
                Arrays.stream(new String[]{"testCount", "testCount2"}).collect(Collectors.toList()),
                BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
        );
        assertEquals(2, coverage.getCoverageResultsMap().size());
        final Coverage testCoverage = coverage.getCoverageResultsMap().get("fr.davidson.diff_jjoules_demo.InternalListTest#testCount");
        assertNotNull(testCoverage);
        assertTrue(testCoverage instanceof CoverageDetailed);
        assertFalse(((CoverageDetailed) testCoverage).getDetailedCoverage().isEmpty());
    }


    @Test
    void testGetCoverage() {
        final Configuration configuration = this.getConfiguration();
        final CoveredTestResultPerTestMethod coverage = CoverageComputation.getCoverage(
                configuration.pathToFirstVersion,
                configuration.getClasspathV1AsString(),
                configuration.junit4,
                Collections.singletonList("fr.davidson.diff_jjoules_demo.InternalListTest"),
                BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
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
                configuration.junit4,
                Collections.singletonList("fr.davidson.diff_jjoules_demo.InternalListTest"),
                BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
        );
        final eu.stamp_project.diff_test_selection.coverage.Coverage convert = CoverageComputation.convert(coverage);
        assertEquals(6, convert.testClassCoverage.get("fr.davidson.diff_jjoules_demo.InternalListTest").testMethodsCoverage.size());
        assertEquals(8, convert.testClassCoverage.get("fr.davidson.diff_jjoules_demo.InternalListTest").testMethodsCoverage.get("testMapOneElement").classCoverageList.get("fr.davidson.diff_jjoules_demo.InternalList").coverages.size());
    }

}
