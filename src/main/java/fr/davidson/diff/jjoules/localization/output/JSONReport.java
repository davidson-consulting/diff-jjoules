package fr.davidson.diff.jjoules.localization.output;

import fr.davidson.diff.jjoules.util.JSONUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JSONReport implements Report {

    public static final String OUTPUT_PATH_NAME_SELECTED_TESTS = "selectedTests.json";

    public static final String OUTPUT_PATH_NAME_SUSPECT_LINES = "suspectLines.json";

    private final String outputPath;

    public JSONReport(String outputPath) {
        this.outputPath = outputPath;
        final File outputDirectory = new File(outputPath);
        if (!outputDirectory.exists()) {
            final boolean mkdir = outputDirectory.mkdirs();
            if (!mkdir) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void outputSelectedTests(Map<String, List<String>> testsList) {
        JSONUtils.write(outputPath + "/" + OUTPUT_PATH_NAME_SELECTED_TESTS, testsList);
    }

    @Override
    public void outputSuspectLines(Map<String, List<Integer>> faultyLines) {
        JSONUtils.write(outputPath + "/" + OUTPUT_PATH_NAME_SUSPECT_LINES, faultyLines);
    }

}
