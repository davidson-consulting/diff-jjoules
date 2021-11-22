package fr.davidson.diff.jjoules.selection;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.DiffCoverage;
import eu.stamp_project.diff_test_selection.selector.EnhancedDiffTestSelection;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class SelectionStep extends DiffJJoulesStep {

    public static final String PATH_TO_CSV_TESTS_EXEC_CHANGES = "testsThatExecuteTheChange.csv";

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectionStep.class);

    protected String getReportPathname() {
        return "selection";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Selection");
        final Coverage coverageV1 = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                    this.configuration.pathToFirstVersion,
                    this.configuration.getClasspathV1AsString(),
                    this.configuration.junit4
                )
        );
        final Coverage coverageV2 = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                        this.configuration.pathToSecondVersion,
                        this.configuration.getClasspathV2AsString(),
                        this.configuration.junit4
                )
        );
        final DiffCoverage coverage = new DiffCoverage();
        final EnhancedDiffTestSelection enhancedDiffTestSelection = new EnhancedDiffTestSelection(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                coverageV1,
                configuration.diff,
                coverage,
                coverageV2
        );
        final Map<String, Set<String>> testsList = enhancedDiffTestSelection.selectTests();
        this.configuration.setTestsList(testsList);
    }



}
