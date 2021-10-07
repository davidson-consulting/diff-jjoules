package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.failer.FailerStep;
import fr.davidson.diff.jjoules.instrumentation.process.AbstractJJoulesProcessor;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.JJoulesInjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class InstrumentationStep extends DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationStep.class);

    protected String getReportPathname() {
        return "instrumentation";
    }

    @Override
    public void run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Instrumentation");
        final Map<String, List<String>> testsList = configuration.getTestsList();
        final AbstractJJoulesProcessor processor = configuration.junit4 ?
                new fr.davidson.diff.jjoules.instrumentation.process.junit4.JJoulesProcessor(testsList, configuration.pathToFirstVersion) :
                new fr.davidson.diff.jjoules.instrumentation.process.junit5.JJoulesProcessor(testsList, configuration.pathToFirstVersion);
        LOGGER.info("Instrument version before commit...");
        this.instrument(configuration.pathToFirstVersion, processor, configuration.getClasspathV1(), testsList);
        this.inject(configuration.pathToFirstVersion);
        if (configuration.pathToSecondVersion != null && !configuration.pathToSecondVersion.isEmpty()) {
            processor.setRootPathFolder(configuration.pathToSecondVersion);
            LOGGER.info("Instrument version after commit...");
            this.instrument(configuration.pathToSecondVersion, processor, configuration.getClasspathV2(), testsList);
            this.inject(configuration.pathToSecondVersion);
        }
    }

    public void instrument(final String rootPathFolder, AbstractProcessor<CtMethod<?>> processor, String[] classpath, Map<String, List<String>> testsList) {
        LOGGER.info("Run on {}", rootPathFolder);
        Launcher launcher = new Launcher();

        final String[] finalClassPath = new String[classpath.length + 2];
        finalClassPath[0] = rootPathFolder + "/target/classes";
        finalClassPath[1] = rootPathFolder + "/target/test-classes";
        System.arraycopy(classpath, 0, finalClassPath, 2, classpath.length);
        launcher.getEnvironment().setSourceClasspath(finalClassPath);
//        launcher.getEnvironment().setNoClasspath(false);
//        launcher.getEnvironment().setAutoImports(false);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setLevel("DEBUG");
        launcher.addInputResource(rootPathFolder + "/" + Utils.TEST_FOLDER_PATH);

        launcher.addProcessor(processor);
        launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
        try {
            launcher.buildModel();
            launcher.process();
        } catch (SpoonException sp) {
            throw new RuntimeException(sp);
        }
    }

    private void inject(final String rootPathFolder) {
        new JJoulesInjection(rootPathFolder).inject();
    }

}
