package fr.davidson.diff.jjoules.localization.select;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LargestImpactSelectorTest {

    @Test
    void test() {
        final LargestImpactSelector selector = new LargestImpactSelector();
        final Map<String, List<String>> selection = selector
                .select("src/test/resources/data_v1.json", "src/test/resources/data_v2.json");
        assertEquals(1, selection.size());
        assertTrue(selection.containsKey("com.google.gson.functional.DefaultTypeAdaptersTest"));
        assertEquals(1, selection.get("com.google.gson.functional.DefaultTypeAdaptersTest").size());
        assertEquals("testDateSerializationWithPattern", selection.get("com.google.gson.functional.DefaultTypeAdaptersTest").get(0));
        assertEquals(20936.0, selector.getDelta());
        final Map<String, Double> deltaPerTest = selector.getDeltaPerTest();
        assertEquals(-61.0, deltaPerTest.get("com.google.gson.DefaultDateTypeAdapterTest-testDateDeserializationISO8601"));
        assertEquals(428.0, deltaPerTest.get("com.google.gson.DefaultDateTypeAdapterTest-testDateSerialization"));
        assertEquals(2076.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testDateSerializationInCollection"));
        assertEquals(488.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testTimestampSerialization"));
        assertEquals(548.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testDateDeserializationWithPattern"));
        assertEquals(-854.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testDateSerializationWithPatternNotOverridenByTypeAdapter"));
        assertEquals(855.0, deltaPerTest.get("com.google.gson.DefaultDateTypeAdapterTest-testDatePattern"));
        assertEquals(-61.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testSqlDateSerialization"));
        assertEquals(17517.0, deltaPerTest.get("com.google.gson.functional.DefaultTypeAdaptersTest-testDateSerializationWithPattern"));
    }
}
