package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.mark.computation.Line;
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

    public static final int EXPECTED_THETA = 38;

    @Test
    void testComputeThetaL() {
        final List<ExecsLines> execLineList = new ArrayList<>();
        execLineList.add(JSONUtils.read("src/test/resources/diff-jjoules-demo/exec_deletions.json", ExecsLines.class));
        execLineList.add(JSONUtils.read("src/test/resources/diff-jjoules-demo-v2/exec_additions.json", ExecsLines.class));
        final Map<String, Integer> actual = Line.computeThetaL(execLineList);
        final Map<String, Integer> oracle =
                new HashMap<String, Integer>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalList#24", 10);
                        put("fr.davidson.diff_jjoules_demo.InternalList#25", 9);
                        put("fr.davidson.diff_jjoules_demo.InternalList#22", 9);
                        put("fr.davidson.diff_jjoules_demo.InternalList#23", 10);
                    }
                };
        for (String expectedKey : oracle.keySet()) {
            assertTrue(actual.containsKey(expectedKey));
            assertEquals(oracle.get(expectedKey), actual.get(expectedKey));
        }
    }

    @Test
    void testComputeTheta() {
        final Map<String, Integer> thetaL = readAndConvertThetaL();
        final int theta = Line.computeTheta(thetaL);
        assertEquals(EXPECTED_THETA, theta);
    }

    @NotNull
    private Map<String, Integer> readAndConvertThetaL() {
        final Map<String, Integer> thetaL = new HashMap<>();
        final Map<String, Double> tmp = JSONUtils.read("src/test/resources/diff-jjoules-demo/thetaL.json", Map.class);
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
                        put("fr.davidson.diff_jjoules_demo.InternalList#24", 0.2631578947368421);
                        put("fr.davidson.diff_jjoules_demo.InternalList#25", 0.23684210526315788);
                        put("fr.davidson.diff_jjoules_demo.InternalList#22", 0.23684210526315788);
                        put("fr.davidson.diff_jjoules_demo.InternalList#23", 0.2631578947368421);
                    }
                };
        final Map<String, Double> actual = Line.computePhiL(EXPECTED_THETA, readAndConvertThetaL());
        for (String expectedKey : oracle.keySet()) {
            assertTrue(actual.containsKey(expectedKey));
            assertEquals(oracle.get(expectedKey), actual.get(expectedKey), 0.0005);
        }
    }
}
