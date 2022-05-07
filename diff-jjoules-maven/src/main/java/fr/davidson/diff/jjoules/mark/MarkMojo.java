package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.filters.TestFilterEnum;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategyEnum;
import fr.davidson.diff.jjoules.util.JSONUtils;
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
     * Specify the path to json file containing the data of V1
     */
    @Parameter(property = "path-data-v1", required = false, defaultValue = DeltaStep.PATH_TO_JSON_DATA_V1)
    private String pathDataV1;

    /**
     * Specify the path to json file containing the data of V2
     */
    @Parameter(property = "path-data-v2", required = false, defaultValue = DeltaStep.PATH_TO_JSON_DATA_V2)
    private String pathDataV2;


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
    @Parameter(property = "mark-strategy", required = false, defaultValue = "STRICT")
    private MarkStrategyEnum markStrategyEnum;

    /**
     * Specify the threshold of the Cohen's D
     */
    @Parameter(property = "cohen-s-d", required = false, defaultValue = "0.8")
    private double cohensD;

    @Override
    protected Configuration getConfiguration() {
        final Configuration configuration = super.getConfiguration();
        configuration.setDataV1(JSONUtils.read(this.pathDataV1, Datas.class));
        configuration.setDataV2(JSONUtils.read(this.pathDataV2, Datas.class));
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
