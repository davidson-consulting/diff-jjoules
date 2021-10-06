package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.failer.processor.MakeTestFailingProcessor;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class FailerStep extends DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailerStep.class);

    protected String getReportPathname() {
        return "failer";
    }

    @Override
    public void run(Configuration configuration) {
        LOGGER.info("Run Failer - {}", configuration.toString());
        final Deltas deltas = configuration.getDeltas();
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
        makeFailVersion(
                configuration.pathToFirstVersion,
                configuration.getClasspathV1(),
                new MakeTestFailingProcessor(testsToBeInstrumented, configuration.pathToFirstVersion)
        );
        makeFailVersion(
                configuration.pathToSecondVersion,
                configuration.getClasspathV2(),
                new MakeTestFailingProcessor(testsToBeInstrumented, configuration.pathToSecondVersion)
        );
    }

    private void makeFailVersion(
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
        launcher.addInputResource(rootPathFolder + "/" + Utils.TEST_FOLDER_PATH);

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
