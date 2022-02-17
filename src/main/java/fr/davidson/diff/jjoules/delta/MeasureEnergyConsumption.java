package fr.davidson.diff.jjoules.delta;

import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.TestResult;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.tlpc.sensor.IndicatorPerLabel;
import fr.davidson.tlpc.sensor.IndicatorsPerIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/09/2021
 */
public class MeasureEnergyConsumption {

    public static final String KEY_ENERGY_CONSUMPTION = "RAPL_ENERGY_PKG";

    public static final String KEY_INSTRUCTIONS = "INSTRUCTIONS_RETIRED";

    public static final String KEY_DURATIONS = "duration";

    public static final String KEY_CYCLES = "CYCLES";

    public static final String KEY_BRANCHES = "branches";

    public static final String KEY_BRANCH_MISSES = "LLC_MISSES";

    public static final String KEY_CACHES = "cache-reference";

    public static final String KEY_CACHE_MISSES = "cache-misses";

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureEnergyConsumption.class);

    public void measureEnergyConsumptionForBothVersion(
            final Configuration configuration,
            final Datas dataV1,
            final Datas dataV2,
            Map<String, Set<String>> testsList) {
        for (int i = 0; i < configuration.getIterations(); i++) {
            runForVersion(
                    configuration.getPathToFirstVersion(),
                    configuration.getClasspathV1AsString() +
                            Constants.PATH_SEPARATOR +
                            configuration.getWrapper().getBinaries(),
                    testsList,
                    configuration.isJunit4()
            );
            readAllJSonFiles(configuration.getPathToFirstVersion(), dataV1);
            runForVersion(
                    configuration.getPathToSecondVersion(),
                    configuration.getClasspathV2AsString() +
                            Constants.PATH_SEPARATOR +
                            configuration.getWrapper().getBinaries(),
                    testsList,
                    configuration.isJunit4()
            );
            readAllJSonFiles(configuration.getPathToSecondVersion(), dataV2);
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
            LOGGER.info("{}", EntryPoint.workingDirectory.getAbsolutePath());
            EntryPoint.runTests(
                    classpath,
                    testClassNames,
                    testMethodsNames
            );
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
                    indicatorPerLabel.get(KEY_ENERGY_CONSUMPTION),
                    indicatorPerLabel.get(KEY_INSTRUCTIONS),
                    indicatorPerLabel.get(KEY_DURATIONS),
                    indicatorPerLabel.get(KEY_CYCLES),
                    indicatorPerLabel.get(KEY_BRANCH_MISSES),
                    0, 0, 0
            );
            Utils.addToGivenMap(fullQualifiedNameTestMethod , data, dataPerTest);
        }
    }
}
