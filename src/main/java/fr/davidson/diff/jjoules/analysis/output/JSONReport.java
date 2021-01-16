package fr.davidson.diff.jjoules.analysis.output;

import fr.davidson.diff.jjoules.analysis.lines.LinesDeltaClassifier;
import fr.davidson.diff.jjoules.analysis.tests.TestDeltaClassifier;
import fr.davidson.diff.jjoules.util.JSONUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JSONReport implements Report {

    private static final String OUTPUT_PATH_TESTS_RECORDS = "test_records.json";

    private static final String OUTPUT_PATH_TESTS_CLASSIFICATIONS = "test_classification.json";

    private static final String OUTPUT_PATH_LINES_CLASSIFICATIONS = "lines_classification.json";

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
    public void outputTestsClassification(TestDeltaClassifier testDeltaClassifier) {
        JSONUtils.write(this.outputPath + "/" + OUTPUT_PATH_TESTS_RECORDS, testDeltaClassifier.getTestRecords());
        final Map<String, Map<?, ?>> classifications = new HashMap<>();
        classifications.put("positive", testDeltaClassifier.getPositiveTests());
        classifications.put("negative", testDeltaClassifier.getNegativeTests());
        classifications.put("neutral", testDeltaClassifier.getNeutralTests());
        JSONUtils.write(this.outputPath + "/" + OUTPUT_PATH_TESTS_CLASSIFICATIONS, classifications);
    }

    @Override
    public void outputLinesClassification(LinesDeltaClassifier linesDeltaClassifier) {
        final Map<String, Map<?, ?>> classifications = new HashMap<>();
        classifications.put("positive", linesDeltaClassifier.getPositiveLines());
        classifications.put("negative", linesDeltaClassifier.getNegativeLines());
        classifications.put("unknown", linesDeltaClassifier.getUnknownLines());
        JSONUtils.write(this.outputPath + "/" + OUTPUT_PATH_LINES_CLASSIFICATIONS, classifications);
    }
}
