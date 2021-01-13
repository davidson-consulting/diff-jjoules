package fr.davidson.diff.jjoules.localization.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class JSONReport implements Report {

    public static final String OUTPUT_PATH_NAME = "suspectLines.json";

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
    public void output(Map<String, List<Integer>> faultyLines) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (final FileWriter fileWriter = new FileWriter(outputPath + "/" + OUTPUT_PATH_NAME, false)) {
            fileWriter.write(gson.toJson(faultyLines));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
