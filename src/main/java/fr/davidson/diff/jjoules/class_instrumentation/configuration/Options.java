package fr.davidson.diff.jjoules.class_instrumentation.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.class_instrumentation.Main;
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
                parse.getString("classpath-v1").split(":"),
                parse.getString("classpath-v2").split(":"),
                parse.getInt("nb-duplication"),
                parse.getBoolean("randomize"),
                parse.getInt("exec-time-in-ms"));
        
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

        FlaggedOption nbDuplication = new FlaggedOption("nb-duplication");
        nbDuplication.setRequired(false);
        nbDuplication.setLongFlag("nb-duplication");
        nbDuplication.setDefault("9");
        nbDuplication.setHelp("TODO");
        nbDuplication.setStringParser(JSAP.INTEGER_PARSER);

        FlaggedOption execTime = new FlaggedOption("exec-time-in-ms");
        execTime.setRequired(false);
        execTime.setLongFlag("exec-time-in-ms");
        execTime.setDefault("2000");
        execTime.setHelp("TODO");
        execTime.setStringParser(JSAP.INTEGER_PARSER);

        Switch randomize = new Switch("randomize");
        randomize.setLongFlag("randomize");
        randomize.setDefault("false");
        randomize.setHelp("TODO");

        Switch help = new Switch("help");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setDefault("false");
        help.setHelp("[Optional] display usage.");

        try {
            jsap.registerParameter(pathDirectoryFirstVersion);
            jsap.registerParameter(pathDirectorySecondVersion);
            jsap.registerParameter(pathToDiff);
            jsap.registerParameter(classpath);
            jsap.registerParameter(classpathV2);
            jsap.registerParameter(nbDuplication);
            jsap.registerParameter(execTime);
            jsap.registerParameter(randomize);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }

        return jsap;
    }

}
