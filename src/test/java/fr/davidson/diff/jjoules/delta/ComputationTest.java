package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public class ComputationTest {

    public static final String DATA_V_1_JSON = "src/test/resources/json/data_v1.json";

    @Test
    void testComputeMedian() {
        final Datas datas = JSONUtils.read(DATA_V_1_JSON, Datas.class);
        final Map<String, Data> medians = Computation.computeMedian(datas);
        final String testName = "com.google.gson.functional.ObjectTest#testClassWithTransientFieldsSerialization";
        final Data data = medians.get(testName);
        assertEquals(16479.0, data.energy);
    }

    @Test
    void testComputeDelta() {
        final Map<String, Data> medians1 = new HashMap<String, Data>() {
            {
                put("test", new Data(
                        0.0D,10.0D,10.0D,10.0D,
                        0.0D,-10.0D,-10.0D,-10.0D)
                );
            }
        };
        final Map<String, Data> medians2 = new HashMap<String, Data>() {
            {
                put("test", new Data(
                        0.0D,-10.0D,-10.0D,-10.0D,
                        0.0D,10.0D,10.0D,10.0D)
                );
            }
        };
        final Deltas deltas = Computation.computeDelta(medians1, medians2);
        assertEquals(0.0D, deltas.get("test").energy);
        assertEquals(-20.0D, deltas.get("test").instructions);
        assertEquals(-20.0D, deltas.get("test").durations);
        assertEquals(-20.0D, deltas.get("test").cycles);
        assertEquals(0.0D, deltas.get("test").caches);
        assertEquals(20.0D, deltas.get("test").cacheMisses);
        assertEquals(20.0D, deltas.get("test").branches);
        assertEquals(20.0D, deltas.get("test").branchMisses);
    }
}