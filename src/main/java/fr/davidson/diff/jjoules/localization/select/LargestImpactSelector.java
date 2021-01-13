package fr.davidson.diff.jjoules.localization.select;

import fr.davidson.diff.jjoules.util.JSONUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LargestImpactSelector implements Selector {

    private double deltaAcc;

    private Map<String, Double> deltaPerTestName;

    public LargestImpactSelector() {
        this.deltaAcc = 0.0D;
        this.deltaPerTestName = new HashMap<>();
    }

    @Override
    public double getDelta() {
        return this.deltaAcc;
    }

    @Override
    public Map<String, Double> getDeltaPerTest() {
        return this.deltaPerTestName;
    }

    @Override
    public Map<String, List<String>> select(String pathJSONDataFirstVersion, String pathJSONDataSecondVersion) {
        final Map<String, Map> dataV1 = JSONUtils.read(pathJSONDataFirstVersion, Map.class);
        final Map<String, Map> dataV2 = JSONUtils.read(pathJSONDataSecondVersion, Map.class);
        this.computeDelta(dataV1, dataV2);
        return _select();
    }

    @NotNull
    private Map<String, List<String>> _select() {
        final Map<String, List<String>> selectedTests = new HashMap<>();
        for (String testName : deltaPerTestName.keySet()) {
            final double currentDelta = deltaPerTestName.get(testName);
            final double percCurrentDelta = (currentDelta / this.deltaAcc) * 100.0D;
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
    private void computeDelta(final Map<String, Map> dataV1,
                              final Map<String, Map> dataV2) {
        this.deltaAcc = 0D;
        this.deltaPerTestName = new HashMap<>();
        for (String testName : dataV1.keySet()) {
            if (dataV2.containsKey(testName)) {
                final double energyV1 = (double) dataV1.get(testName).get("energy");
                final double energyV2 = (double) dataV2.get(testName).get("energy");
                final double currentEnergyDelta = energyV2 - energyV1;
                deltaAcc += currentEnergyDelta;
                deltaPerTestName.put(testName, currentEnergyDelta);
            }
        }
    }

}
