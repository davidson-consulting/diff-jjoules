package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
@Mojo(name = "mark")
public class MarkMojo extends DiffJJoulesMojo {

    /**
     * Specify the path to json file containing the considered test method names
     */
    @Parameter(property = "path-considered-test-method", required = false, defaultValue = DeltaStep.PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME)
    protected String pathConsideredTestMethod;

    @Override
    protected Configuration getConfiguration() {
        final Configuration configuration = super.getConfiguration();
        configuration.setConsideredTestsNames(JSONUtils.read(this.pathConsideredTestMethod, Map.class));
        return configuration;
    }

    @Override
    protected DiffJJoulesStep getStep() {
        return new MarkStep();
    }
}
