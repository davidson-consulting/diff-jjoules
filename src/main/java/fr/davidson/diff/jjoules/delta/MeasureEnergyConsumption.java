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

    public static final String KEY_DURATIONS = "duration|ns";

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
            final TestResult testResult = EntryPoint.runTests(
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
        final File directory = new File(pathToVersion + Constants.FILE_SEPARATOR + InstrumentationProcessor.FOLDER_MEASURES_PATH);
        Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .forEach(file -> {
                    final String testClassName = this.toTestName(file.getName());
                    final Map<String, Map<String, Double>> tlpcReport = JSONUtils.read(file.getAbsolutePath(), Map.class);
                    for (String testMethodName : tlpcReport.keySet()) {
                        final Map<String, Double> tlpcReportTest = tlpcReport.get(testMethodName);
                        final Data data = new Data(
                                tlpcReportTest.get(KEY_ENERGY_CONSUMPTION),
                                tlpcReportTest.get(KEY_INSTRUCTIONS),
                                0,
                                tlpcReportTest.get(KEY_CYCLES),
                                tlpcReportTest.get("LLC_MISSES"),
                                0, 0, 0
                        );
                        Utils.addToGivenMap(new FullQualifiedName(testClassName, testMethodName).toString() , data, dataPerTest);
                    }
                });
    }

    private String toTestName(String path) {
        final String[] split = path.split(Constants.FILE_SEPARATOR);
        return split[split.length - 1].split("\\.json")[0].replace("-", "#");
    }

}
