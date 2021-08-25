package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.failer.configuration.Configuration;
import fr.davidson.diff.jjoules.failer.configuration.Options;
import fr.davidson.diff.jjoules.failer.processor.MakeTestFailingProcessor;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.spoonlabs.flacoco.api.Suspiciousness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class Main {

    public static final String TEST_FOLDER_PATH = "src/test/java/";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        LOGGER.info("{}", configuration.toString());
        final Deltas deltas = JSONUtils.read(configuration.pathToDeltaJSON, Deltas.class);
        final Map<String, List<String>> testsToBeInstrumented = new HashMap<>();
        for (String fullTestMethodName : deltas.keySet()) {
            if (deltas.get(fullTestMethodName).instructions > 0) {
                final String[] split = fullTestMethodName.split("#");
                if (!testsToBeInstrumented.containsKey(split[0])) {
                    testsToBeInstrumented.put(split[0], new ArrayList<>());
                }
                testsToBeInstrumented.get(split[0]).add(split[1]);
            }
        }
        runVersion(
                configuration.pathToFirstVersion,
                configuration.classpathV1,
                new MakeTestFailingProcessor(testsToBeInstrumented, configuration.pathToFirstVersion)
        );
        runVersion(
                configuration.pathToSecondVersion,
                configuration.classpathV2,
                new MakeTestFailingProcessor(testsToBeInstrumented, configuration.pathToSecondVersion)
        );
    }

    private static void runVersion(
            String rootPathFolder,
            String[] classpath,
            MakeTestFailingProcessor processor
    ) {
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
        launcher.addInputResource(rootPathFolder + "/" + TEST_FOLDER_PATH);

        launcher.addProcessor(processor);
        launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setBinaryOutputDirectory(rootPathFolder + "/target/test-classes/");
        try {
            launcher.buildModel();
            launcher.process();
        } catch (SpoonException sp) {
            throw new RuntimeException(sp);
        }
    }

}
