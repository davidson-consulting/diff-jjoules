package fr.davidson.diff.jjoules.selection;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.DiffCoverage;
import eu.stamp_project.diff_test_selection.selector.EnhancedDiffTestSelection;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.coverage.CoverageComputation;
import fr.davidson.diff.jjoules.util.coverage.detection.TestDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        final List<String> allFullQualifiedNameTestClassesV1 = new TestDetector(
                configuration.getPathToFirstVersion() + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToTestFolder()
        ).getAllFullQualifiedNameTestClasses();
        final List<String> allFullQualifiedNameTestClassesV2 = new TestDetector(
                configuration.getPathToSecondVersion() + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToTestFolder()
        ).getAllFullQualifiedNameTestClasses();

        final Coverage coverageV1 = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                        this.configuration.getPathToFirstVersion(),
                        this.configuration.getClasspathV1AsString(),
                        this.configuration.isJunit4(),
                        allFullQualifiedNameTestClassesV1,
                        this.configuration.getWrapper().getBinaries()
                )
        );
        final Coverage coverageV2 = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                        this.configuration.getPathToSecondVersion(),
                        this.configuration.getClasspathV2AsString(),
                        this.configuration.isJunit4(),
                        allFullQualifiedNameTestClassesV2,
                        this.configuration.getWrapper().getBinaries()
                )
        );
        final DiffCoverage coverage = new DiffCoverage();
        final EnhancedDiffTestSelection enhancedDiffTestSelection = new EnhancedDiffTestSelection(
                configuration.getPathToFirstVersion(),
                configuration.getPathToSecondVersion(),
                coverageV1,
                configuration.getDiff(),
                coverage,
                coverageV2
        );
        final Map<String, Set<String>> testsList = enhancedDiffTestSelection.selectTests();
        this.configuration.setTestsList(testsList);
        CSVFileManager.writeFile(
                Constants.joinFiles(this.configuration.getOutput(), SelectionStep.PATH_TO_CSV_TESTS_EXEC_CHANGES),
                CSVFileManager.formatTestListsToCSVLines(testsList)
        );
    }


}
