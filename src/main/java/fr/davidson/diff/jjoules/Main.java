package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.configuration.Configuration;
import fr.davidson.diff.jjoules.configuration.Options;
import fr.davidson.diff.jjoules.maven.JJoulesInjection;
import fr.davidson.diff.jjoules.process.AbstractJJoulesProcessor;
import fr.davidson.diff.jjoules.util.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.sniper.SniperJavaPrettyPrinter;

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
        if (configuration == null) {
            return;
        }
        final Map<String, List<String>> testsList = CSVReader.readFile(configuration.pathToTestListAsCSV);
        LOGGER.info("{}", testsList.keySet().stream().map(key -> key + ":" + testsList.get(key)).collect(Collectors.joining("\n")));
        final AbstractJJoulesProcessor processor = configuration.junit4 ?
                new fr.davidson.diff.jjoules.process.junit4.JJoulesProcessor(testsList, configuration.pathToFirstVersion) :
                new fr.davidson.diff.jjoules.process.junit5.JJoulesProcessor(testsList, configuration.pathToFirstVersion);
        LOGGER.info("Instrument version before commit...");
        Main.run(configuration.pathToFirstVersion, processor, configuration.classpath, testsList);
        Main.inject(configuration.pathToFirstVersion);
        processor.setRootPathFolder(configuration.pathToSecondVersion);
        LOGGER.info("Instrument version after commit...");
        Main.run(configuration.pathToSecondVersion, processor, configuration.classpath, testsList);
        Main.inject(configuration.pathToSecondVersion);
    }

    private static void run(final String rootPathFolder, AbstractProcessor<CtMethod<?>> processor, String[] classpath, Map<String, List<String>> testsList) {
        LOGGER.info("Run on {}", rootPathFolder);
        Launcher launcher = new Launcher();

        final String[] finalClassPath = new String[classpath.length + 2];
        finalClassPath[0] = rootPathFolder + "/target/classes";
        finalClassPath[1] = rootPathFolder + "/target/test-classes";
        System.arraycopy(classpath, 0, finalClassPath, 2, classpath.length);
        launcher.getEnvironment().setSourceClasspath(finalClassPath);
        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setAutoImports(false);
        launcher.getEnvironment().setLevel("DEBUG");
        //final ChangeCollector changeCollector = new ChangeCollector();
        //changeCollector.attachTo(launcher.getEnvironment());
        //launcher.getEnvironment().setPrettyPrinterCreator(() ->
        //        new SniperJavaPrettyPrinter(launcher.getEnvironment())
        //);
        launcher.addInputResource(rootPathFolder + "/" + TEST_FOLDER_PATH);

        launcher.addProcessor(processor);
        launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
        try {
            launcher.buildModel();
            launcher.process();
        } catch (SpoonException sp) {
            sp.printStackTrace();
        }
    }

    private static void inject(final String rootPathFolder) {
        new JJoulesInjection(rootPathFolder).inject();
    }

}
