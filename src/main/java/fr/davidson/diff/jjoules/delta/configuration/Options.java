package fr.davidson.diff.jjoules.delta.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.instrumentation.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Options {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final JSAP OPTIONS = initJSAP();

    public static void usage() {
        LOGGER.error(OPTIONS.getUsage());
        LOGGER.error("");
        LOGGER.error(OPTIONS.getHelp());
    }

    public static Configuration parse(String[] args) {
        final JSAPResult parse = OPTIONS.parse(args);
        if (parse.getBoolean("help")) {
            usage();
            return null;
        }
        return new Configuration(
                parse.getString("path-dir-first-version"),
                parse.getString("path-dir-second-version"),
                parse.getString("tests-list"),
                parse.getString("classpath-v1"),
                parse.getString("classpath-v2"),
                parse.getInt("iterations"),
                parse.getString("output")
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

        FlaggedOption pathToDiff = new FlaggedOption("tests-list");
        pathToDiff.setRequired(false);
        pathToDiff.setLongFlag("tests-list");
        pathToDiff.setShortFlag('l');
        pathToDiff.setHelp("[Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.");
        pathToDiff.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption classpath = new FlaggedOption("classpath-v1");
        classpath.setRequired(true);
        classpath.setLongFlag("classpath-v1");
        classpath.setShortFlag('c');
        classpath.setHelp("[Mandatory] Specify the classpath to execute the tests. Should be a single string, separated by ':' (double-dot)");
        classpath.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption classpathV2 = new FlaggedOption("classpath-v2");
        classpathV2.setRequired(true);
        classpathV2.setLongFlag("classpath-v2");
        classpathV2.setShortFlag('b');
        classpathV2.setHelp("[Mandatory] Specify the classpath to execute the tests. Should be a single string, separated by ':' (double-dot)");
        classpathV2.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption report = new FlaggedOption("iterations");
        report.setRequired(false);
        report.setLongFlag("iterations");
        report.setShortFlag('i');
        report.setDefault("5");
        report.setHelp("TODO");
        report.setStringParser(JSAP.INTEGER_PARSER);

        FlaggedOption outputPath = new FlaggedOption("output");
        outputPath.setRequired(false);
        outputPath.setLongFlag("output");
        outputPath.setShortFlag('o');
        outputPath.setDefault("target/diff-jjoules");
        outputPath.setHelp("TODO");
        outputPath.setStringParser(JSAP.STRING_PARSER);

        Switch help = new Switch("help");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setDefault("false");
        help.setHelp("[Optional] display usage.");

        try {
            jsap.registerParameter(pathDirectoryFirstVersion);
            jsap.registerParameter(pathDirectorySecondVersion);
            jsap.registerParameter(report);
            jsap.registerParameter(outputPath);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }

        return jsap;
    }

}
