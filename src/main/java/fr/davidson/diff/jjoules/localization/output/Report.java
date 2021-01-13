package fr.davidson.diff.jjoules.localization.output;

import java.util.List;
import java.util.Map;

public interface Report {

    public void outputSelectedTests(Map<String, List<String>> testsList);

    public void outputSuspectLines(Map<String, List<Integer>> faultyLines);


}
