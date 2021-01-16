package fr.davidson.diff.jjoules.localization.select;

import fr.davidson.diff.jjoules.util.TestRecord;

import java.util.List;
import java.util.Map;

public interface Selector {

    public Map<String, List<String>> select(String pathJSONDataFirstVersion, String pathJSONDataSecondVersion);

    public Map<String, List<TestRecord>> getTestRecordPerTestClass();
}
