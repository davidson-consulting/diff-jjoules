package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.ExecsLines;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class TestTest {

    @Test
    void testComputeOmegaT() {
        final Map<String, Double> phiL =
                new HashMap<String, Double>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalList#29", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#31", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#32", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalList#30", 1.0);
                    }
                };
        final List<ExecsLines> execLineList = new ArrayList<>();
        execLineList.add(JSONUtils.read("src/test/resources/json/exec_deletions.json", ExecsLines.class));
        execLineList.add(JSONUtils.read("src/test/resources/json/exec_additions.json", ExecsLines.class));

        final Map<String, Double> actual = fr.davidson.diff.jjoules.mark.strategies.original.computation.Test.computeOmegaT(execLineList, phiL);

        final Map<String, Double> oracle =
                new HashMap<String, Double>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testCountFailing", 1.0);
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount", 1.0);
                    }
                };
        for (String expectedKey : oracle.keySet()) {
            assertTrue(actual.containsKey(expectedKey));
            assertEquals(oracle.get(expectedKey), actual.get(expectedKey));
        }
    }

    @Test
    void testComputeOmegaUpperT() {
        final Map<String, Double> omegaT =
                new HashMap<String, Double>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testMapEmptyList", 2.1315789473684212);
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount2", 0.0);
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testMapMultipleElement", 6.078947368421052);
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testMapOneElement", 6.078947368421052);
                        put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount", 0.0);
                    }
                };
        final Deltas deltaT = new Deltas();
        final Delta delta = new Delta(
                new Data(10, 10, 10, 10, 10, 10, 10, 10),
                new Data(100, 100, 100, 100, 100, 100, 100, 100)
        );
        deltaT.put("fr.davidson.diff_jjoules_demo.InternalListTest#testMapEmptyList", delta);
        final Map<String, Data> actual = fr.davidson.diff.jjoules.mark.strategies.original.computation.Test.computeOmegaUpperT(omegaT, deltaT);
        assertTrue(actual.containsKey("fr.davidson.diff_jjoules_demo.InternalListTest#testMapEmptyList"));
        assertEquals(191.84210526315792, actual.get("fr.davidson.diff_jjoules_demo.InternalListTest#testMapEmptyList").energy);
    }
}
