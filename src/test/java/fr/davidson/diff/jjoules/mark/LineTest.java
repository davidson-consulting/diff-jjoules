package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.mark.strategies.original.computation.ExecsLines;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.Line;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class LineTest {

    @Test
    void testComputeThetaL() {
        final List<ExecsLines> execLineList = new ArrayList<>();
        execLineList.add(JSONUtils.read("src/test/resources/json/exec_deletions.json", ExecsLines.class));
        execLineList.add(JSONUtils.read("src/test/resources/json/exec_additions.json", ExecsLines.class));
        final Map<String, Integer> actual = Line.computeThetaL(execLineList);
        final Map<String, Double> oracle = JSONUtils.read("src/test/resources/json/theta.json", Map.class);
        for (String expectedKey : oracle.keySet()) {
            assertTrue(actual.containsKey(expectedKey));
            assertEquals((double)oracle.get(expectedKey), (double)actual.get(expectedKey));
        }
    }

    @Test
    void testComputeTheta() {
        final Map<String, Integer> thetaL = readAndConvertThetaL();
        final int theta = Line.computeTheta(thetaL);
        assertEquals(2, theta);
    }

    @NotNull
    private Map<String, Integer> readAndConvertThetaL() {
        final Map<String, Integer> thetaL = new HashMap<>();
        final Map<String, Double> tmp = JSONUtils.read("src/test/resources/json/theta.json", Map.class);
        for (String key : tmp.keySet()) {
            thetaL.put(key, tmp.get(key).intValue());
        }
        return thetaL;
    }

    @Test
    void testComputePhiL() {
        final Map<String, Double> oracle =
                new HashMap<String, Double>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalList#29", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#31", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#32", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#30", 1.0);
                    }
                };
        final Map<String, Double> actual = Line.computePhiL(2, readAndConvertThetaL());
        for (String expectedKey : oracle.keySet()) {
            assertTrue(actual.containsKey(expectedKey));
            assertEquals(oracle.get(expectedKey), actual.get(expectedKey), 0.0005);
        }
    }
}
