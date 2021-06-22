package fr.davidson.diff.jjoules.mark.computation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class Line {

    public static Map<String, Integer> computeThetaL(
            List<List<ExecLineTestMap>> execLineTestMaps
    ) {
        final Map<String, Integer> thetaL = new HashMap<>();
        for (ExecLineTestMap execLineTestMapDeletion : execLineTestMaps.get(0)) {
            computeThetaL(thetaL, execLineTestMapDeletion);
        }
        for (ExecLineTestMap execLineTestMapAddition : execLineTestMaps.get(1)) {
            computeThetaL(thetaL, execLineTestMapAddition);
        }
        return thetaL;
    }

    private static void computeThetaL(Map<String, Integer> thetaL, ExecLineTestMap execLineTestMapDeletion) {
        for (String modifiedLine : execLineTestMapDeletion.getExecLt().keySet()) {
            thetaL.put(modifiedLine,
                    execLineTestMapDeletion
                            .getExecLt()
                            .get(modifiedLine)
                            .values()
                            .stream()
                            .reduce(Integer::sum)
                            .orElse(0));
        }
    }

    public static int computeTheta(Map<String, Integer> thetaL) {
        return thetaL.values().stream().reduce(Integer::sum).orElse(0);
    }

    public static Map<String, Double> computePhiL(int theta, Map<String, Integer> thetaL) {
        final Map<String, Double> phiL = new HashMap<>();
        for (String key : thetaL.keySet()) {
            final double phi = ((double)thetaL.get(key)) / ((double)theta);
            phiL.put(key, phi);
        }
        return phiL;
    }

}
