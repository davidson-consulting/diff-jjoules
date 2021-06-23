package fr.davidson.diff.jjoules.mark.computation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class Delta {

    public static Map<String, Double> computeDelta(
            Map<String, Map> dataJsonV1,
            Map<String, Map> dataJsonV2
    ) {
        final Map<String, Double> deltas = new HashMap<>();
        for (String key : dataJsonV1.keySet()) {
            if (dataJsonV2.containsKey(key)) {
                deltas.put(key.split(".json")[0],
                        (Double)dataJsonV2.get(key).get("Energy") - (Double)dataJsonV1.get(key).get("Energy")
                );
            }
        }
        return deltas;
    }

}
