package fr.davidson.diff.jjoules.mutation;

import fr.davidson.diff.jjoules.util.maven.JJoulesInjection;
import fr.davidson.diff.jjoules.mutation.configuration.Options;
import fr.davidson.diff.jjoules.mutation.configuration.Configuration;
import fr.davidson.diff.jjoules.mutation.process.UntareJjoulesProcessor;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/05/2021
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String MAIN_FOLDER_PATH = "src/main/java/";

    private static final String TEST_FOLDER_PATH = "src/test/java/";

    public static void main(String[] args) {
        final Configuration configuration = Options.parse(args);
        if (configuration == null) {
            return;
        }
        LOGGER.info("{}", configuration.toString());
        final Map<String, Set<String>> methodNamesPerFullQualifiedName = CSVFileManager.readFile(configuration.pathToMethodNames);
        LOGGER.info("{}", methodNamesPerFullQualifiedName.keySet()
                .stream()
                .map(key ->
                        key + ":" + methodNamesPerFullQualifiedName.get(key)
                ).collect(Collectors.joining("\n")));
        final UntareJjoulesProcessor processor = new UntareJjoulesProcessor(
                methodNamesPerFullQualifiedName,
                configuration.energyToConsume,
                configuration.rootPathDir + "/" + MAIN_FOLDER_PATH
        );
        Main.run(configuration.rootPathDir, processor, configuration.classpath);
        Main.inject(configuration.rootPathDir);
    }

    private static void run(
            final String rootPathFolder,
            AbstractProcessor<CtMethod<?>> processor,
            String[] classpath
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
        launcher.addInputResource(rootPathFolder + "/" + MAIN_FOLDER_PATH);

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
