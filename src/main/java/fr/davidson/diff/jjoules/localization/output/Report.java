package fr.davidson.diff.jjoules.localization.output;

import fr.davidson.diff.jjoules.localization.select.TestRecord;

import java.util.List;
import java.util.Map;

public interface Report {

    public void outputSelectedTests(Map<String, List<String>> testsList, Map<String, List<TestRecord>> testRecordPerTestClass);

    public void outputSuspectLines(Map<String, List<Integer>> faultyLines);

}
