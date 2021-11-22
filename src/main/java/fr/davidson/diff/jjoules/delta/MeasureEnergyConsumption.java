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

    public static final String PATH_TO_JJOULES_REPORT = "/target/jjoules-reports/";

    public static final String KEY_ENERGY_CONSUMPTION = "package|uJ";

    public static final String KEY_INSTRUCTIONS = "instructions";

    public static final String KEY_DURATIONS = "duration|ns";

    public static final String KEY_CYCLES = "cycles";

    public static final String KEY_BRANCHES = "branches";

    public static final String KEY_BRANCH_MISSES = "branch-misses";

    public static final String KEY_CACHES = "cache-reference";

    public static final String KEY_CACHE_MISSES = "cache-misses";

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureEnergyConsumption.class);

    public void measureEnergyConsumptionForBothVersion(
            final Configuration configuration,
            final Datas dataV1,
            final Datas dataV2,
            Map<String, Set<String>> testsList) {
        for (int i = 0; i < configuration.iterations; i++) {
            runForVersion(
                    configuration.pathToFirstVersion,
                    configuration.getClasspathV1AsString(),
                    testsList,
                    configuration.junit4
            );
            readAllJSonFiles(configuration.pathToFirstVersion, dataV1);
            runForVersion(
                    configuration.pathToSecondVersion,
                    configuration.getClasspathV2AsString(),
                    testsList,
                    configuration.junit4
            );
            readAllJSonFiles(configuration.pathToSecondVersion, dataV2);
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
    }

    private void readAllJSonFiles(
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

    private String toTestName(String path) {
        final String[] split = path.split("/");
        return split[split.length - 1].split("\\.json")[0].replace("-", "#");
    }

}
