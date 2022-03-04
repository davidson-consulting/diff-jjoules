package fr.davidson.diff.jjoules.mutation;

import fr.davidson.diff.jjoules.mutation.processor.DiffJJoulesMutationProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 18/02/2022
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Configuration configuration = parse(args);
        run(configuration);
    }

    public static void run(Configuration configuration) {
        LOGGER.info("Running diff-jjoules-mutation with {}", configuration.toString());
        final DiffJJoulesMutationProcessor processor = new DiffJJoulesMutationProcessor(configuration.getMethodList(), configuration.getConsumption());
        Launcher launcher = new Launcher();
        final String[] classpath = configuration.getWrapper().buildClasspath(configuration.getRootPathFolder()).split(Constants.PATH_SEPARATOR);
        final String[] finalClassPath = new String[classpath.length + 2];
        finalClassPath[0] = Constants.joinFiles(configuration.getRootPathFolder(), configuration.getWrapper().getPathToBinFolder());
        finalClassPath[1] = Constants.joinFiles(configuration.getRootPathFolder(), configuration.getWrapper().getPathToBinTestFolder());
        System.arraycopy(classpath, 0, finalClassPath, 2, classpath.length);
        launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));
        launcher.getEnvironment().setSourceClasspath(finalClassPath);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(Constants.joinFiles(configuration.getRootPathFolder(), configuration.getSrcPathFolder()));
        launcher.addProcessor(processor);
        launcher.getEnvironment().setSourceOutputDirectory(new File(Constants.joinFiles(configuration.getRootPathFolder(), configuration.getSrcPathFolder())));
        launcher.getEnvironment().setOutputType(OutputType.CLASSES);
        try {
            launcher.run();
        } catch (SpoonException sp) {
            throw new RuntimeException(sp);
        }
        LOGGER.info("Injecting TLPC-sensor dependency to {}", configuration.getRootPathFolder());
        configuration.getWrapper().injectDependencies(configuration.getRootPathFolder());
    }

    public static Configuration parse(String[] args) {
        Configuration configuration = new Configuration();
        final CommandLine commandLine = new CommandLine(configuration);
        commandLine.setUsageHelpWidth(120);
        try {
            commandLine.parseArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            commandLine.usage(System.err);
            System.exit(-1);
        }
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            System.exit(0);
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            System.exit(0);
        }
        configuration.init();
        return configuration;
    }

}
