package fr.davidson.diff.jjoules.localization.configuration;


import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.localization.output.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final Map<String, List<String>> testsList;

    public final String diff;

    public final Report report;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String testsList,
                         String pathToDiff,
                         Report report) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        final String[] testClassList = testsList.split(",");
        this.testsList = new HashMap<>();
        for (String testClass : testClassList) {
            final String[] testClassSplit = testClass.split("#");
            this.testsList.put(testClassSplit[0], new ArrayList<>());
            Arrays.stream(testClassSplit[1].split("\\+")).forEach(this.testsList.get(testClassSplit[0])::add);
        }
        if (pathToDiff == null || pathToDiff.isEmpty()) {
            LOGGER.warn("No path to diff file has been specified.");
            LOGGER.warn("I'll compute a diff file using the UNIX diff command");
            LOGGER.warn("You may encounter troubles.");
            LOGGER.warn("If so, please specify a path to a correct diff file");
            LOGGER.warn("or implement a new way to compute a diff file.");
            this.diff = new DiffComputer()
                    .computeDiffWithDiffCommand(
                            new File(pathToFirstVersion + "/" + SRC_FOLDER),
                            new File(pathToSecondVersion + "/" + SRC_FOLDER)
                    );
        } else {
            this.diff = this.readFile(pathToDiff);
        }
        this.report = report;
    }

    private String readFile(String pathToFileToRead) {
        final String nl = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(pathToFileToRead)))) {
            reader.lines().forEach(
                    line -> builder.append(line).append(nl)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }
}
