package fr.davidson.diff.jjoules.localization.select;

import java.util.List;
import java.util.Map;

public interface Selector {

    public Map<String, List<String>> select(String pathJSONDataFirstVersion, String pathJSONDataSecondVersion);

    public double getDelta();

    public Map<String, Double> getDeltaPerTest();

}
