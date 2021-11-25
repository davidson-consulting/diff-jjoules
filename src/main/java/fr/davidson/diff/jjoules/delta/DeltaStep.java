package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class DeltaStep extends DiffJJoulesStep {

    public static final String PATH_TO_JSON_DATA_V1 = "data_v1.json";

    public static final String PATH_TO_JSON_DATA_V2 = "data_v2.json";

    public static final String PATH_TO_JSON_DELTA = "deltas.json";

    public static final String PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME = "consideredTestMethods.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeltaStep.class);

    protected String getReportPathname() {
        return "delta";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Delta");
        final Map<String, Set<String>> testsList = configuration.getTestsList();
        final Datas dataV1 = new Datas();
        final Datas dataV2 = new Datas();
        new MeasureEnergyConsumption().measureEnergyConsumptionForBothVersion(
                configuration,
                dataV1,
                dataV2,
                testsList
        );
        final Map<String, Data> mediansV1 = Computation.computeMedian(dataV1);
        final Map<String, Data> mediansV2 = Computation.computeMedian(dataV2);
        final Deltas deltas = Computation.computeDelta(mediansV1, mediansV2);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_DATA_V1, dataV1);
        configuration.setDataV1(dataV1);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_DATA_V2, dataV2);
        configuration.setDataV2(dataV2);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_DELTA, deltas);
        configuration.setDeltas(deltas);
        filterTestMethods(configuration, dataV1, dataV2, deltas);
    }

    private void filterTestMethods(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName) {
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);
        final Map<String, Set<String>> consideredTestsNames = new HashMap<>();
        for (String key : deltaPerTestMethodName.keySet()) {
            if (emptyIntersectionPerTestMethodName.get(key)) {
                final String[] split = key.split("#");
                if (!consideredTestsNames.containsKey(split[0])) {
                    consideredTestsNames.put(split[0], new HashSet<>());
                }
                consideredTestsNames.get(split[0]).add(split[1]);
            }
        }
        JSONUtils.write(
                configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME,
                consideredTestsNames
        );
        configuration.setConsideredTestsNames(consideredTestsNames);
    }

}
