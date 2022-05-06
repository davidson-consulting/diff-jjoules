package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.mark.filters.TestFilterEnum;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategyEnum;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
    @Parameter(property = "path-considered-test-method", required = false, defaultValue = TestFilter.PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME)
    private String pathConsideredTestMethod;

    /**
     * Specify the path to json file containing the deltas
     */
    @Parameter(property = "path-deltas", required = false, defaultValue = DeltaStep.PATH_TO_JSON_DELTA)
    private String pathDeltas;

    /**
     *
     */
    @Parameter(property = "test-filter", required = false, defaultValue = "ALL")
    private TestFilterEnum testFilterEnum;

    /**
     *
     */
    @Parameter(property = "mark-strategy", required = false, defaultValue = "ORIGINAL")
    private MarkStrategyEnum markStrategyEnum;

    /**
     * Specify the threshold of the Cohen's D
     */
    @Parameter(property = "cohen-s-d", required = false, defaultValue = "0.8")
    private double cohensD;

    @Override
    protected Configuration getConfiguration() {
        final Configuration configuration = super.getConfiguration();
        configuration.setConsideredTestsNames(JSONUtils.read(this.pathConsideredTestMethod, MethodNamesPerClassNames.class));
        configuration.setDeltas(JSONUtils.read(this.pathDeltas, Deltas.class));
        configuration.setTestFilterEnum(this.testFilterEnum);
        configuration.setCohensD(this.cohensD);
        configuration.setMarkStrategyEnum(this.markStrategyEnum);
        return configuration;
    }

    @Override
    protected DiffJJoulesStep getStep() {
        return new MarkStep();
    }
}
