package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.configuration.Configuration;
import fr.davidson.diff.jjoules.configuration.Options;
import fr.davidson.diff.jjoules.process.JJoulesProcessor;
import fr.davidson.diff.jjoules.util.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 26/11/2020
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String TEST_FOLDER_PATH = "src/test/java/";

    public static void main(String[] args) {
        final Configuration configuration = Options.parse(args);
        final Map<String, List<String>> testsList = CSVReader.readFile(configuration.pathToTestListAsCSV);
        LOGGER.info("{}", testsList.keySet().stream().map(key -> key + ":" + testsList.get(key)).collect(Collectors.joining("\n")));
        LOGGER.info("Instrument version before commit...");
        Main.run(configuration.pathToFirstVersion, testsList);
        LOGGER.info("Instrument version after commit...");
        Main.run(configuration.pathToSecondVersion, testsList);
    }

    private static void run(final String rootPathFolder, final Map<String, List<String>> testsList) {
        LOGGER.info("Run on {}", rootPathFolder);
        Launcher launcher = new Launcher();
        launcher.addInputResource(rootPathFolder + "/" + TEST_FOLDER_PATH);

        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setNoClasspath(true);

        launcher.addProcessor(new JJoulesProcessor(testsList));
        launcher.getEnvironment().setOutputType(OutputType.CLASSES);
        launcher.getEnvironment().setSourceOutputDirectory(new File(rootPathFolder + "/" + TEST_FOLDER_PATH));
        launcher.run();
    }

}
