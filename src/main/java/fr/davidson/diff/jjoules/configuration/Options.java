package fr.davidson.diff.jjoules.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class Options {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final JSAP OPTIONS = initJSAP();

    public static void usage() {
        LOGGER.error("");
        LOGGER.error("Usage: java -jar target/dspot-<version>-jar-with-dependencies.jar");
        LOGGER.error("                          " + OPTIONS.getUsage());
        LOGGER.error("");
        LOGGER.error(OPTIONS.getHelp());
        System.exit(1);
    }

    public static Configuration parse(String[] args) {
        final JSAPResult parse = OPTIONS.parse(args);
        return new Configuration(
                parse.getString("path-dir-first-version"),
                parse.getString("path-dir-second-version"),
                parse.getString("tests-list"),
                parse.getString("classpath").split(":"),
                parse.getBoolean("junit4")
        );
    }

    private static JSAP initJSAP() {
        JSAP jsap = new JSAP();

        FlaggedOption pathDirectoryFirstVersion = new FlaggedOption("path-dir-first-version");
        pathDirectoryFirstVersion.setLongFlag("path-dir-first-version");
        pathDirectoryFirstVersion.setShortFlag('p');
        pathDirectoryFirstVersion.setRequired(true);
        pathDirectoryFirstVersion.setHelp("[Mandatory] Specify the path to root directory of the project in the first version.");
        pathDirectoryFirstVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathDirectorySecondVersion = new FlaggedOption("path-dir-second-version");
        pathDirectorySecondVersion.setLongFlag("path-dir-second-version");
        pathDirectorySecondVersion.setShortFlag('q');
        pathDirectorySecondVersion.setRequired(true);
        pathDirectorySecondVersion.setHelp("[Mandatory] Specify the path to root directory of the project in the second version.");
        pathDirectorySecondVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption module = new FlaggedOption("module");
        module.setRequired(false);
        module.setLongFlag("module");
        module.setShortFlag('m');
        module.setDefault("");
        module.setHelp("[Optional] In case of multi-module project, specify which module (a path from the project's root).");
        module.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathToDiff = new FlaggedOption("tests-list");
        pathToDiff.setRequired(false);
        pathToDiff.setLongFlag("tests-list");
        pathToDiff.setShortFlag('l');
        pathToDiff.setHelp("[Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.");
        pathToDiff.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption classpath = new FlaggedOption("classpath");
        classpath.setRequired(false);
        classpath.setLongFlag("classpath");
        classpath.setShortFlag('c');
        classpath.setHelp("[Mandatory] Specify the classpath to execute the tests. Should be a single string, separated by ':' (double-dot)");
        classpath.setStringParser(JSAP.STRING_PARSER);

        Switch junit4 = new Switch("junit4");
        junit4.setLongFlag("junit4");
        junit4.setDefault("false");
        junit4.setHelp("[Optional] enable this flag for junit4 test suites");

        try {
            jsap.registerParameter(pathDirectoryFirstVersion);
            jsap.registerParameter(pathDirectorySecondVersion);
            jsap.registerParameter(module);
            jsap.registerParameter(pathToDiff);
            jsap.registerParameter(classpath);
            jsap.registerParameter(junit4);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }

        return jsap;
    }

}
