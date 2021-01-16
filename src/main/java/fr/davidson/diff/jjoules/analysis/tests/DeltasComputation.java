package fr.davidson.diff.jjoules.analysis.tests;

import java.util.HashMap;
import java.util.Map;

public class DeltasComputation {

    private Map<String, Double> deltaPerTestName;

    private double positiveDelta;

    private double negativeDelta;

    private double globalDelta;

    public DeltasComputation() {
        this.deltaPerTestName = new HashMap<>();
        this.positiveDelta = 0.0D;
        this.negativeDelta = 0.0D;
        this.globalDelta = 0.0D;
    }

    public void compute(final Map<String, Map> dataJsonV1, final Map<String, Map> dataJsonV2) {
        this.positiveDelta = 0.0D;
        this.negativeDelta = 0.0D;
        this.globalDelta = 0.0D;
        for (String testName : dataJsonV1.keySet()) {
            if (dataJsonV2.containsKey(testName)) {
                computeDeltaForTest(dataJsonV1, dataJsonV2, testName);
            }
        }
    }

    private void computeDeltaForTest(Map<String, Map> dataJsonV1, Map<String, Map> dataJsonV2, String testName) {
        final double energyV1 = (double) dataJsonV1.get(testName).get("energy");
        final double energyV2 = (double) dataJsonV2.get(testName).get("energy");
        final double currentEnergyDelta = energyV2 - energyV1;
        if (currentEnergyDelta < 0) {
            negativeDelta += currentEnergyDelta;
        } else {
            positiveDelta += currentEnergyDelta;
        }
        globalDelta += currentEnergyDelta;
        deltaPerTestName.put(testName, currentEnergyDelta);
    }

    public Map<String, Double> getDeltaPerTestName() {
        return deltaPerTestName;
    }

    public double getPositiveDelta() {
        return positiveDelta;
    }

    public double getNegativeDelta() {
        return negativeDelta;
    }

    public double getGlobalDelta() {
        return globalDelta;
    }
}
