package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
@Mojo(name = "delta")
public class DeltaMojo extends DiffJJoulesMojo {

    protected String getReportPathname() {
        return "delta";
    }

    @Override
    protected void _run(Configuration configuration) {
        getLog().info("Run Delta - " + configuration.toString());
        final Map<String, List<String>> testsList = configuration.getTestsList();
        final Datas dataV1 = new Datas();
        final Datas dataV2 = new Datas();
        MeasureEnergyConsumption.measureEnergyConsumptionForBothVersion(
                configuration,
                dataV1,
                dataV2,
                testsList
        );
        final Map<String, Data> mediansV1 = Computation.computeMedian(dataV1);
        final Map<String, Data> mediansV2 = Computation.computeMedian(dataV2);
        final Deltas deltas = Computation.computeDelta(mediansV1, mediansV2);
        JSONUtils.write(configuration.output + "/" + configuration.pathToJSONDataV1, dataV1);
        configuration.setDataV1(dataV1);
        JSONUtils.write(configuration.output + "/" + configuration.pathToJSONDataV2, dataV2);
        configuration.setDataV2(dataV2);
        JSONUtils.write(configuration.output + "/" + configuration.pathToJSONDelta, deltas);
        configuration.setDeltas(deltas);
        filterTestMethods(configuration, dataV1, dataV2, deltas);
    }

    private void filterTestMethods(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName) {
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);
        final Map<String, List<String>> consideredTestsNames = new HashMap<>();
        for (String key : deltaPerTestMethodName.keySet()) {
            if (emptyIntersectionPerTestMethodName.get(key)) {
                final String[] split = key.split("#");
                if (!consideredTestsNames.containsKey(split[0])) {
                    consideredTestsNames.put(split[0], new ArrayList<>());
                }
                consideredTestsNames.get(split[0]).add(split[1]);
            }
        }
        JSONUtils.write(
                configuration.output + "/" + configuration.pathToJSONConsideredTestMethodNames,
                consideredTestsNames
        );
        configuration.setConsideredTestsNames(consideredTestsNames);
    }
}
