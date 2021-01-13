package fr.davidson.diff.jjoules.localization.select;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LargestImpactSelectorTest {

    @Test
    void test() {
        final Map<String, List<String>> selection = new LargestImpactSelector()
                .select("src/test/resources/data_v1.json", "src/test/resources/data_v2.json");
        assertEquals(1, selection.size());
        assertTrue(selection.containsKey("com.google.gson.functional.DefaultTypeAdaptersTest"));
        assertEquals(1, selection.get("com.google.gson.functional.DefaultTypeAdaptersTest").size());
        assertEquals("testDateSerializationWithPattern", selection.get("com.google.gson.functional.DefaultTypeAdaptersTest").get(0));
    }
}
