package fr.davidson.diff.jjoules.delta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.TestResult;
import eu.stamp_project.testrunner.runner.Failure;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.tlpc.sensor.IndicatorPerLabel;
import fr.davidson.tlpc.sensor.IndicatorsPerIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/09/2021
 */
public class MeasureEnergyConsumption {

    private Set<Failure> failures;

    private static final String JSON_REPORT_FAILURE_PATHNAME = "test_failures.json";

    public MeasureEnergyConsumption() {
        this.failures = new HashSet<>();
    }

    public void measureEnergyConsumptionForBothVersion(
            final Configuration configuration,
            final Datas dataV1,
            final Datas dataV2,
            Map<String, Set<String>> testsList) {
        for (int i = 0; i < configuration.getIterations(); i++) {
            runForVersion(
                    configuration.getPathToFirstVersion(),
                    Constants.joinPaths(configuration.getWrapper().getBinaries(), configuration.getClasspathV1AsString()),
                    testsList,
                    configuration.isJunit4()
            );
            readAllJSonFiles(configuration.getPathToFirstVersion(), dataV1);
            runForVersion(
                    configuration.getPathToSecondVersion(),
                    Constants.joinPaths(configuration.getWrapper().getBinaries(), configuration.getClasspathV2AsString()),
                    testsList,
                    configuration.isJunit4()
            );
            readAllJSonFiles(configuration.getPathToSecondVersion(), dataV2);
        }
        outputFailures(configuration);
    }

    private void outputFailures(Configuration configuration) {
        try (final FileWriter writer = new FileWriter(
                Constants.joinFiles(configuration.getOutput(), JSON_REPORT_FAILURE_PATHNAME))) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this.failures));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void runForVersion(
            final String pathToVersion,
            final String classpath,
            Map<String, Set<String>> testsList,
            boolean junit4
    ) {
        EntryPoint.jUnit5Mode = !junit4;
        EntryPoint.verbose = true;
        EntryPoint.timeoutInMs = 100000;
        try {
            final String[] testClassNames = testsList.keySet().toArray(new String[0]);
            final String[] testMethodsNames = testsList.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toArray(String[]::new);
            EntryPoint.workingDirectory = new File(pathToVersion);
            final TestResult testResult = EntryPoint.runTests(
                    classpath,
                    testClassNames,
                    testMethodsNames
            );
            failures.addAll(testResult.getFailingTests());
        } catch (TimeoutException | java.lang.RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void readAllJSonFiles(
            final String pathToVersion,
            final Map<String, List<Data>> dataPerTest
    ) {
        final IndicatorsPerIdentifier tlpcReport =
                JSONUtils.read(
                        Constants.joinFiles(pathToVersion, InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME),
                        IndicatorsPerIdentifier.class
                );
        for (String fullQualifiedNameTestMethod : tlpcReport.keySet()) {
            final IndicatorPerLabel indicatorPerLabel = tlpcReport.get(fullQualifiedNameTestMethod);
            final Data data = new Data(
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_ENERGY_CONSUMPTION),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_INSTRUCTIONS),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_DURATION),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_CYCLES),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_CACHES),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_CACHE_MISSES),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_BRANCHES),
                    indicatorPerLabel.get(IndicatorPerLabel.KEY_BRANCH_MISSES)
            );
            Utils.addToGivenMap(fullQualifiedNameTestMethod, data, dataPerTest);
        }
    }
}
