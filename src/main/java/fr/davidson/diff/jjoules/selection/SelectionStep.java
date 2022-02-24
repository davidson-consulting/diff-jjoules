package fr.davidson.diff.jjoules.selection;

import com.google.gson.GsonBuilder;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.coverage.DiffCoverage;
import eu.stamp_project.diff_test_selection.selector.EnhancedDiffTestSelection;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.runner.Failure;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.coverage.CoverageComputation;
import fr.davidson.diff.jjoules.util.coverage.detection.TestDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class SelectionStep extends DiffJJoulesStep {

    public static final String JSON_REPORT_FAILURE_PATHNAME = "test_failures.json";

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

        final CoveredTestResultPerTestMethod coverage1 = CoverageComputation.getCoverage(
                this.configuration.getPathToFirstVersion(),
                this.configuration.getClasspathV1AsString(),
                this.configuration.isJunit4(),
                allFullQualifiedNameTestClassesV1,
                this.configuration.getWrapper().getBinaries()
        );
        final Coverage coverageV1 = CoverageComputation.convert(coverage1);
        final CoveredTestResultPerTestMethod coverage2 = CoverageComputation.getCoverage(
                this.configuration.getPathToSecondVersion(),
                this.configuration.getClasspathV2AsString(),
                this.configuration.isJunit4(),
                allFullQualifiedNameTestClassesV2,
                this.configuration.getWrapper().getBinaries()
        );
        final Coverage coverageV2 = CoverageComputation.convert(coverage2);
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
        final Set<Failure> failures = coverage1.getFailingTests();
        failures.addAll(coverage2.getFailingTests());
        for (Failure failure : failures) {
            testsList.get(failure.testClassName).remove(FullQualifiedName.fromString(failure.testCaseName).methodName);
        }
        outputFailures(failures);
        CSVFileManager.writeFile(
                Constants.joinFiles(this.configuration.getOutput(), SelectionStep.PATH_TO_CSV_TESTS_EXEC_CHANGES),
                CSVFileManager.formatTestListsToCSVLines(testsList)
        );
    }

    private void outputFailures(final Set<Failure> failures) {
        try (final FileWriter writer = new FileWriter(
                Constants.joinFiles(this.configuration.getOutput(), JSON_REPORT_FAILURE_PATHNAME))) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(failures));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
