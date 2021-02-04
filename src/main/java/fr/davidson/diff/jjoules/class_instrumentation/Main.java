package fr.davidson.diff.jjoules.class_instrumentation;

import fr.davidson.diff.jjoules.class_instrumentation.configuration.Configuration;
import fr.davidson.diff.jjoules.class_instrumentation.configuration.Options;
import fr.davidson.diff.jjoules.class_instrumentation.duplications.DuplicationManager;
import fr.davidson.diff.jjoules.class_instrumentation.duplications.XMLReader;
import fr.davidson.diff.jjoules.class_instrumentation.maven.JJoulesInjection;
import fr.davidson.diff.jjoules.class_instrumentation.process.JJoulesProcessor;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.HashMap;
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
        LOGGER.info("{}", configuration.toString());
        final Map<String, List<String>> testsList = CSVReader.readFile(configuration.pathToTestListAsCSV);
        LOGGER.info("{}", testsList.keySet().stream().map(key -> key + ":" + testsList.get(key)).collect(Collectors.joining("\n")));
        final Map<String, Integer> numberOfDuplicationRequired;
        if (configuration.nbDuplication == -1) {
            final DuplicationManager duplicationManager = new DuplicationManager(configuration.timeOfExecutionToReachInMs);
            if (!new File(configuration.pathToFirstVersion + "/target/surefire-reports/").exists()) {
                LOGGER.warn("You specified -1 to compute dynamically the number of duplication.");
                LOGGER.warn("However, the folder {} does not exists.", configuration.pathToFirstVersion + "/target/surefire-reports/");
                LOGGER.warn("Using default value (10) for number of duplication");
                numberOfDuplicationRequired = getDefaultNBDuplication(configuration, testsList);
            } else {
                final Map<String, Double> timePerTest = XMLReader.readAllXML(configuration.pathToFirstVersion + "/target/surefire-reports/");
                numberOfDuplicationRequired = duplicationManager.computeNumberOfDuplicationRequired(testsList, timePerTest);
                JSONUtils.write(configuration.pathToFirstVersion + "/duplications.json", numberOfDuplicationRequired);
            }
        } else {
            numberOfDuplicationRequired = getDefaultNBDuplication(configuration, testsList);
        }
        final JJoulesProcessor processor = new JJoulesProcessor(
                numberOfDuplicationRequired,
                testsList,
                configuration.pathToFirstVersion,
                configuration.numberOfMethodToProcess
        );
        LOGGER.info("Instrument version before commit...");
        Main.run(configuration.pathToFirstVersion, processor, configuration.classpathV1, testsList);
        Main.inject(configuration.pathToFirstVersion, configuration.shouldRandomize);
        processor.setRootPathFolder(configuration.pathToSecondVersion);
        processor.resetNumberOfTestMethodProcessed();
        LOGGER.info("Instrument version after commit...");
        Main.run(configuration.pathToSecondVersion, processor, configuration.classpathV2, testsList);
        Main.inject(configuration.pathToSecondVersion, configuration.shouldRandomize);
    }

    @NotNull
    private static Map<String, Integer> getDefaultNBDuplication(Configuration configuration, Map<String, List<String>> testsList) {
        final Map<String, Integer> numberOfDuplicationRequired;
        numberOfDuplicationRequired = new HashMap<>();
        for (String testClassName : testsList.keySet()) {
            for (String testMethodName : testsList.get(testClassName)) {
                final String testName = testClassName + "#" + testMethodName;
                numberOfDuplicationRequired.put(testName, 10);
            }
        }
        return numberOfDuplicationRequired;
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

    private static void inject(final String rootPathFolder, boolean randomize) {
        new JJoulesInjection(rootPathFolder, randomize).inject();
    }

}
