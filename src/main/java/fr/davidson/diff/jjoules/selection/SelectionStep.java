package fr.davidson.diff.jjoules.selection;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
        eu.stamp_project.diff_test_selection.Main.run(new eu.stamp_project.diff_test_selection.configuration.Configuration(
                this.configuration.pathToFirstVersion,
                this.configuration.pathToSecondVersion,
                "",
                "CSV",
                "",
                true
        ));
        final Map<String, List<String>> testsList = CSVFileManager.readFile(this.configuration.pathToFirstVersion + "/" + PATH_TO_CSV_TESTS_EXEC_CHANGES);
        this.configuration.setTestsList(testsList);
    }

}
