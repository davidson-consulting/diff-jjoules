package fr.davidson.diff.jjoules.analysis.tests;

import fr.davidson.diff.jjoules.util.TestRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDeltaClassifier {

    private Map<String, Double> positiveTests;

    private Map<String, Double> negativeTests;

    private Map<String, Double> neutralTests;

    private List<TestRecord> testRecords;

    public TestDeltaClassifier() {
        this.positiveTests = new HashMap<>();
        this.negativeTests = new HashMap<>();
        this.neutralTests = new HashMap<>();
        this.testRecords = new ArrayList<>();
    }

    public void classify(DeltasComputation deltasComputation) {
        this.classify(deltasComputation.getDeltaPerTestName(),
                deltasComputation.getPositiveDelta(),
                deltasComputation.getNegativeDelta(),
                deltasComputation.getGlobalDelta()
        );
    }

    public void classify(Map<String, Double> deltaPerTestName, double positiveDelta, double negativeDelta, double globalDelta) {
        final long nbPositiveTest = deltaPerTestName
                .keySet()
                .stream()
                .filter(testName -> deltaPerTestName.get(testName) > 0)
                .count();
        final long nbNegativeTest = deltaPerTestName
                .keySet()
                .stream()
                .filter(testName -> deltaPerTestName.get(testName) < 0)
                .count();
        final double sharePerPositiveTest = 100.0D / (double) nbPositiveTest;
        final double sharePerNegativeTest = 100.0D / (double) nbNegativeTest;

        for (String testName : deltaPerTestName.keySet()) {
            final double currentDelta = deltaPerTestName.get(testName);
            final double categoryDelta = currentDelta < 0 ? negativeDelta : positiveDelta;
            final double categoryPercentage = (Math.abs(currentDelta) / Math.abs(categoryDelta)) * 100.0D;
            final double globalPercentage = (Math.abs(currentDelta) / Math.abs(globalDelta)) * 100.0D;
            final TestRecord testRecord = new TestRecord(
                    testName,
                    currentDelta,
                    globalPercentage,
                    categoryPercentage,
                    currentDelta > 0 ? TestRecord.Category.POSITIVE : TestRecord.Category.NEGATIVE
            );
            this.testRecords.add(testRecord);
            final double currentShare = currentDelta > 0 ? sharePerPositiveTest : sharePerNegativeTest;
            if (categoryPercentage > (1.50D * currentShare)) {
                if (currentDelta > 0) {
                    this.positiveTests.put(testName, currentDelta);
                } else {
                    this.negativeTests.put(testName, currentDelta);
                }
            } else {
                this.neutralTests.put(testName, currentDelta);
            }
        }
        this.testRecords.add(new TestRecord(
                "global",
                globalDelta,
                100.0D,
                0.0D,
                TestRecord.Category.NEUTRAL
        ));
        this.testRecords.add(new TestRecord(
                "negative",
                negativeDelta,
                (Math.abs(negativeDelta) / Math.abs(globalDelta)) * 100.0D,
                sharePerNegativeTest,
                TestRecord.Category.NEGATIVE
        ));
        this.testRecords.add(new TestRecord(
                "positive",
                positiveDelta,
                (Math.abs(positiveDelta) / Math.abs(globalDelta)) * 100.0D,
                sharePerPositiveTest,
                TestRecord.Category.POSITIVE
        ));
    }

    public List<TestRecord> getTestRecords() {
        return testRecords;
    }

    public Map<String, Double> getPositiveTests() {
        return positiveTests;
    }

    public Map<String, Double> getNegativeTests() {
        return negativeTests;
    }

    public Map<String, Double> getNeutralTests() {
        return neutralTests;
    }
}
