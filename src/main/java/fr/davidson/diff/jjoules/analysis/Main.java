package fr.davidson.diff.jjoules.analysis;


import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import fr.davidson.diff.jjoules.analysis.configuration.Configuration;
import fr.davidson.diff.jjoules.analysis.configuration.Options;
import fr.davidson.diff.jjoules.analysis.lines.LinesDeltaClassifier;
import fr.davidson.diff.jjoules.analysis.tests.TestDeltaClassifier;
import fr.davidson.diff.jjoules.analysis.tests.DeltasComputation;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        System.out.println(configuration.toString());
        final Map<String, Map> dataJsonV1 = JSONUtils.read(configuration.pathToJSONDataFirstVersion, Map.class);
        final Map<String, Map> dataJsonV2 = JSONUtils.read(configuration.pathToJSONDataSecondVersion, Map.class);
        final Map<String, List<String>> testsList = getTestList(dataJsonV1, dataJsonV2);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV1 = getCoverage(configuration.pathToFirstVersion, testsList);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV2 = getCoverage(configuration.pathToSecondVersion, testsList);
        JSONUtils.write("coverage_v1.json", coverageV1);
        JSONUtils.write("coverage_v2.json", coverageV2);
        // 1 compute the deltas of the tests : POSITIVE, NEGATIVE and NEUTRAL tests
        final DeltasComputation deltasComputation = new DeltasComputation();
        deltasComputation.compute(dataJsonV1, dataJsonV2);
        deltasComputation.getDeltaPerTestName();
        final TestDeltaClassifier testDeltaClassifier = new TestDeltaClassifier();
        testDeltaClassifier.classify(deltasComputation);
        // 2 split the sources lines into three sets : positive, negative, unknown
        final LinesDeltaClassifier linesDeltaClassifier = new LinesDeltaClassifier(
                testDeltaClassifier, configuration.pathToFirstVersion, configuration.pathToSecondVersion, configuration.diff
        );
        linesDeltaClassifier.classify(coverageV1, coverageV2);
        configuration.report.outputTestsClassification(testDeltaClassifier);
        configuration.report.outputLinesClassification(linesDeltaClassifier);
        // 3 split the tests into three sets : test_p, test_n, test_b
        // TODO
    }

    @NotNull
    private static Map<String, List<String>> getTestList(Map<String, Map> dataJsonV1, Map<String, Map> dataJsonV2) {
        final Map<String, List<String>> testsList = new HashMap<>();
        extract(dataJsonV1, testsList);
        extract(dataJsonV2, testsList);
        return testsList;
    }

    private static void extract(Map<String, Map> dataJsonV1, Map<String, List<String>> testsList) {
        for (final String testName : dataJsonV1.keySet()) {
            final String[] splitTestName = testName.split("-");
            final String testClassName = splitTestName[0];
            final String testMethodName = splitTestName[1];
            if (testsList.containsKey(testClassName) && testsList.get(testClassName).contains(testMethodName)) {
               continue;
            }
            Utils.addToGivenMap(testClassName, testMethodName, testsList);
        }
    }

    private static Map<String, Map<String, Map<String, List<Integer>>>> getCoverage(final String pathToFirstVersion, Map<String, List<String>> tests) {
        System.out.println("Computing coverage for " + pathToFirstVersion);
        new CloverExecutor().instrumentAndRunGivenTest(pathToFirstVersion, tests);
        return new CloverReader().read(pathToFirstVersion);
    }

}
