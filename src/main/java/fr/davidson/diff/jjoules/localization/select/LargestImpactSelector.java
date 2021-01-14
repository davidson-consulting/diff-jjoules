package fr.davidson.diff.jjoules.localization.select;

import fr.davidson.diff.jjoules.util.JSONUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LargestImpactSelector implements Selector {

    private double positiveDelta;

    private double negativeDelta;

    private double globalDelta;

    private Map<String, Double> deltaPerTestName;

    private final Map<String, List<TestRecord>> testRecordPerTestClass;

    public LargestImpactSelector() {
        this.positiveDelta = 0.0D;
        this.negativeDelta = 0.0D;
        this.globalDelta = 0.0D;
        this.deltaPerTestName = new HashMap<>();
        this.testRecordPerTestClass = new HashMap<>();
    }

    @Override
    public Map<String, List<TestRecord>> getTestRecordPerTestClass() {
        return testRecordPerTestClass;
    }

    @Override
    public Map<String, List<String>> select(String pathJSONDataFirstVersion, String pathJSONDataSecondVersion) {
        final Map<String, Map> dataV1 = JSONUtils.read(pathJSONDataFirstVersion, Map.class);
        final Map<String, Map> dataV2 = JSONUtils.read(pathJSONDataSecondVersion, Map.class);
        this.computeDelta(dataV1, dataV2);
        return _select();
    }

    private void computeDelta(final Map<String, Map> dataV1,
                              final Map<String, Map> dataV2) {
        this.positiveDelta = 0.0D;
        this.negativeDelta = 0.0D;
        this.globalDelta = 0.0D;
        for (String testName : dataV1.keySet()) {
            if (dataV2.containsKey(testName)) {
                final double energyV1 = (double) dataV1.get(testName).get("energy");
                final double energyV2 = (double) dataV2.get(testName).get("energy");
                final double currentEnergyDelta = energyV2 - energyV1;
                if (currentEnergyDelta < 0) {
                    negativeDelta += currentEnergyDelta;
                } else {
                    positiveDelta += currentEnergyDelta;
                }
                globalDelta += currentEnergyDelta;
                deltaPerTestName.put(testName, currentEnergyDelta);
            }
        }
    }

    @NotNull
    private Map<String, List<String>> _select() {
        final Map<String, List<String>> selectedTests = new HashMap<>();
        for (String testName : deltaPerTestName.keySet()) {
            final String[] splitTestName = testName.split("-");
            final String testClassName = splitTestName[0];
            final String testMethodName = splitTestName[1];
            final double currentDelta = deltaPerTestName.get(testName);
            final double globalPercentage = (currentDelta / this.globalDelta) * 100.0D;
            final double categoryDelta = currentDelta < 0 ? this.negativeDelta : this.positiveDelta;
            final double categoryPercentage = (currentDelta / categoryDelta) * 100.0D;
            final TestRecord testRecord = new TestRecord(
                    testMethodName,
                    currentDelta,
                    globalPercentage,
                    categoryPercentage,
                    currentDelta > 0 ? TestRecord.Category.POSITIVE : TestRecord.Category.NEGATIVE
            );
            this.addToGivenMap(testClassName, testRecord, this.testRecordPerTestClass);
            if (currentDelta > 0 && categoryPercentage > 25) { // TODO
                this.addToGivenMap(testClassName, testMethodName, selectedTests);
            }
        }
        this.addToGivenMap("global",
                new TestRecord(
                        "",
                        this.globalDelta,
                        100.0D,
                        0.0,
                        this.globalDelta < 0 ? TestRecord.Category.NEGATIVE : TestRecord.Category.POSITIVE
                ), this.testRecordPerTestClass);
        return selectedTests;
    }

    private <T> void addToGivenMap(final String key, T value, Map<String, List<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new ArrayList<>());
        }
        givenMap.get(key).add(value);
    }

}
