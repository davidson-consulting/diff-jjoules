package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.failer.processor.MakeTestFailingProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;

import java.util.*;

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
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Failer");
        final Deltas deltas = this.configuration.getDeltas();
        final Map<String, Set<String>> testsToBeInstrumented = new HashMap<>();
        for (String fullTestMethodName : deltas.keySet()) {
            if (deltas.get(fullTestMethodName).instructions > 0) {
                final String[] split = fullTestMethodName.split("#");
                if (!testsToBeInstrumented.containsKey(split[0])) {
                    testsToBeInstrumented.put(split[0], new HashSet<>());
                }
                testsToBeInstrumented.get(split[0]).add(split[1]);
            }
        }
        makeFailVersion(
                this.configuration.getPathToFirstVersion(),
                this.configuration.getClasspathV1(),
                new MakeTestFailingProcessor(
                        testsToBeInstrumented,
                        this.configuration.getPathToFirstVersion(),
                        this.configuration.getWrapper().getPathToTestFolder()
                )
        );
        makeFailVersion(
                this.configuration.getPathToSecondVersion(),
                this.configuration.getClasspathV2(),
                new MakeTestFailingProcessor(
                        testsToBeInstrumented,
                        this.configuration.getPathToSecondVersion(),
                        this.configuration.getWrapper().getPathToTestFolder()
                )
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
        finalClassPath[0] = rootPathFolder + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToBinFolder();
        finalClassPath[1] = rootPathFolder + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToBinTestFolder();
        System.arraycopy(classpath, 0, finalClassPath, 2, classpath.length);
        launcher.getEnvironment().setSourceClasspath(finalClassPath);
        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setAutoImports(false);
        launcher.getEnvironment().setLevel("DEBUG");
        launcher.addInputResource(rootPathFolder + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToTestFolder());

        launcher.addProcessor(processor);
        launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setBinaryOutputDirectory(rootPathFolder + Constants.FILE_SEPARATOR + this.configuration.getWrapper().getPathToBinTestFolder());
        try {
            launcher.buildModel();
            launcher.process();
        } catch (SpoonException sp) {
            throw new RuntimeException(sp);
        }
    }

}
