package fr.davidson.diff.jjoules.localization.output;

import java.util.List;
import java.util.Map;

public interface Report {

    public void output(Map<String, List<Integer>> faultyLines);

}
