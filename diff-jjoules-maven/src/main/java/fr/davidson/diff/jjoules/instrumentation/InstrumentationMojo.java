package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.selection.SelectionStep;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
@Mojo(name = "instrument")
public class InstrumentationMojo extends DiffJJoulesMojo {

    /**
     * Specify the path to json file containing the considered test method names
     */
    @Parameter(property = "path-test-list", required = false, defaultValue = SelectionStep.PATH_TO_CSV_TESTS_EXEC_CHANGES)
    protected String pathTestList;

    @Override
    protected Configuration getConfiguration() {
        final Configuration configuration = super.getConfiguration();
        configuration.setTestsList(CSVFileManager.readFile(this.pathTestList));
        return configuration;
    }

    @Override
    protected DiffJJoulesStep getStep() {
        return new InstrumentationStep();
    }
}
