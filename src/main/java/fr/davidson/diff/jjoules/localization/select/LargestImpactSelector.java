package fr.davidson.diff.jjoules.localization.select;

import fr.davidson.diff.jjoules.util.JSONUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LargestImpactSelector implements Selector {

    @Override
    public Map<String, List<String>> select(String pathJSONDataFirstVersion, String pathJSONDataSecondVersion) {
        final Map<String, Map> dataV1 = JSONUtils.read(pathJSONDataFirstVersion, Map.class);
        final Map<String, Map> dataV2 = JSONUtils.read(pathJSONDataSecondVersion, Map.class);
        final Map<String, Double> deltaPerTestName = new HashMap<>();
        final double deltaAcc = this.computeDelta(dataV1, dataV2, deltaPerTestName);
        return _select(deltaPerTestName, deltaAcc);
    }

    @NotNull
    private Map<String, List<String>> _select(Map<String, Double> deltaPerTestName, double deltaAcc) {
        final Map<String, List<String>> selectedTests = new HashMap<>();
        for (String testName : deltaPerTestName.keySet()) {
            final double currentDelta = deltaPerTestName.get(testName);
            final double percCurrentDelta = (currentDelta / deltaAcc) * 100.0D;
            if (percCurrentDelta > 25) {
                final String[] splitTestName = testName.split("-");
                if (!selectedTests.containsKey(splitTestName[0])) {
                    selectedTests.put(splitTestName[0], new ArrayList<>());
                }
                selectedTests.get(splitTestName[0]).add(splitTestName[1]);
            }
        }
        return selectedTests;
    }

    // not pure
    private double computeDelta(final Map<String, Map> dataV1,
                                final Map<String, Map> dataV2,
                                final Map<String, Double> deltaPerTestName) {
        double deltaAcc = 0D;
        for (String testName : dataV1.keySet()) {
            if (dataV2.containsKey(testName)) {
                final double energyV1 = (double) dataV1.get(testName).get("energy");
                final double energyV2 = (double) dataV2.get(testName).get("energy");
                final double currentEnergyDelta = energyV2 - energyV1;
                deltaAcc += currentEnergyDelta;
                deltaPerTestName.put(testName, currentEnergyDelta);
            }
        }
        return deltaAcc;
    }

}
