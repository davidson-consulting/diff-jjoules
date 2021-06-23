package fr.davidson.diff.jjoules.mark.computation;

import fr.davidson.diff.jjoules.mark.Main;
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
            final List<List<ExecLineTestMap>> execLineTestMaps,
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

    public static Map<String, Double> computeOmegaUpperT(
            final Map<String, Double> omegaT,
            final Map<String, Double> deltaT
            ) {
        final Map<String, Double> omegaUpperT = new HashMap<>();
        LOGGER.info("{}", omegaT);
        LOGGER.info("{}", deltaT);
        for (String key : omegaT.keySet()) {
            LOGGER.info("{}, {} {}", key, deltaT.get(key), key.replaceAll("#", "-"));
            omegaUpperT.put(key, omegaT.get(key) * deltaT.get(key.replace("#", "-")));
        }
        return omegaUpperT;
    }
}
