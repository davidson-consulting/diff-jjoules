package fr.davidson.diff.jjoules.mark.computation;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Delta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static Map<String, Double> computeOmegaT(
            final List<ExecsLines> execLineTestMaps,
            final Map<String, Double> phiL
            ) {
        final Map<String, Double> omegaT = new HashMap<>();
        for (ExecLineTestMap execLineTestMapDeletion : execLineTestMaps.get(0)) {
            computeOmegaT(phiL, omegaT, execLineTestMapDeletion);
        }
        for (ExecLineTestMap execLineTestMapAddition : execLineTestMaps.get(1)) {
            computeOmegaT(phiL, omegaT, execLineTestMapAddition);
        }
        return omegaT;
    }

    private static void computeOmegaT(Map<String, Double> phiL, Map<String, Double> omegaT, ExecLineTestMap execLineTestMapDeletion) {
        for (String modifiedLine : execLineTestMapDeletion.getExecLt().keySet()) {
            final Map<String, Integer> execPerTestMethod = execLineTestMapDeletion.getExecLt().get(modifiedLine);
            for (String testMethodName : execPerTestMethod.keySet()) {
                if (!omegaT.containsKey(testMethodName)) {
                    omegaT.put(testMethodName, execPerTestMethod.get(testMethodName) * phiL.get(modifiedLine));
                } else {
                    omegaT.put(testMethodName, omegaT.get(testMethodName) + execPerTestMethod.get(testMethodName) * phiL.get(modifiedLine));
                }
            }
        }
    }

    public static Map<String, Data> computeOmegaUpperT(
            final Map<String, Double> omegaT,
            final Map<String, Delta> deltaT) {
        final Map<String, Data> omegaUpperT = new HashMap<>();
        for (String key : omegaT.keySet()) {
            if (!deltaT.containsKey(key)) {
                LOGGER.info("WARNING {} is not in delta(t) map!", key);
                continue;
            }
            omegaUpperT.put(key,
                    new Data(
                        omegaT.get(key) * deltaT.get(key).energy,
                        omegaT.get(key) * deltaT.get(key).instructions,
                        omegaT.get(key) * deltaT.get(key).durations,
                        omegaT.get(key) * deltaT.get(key).cycles,
                        omegaT.get(key) * deltaT.get(key).caches,
                        omegaT.get(key) * deltaT.get(key).cacheMisses,
                        omegaT.get(key) * deltaT.get(key).branches,
                        omegaT.get(key) * deltaT.get(key).branchMisses
                    )
            );
        }
        return omegaUpperT;
    }
}
