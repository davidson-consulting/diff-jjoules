package fr.davidson.diff.jjoules.delta;

import eu.stamp_project.testrunner.EntryPoint;
import fr.davidson.diff.jjoules.delta.configuration.Configuration;
import fr.davidson.diff.jjoules.delta.configuration.Options;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(fr.davidson.diff.jjoules.mark.Main.class);

    private static final String PATH_TO_JJOULES_REPORT = "/target/jjoules-reports/";

    private static final String KEY_ENERGY_CONSUMPTION = "package|uJ";

    private static final String KEY_INSTRUCTIONS = "instructions";

    private static final String KEY_DURATIONS = "duration|ns";

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        LOGGER.info("{}", configuration.toString());
        final Map<String, List<String>> testsList = CSVReader.readFile(configuration.pathToTestListAsCSV);
        final String[] testClassNames = testsList.keySet().toArray(new String[0]);
        final String[] testMethodsNames = testsList.values()
                .stream()
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        LOGGER.info("{}", Arrays.toString(testClassNames));
        LOGGER.info("{}", Arrays.toString(testMethodsNames));
        final Datas dataV1 = new Datas();
        final Datas dataV2 = new Datas();
        EntryPoint.verbose = true;
        EntryPoint.jUnit5Mode = true;
        EntryPoint.timeoutInMs = 100000;
        for (int i = 0; i < configuration.iterations; i++) {
            runForVersionAndCollect(
                    configuration.pathToFirstVersion,
                    configuration.classpathV1,
                    testClassNames,
                    testMethodsNames,
                    dataV1
            );
            runForVersionAndCollect(
                    configuration.pathToSecondVersion,
                    configuration.classpathV2,
                    testClassNames,
                    testMethodsNames,
                    dataV2
            );
        }
        final Map<String, Data> mediansV1 = computeMedian(dataV1);
        final Map<String, Data> mediansV2 = computeMedian(dataV2);
        final Deltas deltaPerTestMethodName = computeDelta(mediansV1, mediansV2);
        JSONUtils.write(configuration.output + "/data_v1.json", dataV1);
        JSONUtils.write(configuration.output + "/data_v2.json", dataV2);
        JSONUtils.write(configuration.output + "/delta.json", deltaPerTestMethodName);
    }

    private static Deltas computeDelta(
            Map<String, Data> mediansV1,
            Map<String, Data> mediansV2
    ) {
        final Deltas deltaPerName = new Deltas();
        for (String testMethodName : mediansV1.keySet()) {
            deltaPerName.put(testMethodName, new Delta(mediansV1.get(testMethodName), mediansV2.get(testMethodName)));
        }
        return deltaPerName;
    }

    // TODO asking my self : should we take the medians separately or should we take the medians over the 3 measures ?
    private static Map<String, Data> computeMedian(final Map<String, List<Data>> data) {
        final Map<String, Data> medianPerTestName = new HashMap<>();
        for (String testMethodName : data.keySet()) {
            medianPerTestName.put(testMethodName,
                    new Data(
                            getMedian(data.get(testMethodName), Data::getEnergy),
                            getMedian(data.get(testMethodName), Data::getInstructions),
                            getMedian(data.get(testMethodName), Data::getDurations)
                    )
            );
        }
        return medianPerTestName;
    }

    private static double getMedian(List<Data> values, Function<Data, Double> getter) {
        return getMedian(values.stream().map(getter).sorted().collect(Collectors.toList()));
    }

    private static double getMedian(List<Double> values) {
        return values.size() % 2 == 0 ?
                values.get(values.size() / 2) + values.get((values.size() + 1) / 2) :
                values.get(values.size() / 2);
    }

    private static void runForVersionAndCollect(
            final String pathToVersion,
            final String classpath,
            final String[] testClassNames,
            final String[] testMethodsNames,
            final Map<String, List<Data>> data
    ) {
        try {
            EntryPoint.workingDirectory = new File(pathToVersion);
            LOGGER.info("CWD {}", EntryPoint.workingDirectory.getAbsolutePath());
            EntryPoint.runTests(
                    classpath,
                    testClassNames,
                    testMethodsNames
            );
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        readAllJSonFiles(pathToVersion, data);
    }

    private static void readAllJSonFiles(
            final String pathToVersion,
            final Map<String, List<Data>> dataPerTest
    ) {
        final File jjoulesReportDirectory = new File(pathToVersion + PATH_TO_JJOULES_REPORT);
        LOGGER.info("Reading json file from {} ({})", jjoulesReportDirectory.getAbsolutePath(), jjoulesReportDirectory.listFiles());
        for (File jsonFile : jjoulesReportDirectory.listFiles()) {
            LOGGER.info("{}", jsonFile.getAbsolutePath());
            final String testName = toTestName(jsonFile.getAbsolutePath());
            final Map<String, Double> jjoulesReports = JSONUtils.read(jsonFile.getAbsolutePath(), Map.class);
            final Data data = new Data(
                    jjoulesReports.get(KEY_ENERGY_CONSUMPTION),
                    jjoulesReports.get(KEY_INSTRUCTIONS),
                    jjoulesReports.get(KEY_DURATIONS)
            );
            Utils.addToGivenMap(testName, data, dataPerTest);
        }
    }

    private static String toTestName(String path) {
        final String[] split = path.split("/");
        return split[split.length - 1].split("\\.json")[0].replace("-", "#");
    }

}
