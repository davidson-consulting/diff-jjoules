package fr.davidson.diff.jjoules.localization.output;

import fr.davidson.diff.jjoules.instrumentation.maven.JJoulesInjection;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.TestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JSONReport implements Report {

    private static final Logger LOGGER = LoggerFactory.getLogger(JJoulesInjection.class);

    public static final String OUTPUT_PATH_NAME_SELECTED_TESTS = "selectedTests.json";

    public static final String OUTPUT_PATH_NAME_DELTA_PER_TEST = "deltas.json";

    public static final String OUTPUT_PATH_NAME_SUSPECT_LINES = "suspectLines.json";

    private final String outputPath;

    public JSONReport(String outputPath) {
        this.outputPath = outputPath;
        final File outputDirectory = new File(outputPath);
        if (!outputDirectory.exists()) {
            final boolean mkdir = outputDirectory.mkdirs();
            LOGGER.info("Creating {}", outputDirectory.getAbsolutePath());
            if (!mkdir) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void outputSelectedTests(Map<String, List<String>> testsList, Map<String, List<TestRecord>> testRecordPerTestClass) {
        LOGGER.info("writing {}", outputPath + "/" + OUTPUT_PATH_NAME_DELTA_PER_TEST);
        JSONUtils.write(outputPath + "/" + OUTPUT_PATH_NAME_DELTA_PER_TEST, testRecordPerTestClass);
        LOGGER.info("writing {}", outputPath + "/" + OUTPUT_PATH_NAME_SELECTED_TESTS);
        JSONUtils.write(outputPath + "/" + OUTPUT_PATH_NAME_SELECTED_TESTS, testsList);
    }

    @Override
    public void outputSuspectLines(Map<String, List<Integer>> faultyLines) {
        LOGGER.info("writing {}", outputPath + "/" + OUTPUT_PATH_NAME_SUSPECT_LINES);
        JSONUtils.write(outputPath + "/" + OUTPUT_PATH_NAME_SUSPECT_LINES, faultyLines);
    }

}
