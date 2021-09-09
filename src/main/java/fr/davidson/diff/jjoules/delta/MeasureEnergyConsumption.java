package fr.davidson.diff.jjoules.delta;

import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.TestResult;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/09/2021
 */
public class MeasureEnergyConsumption {

    private static final String PATH_TO_JJOULES_REPORT = "/target/jjoules-reports/";

    private static final String KEY_ENERGY_CONSUMPTION = "package|uJ";

    private static final String KEY_INSTRUCTIONS = "instructions";

    private static final String KEY_DURATIONS = "duration|ns";

    private static final String KEY_CYCLES = "cycles";

    private static final String KEY_BRANCHES = "branches";

    private static final String KEY_BRANCH_MISSES = "branch-misses";

    private static final String KEY_CACHES = "cache-reference";

    private static final String KEY_CACHE_MISSES = "cache-misses";

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureEnergyConsumption.class);

    static void measureEnergyConsumptionForBothVersion(
            final Configuration configuration,
            final Datas dataV1,
            final Datas dataV2,
            Map<String, List<String>> testsList) {
        for (int i = 0; i < configuration.iterations; i++) {
            runForVersionAndCollect(
                    configuration.pathToFirstVersion,
                    configuration.getClasspathV1AsString(),
                    dataV1,
                    testsList
            );
            runForVersionAndCollect(
                    configuration.pathToSecondVersion,
                    configuration.getClasspathV2AsString(),
                    dataV2,
                    testsList
            );
        }
    }

    private static void runForVersionAndCollect(
            final String pathToVersion,
            final String classpath,
            final Map<String, List<Data>> data,
            Map<String, List<String>> testsList) {
            EntryPoint.jUnit5Mode = false;
            EntryPoint.verbose = false;
            EntryPoint.timeoutInMs = 100000;
            try {
                final String[] testClassNames = testsList.keySet().toArray(new String[0]);
                final String[] testMethodsNames = testsList.values()
                        .stream()
                        .flatMap(Collection::stream)
                        .toArray(String[]::new);
                EntryPoint.workingDirectory = new File(pathToVersion);
                LOGGER.info("{}", EntryPoint.workingDirectory.getAbsolutePath());
                final TestResult testResult = EntryPoint.runTests(
                        classpath +
                                ":" + pathToVersion + "/target/classes" +
                                ":" + pathToVersion + "/target/test-classes",
                        testClassNames,
                        testMethodsNames
                );
            } catch (TimeoutException | java.lang.RuntimeException e) {
                throw new RuntimeException(e);
            }
        readAllJSonFiles(pathToVersion, data);
    }

    private static void readAllJSonFiles(
            final String pathToVersion,
            final Map<String, List<Data>> dataPerTest
    ) {
        final File jjoulesReportDirectory = new File(pathToVersion + PATH_TO_JJOULES_REPORT);
        LOGGER.info("Reading json file from {}({})", jjoulesReportDirectory.getAbsolutePath(),
                (jjoulesReportDirectory.listFiles() != null ?
                        Arrays.stream(jjoulesReportDirectory.listFiles())
                                .map(File::getAbsolutePath)
                                .collect(Collectors.joining(",")) : ""
                )
        );
        for (File jsonFile : jjoulesReportDirectory.listFiles()) {
            final String testName = toTestName(jsonFile.getAbsolutePath());
            final Map<String, Double> jjoulesReports = JSONUtils.read(jsonFile.getAbsolutePath(), Map.class);
            final Data data = new Data(
                    jjoulesReports.get(KEY_ENERGY_CONSUMPTION),
                    jjoulesReports.get(KEY_INSTRUCTIONS),
                    jjoulesReports.get(KEY_DURATIONS),
                    jjoulesReports.get(KEY_CYCLES),
                    jjoulesReports.get(KEY_CACHES),
                    jjoulesReports.get(KEY_CACHE_MISSES),
                    jjoulesReports.get(KEY_BRANCHES),
                    jjoulesReports.get(KEY_BRANCH_MISSES)
            );
            Utils.addToGivenMap(testName, data, dataPerTest);
        }
    }

    private static String toTestName(String path) {
        final String[] split = path.split("/");
        return split[split.length - 1].split("\\.json")[0].replace("-", "#");
    }

}
