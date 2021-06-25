package fr.davidson.diff.jjoules.mark.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.instrumentation.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 14/06/2021
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
                parse.getString("path-data-json"),
                parse.getString("path-to-diff"),
                parse.getString("tests-list")
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

        FlaggedOption pathJSONDataFirstVersion = new FlaggedOption("path-data-json-first-version");
        pathJSONDataFirstVersion.setRequired(false);
        pathJSONDataFirstVersion.setLongFlag("path-data-json-first-version");
        pathJSONDataFirstVersion.setShortFlag('f');
        pathJSONDataFirstVersion.setHelp("TODO");
        pathJSONDataFirstVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathJSONDataSecondVersion = new FlaggedOption("path-data-json-second-version");
        pathJSONDataSecondVersion.setRequired(false);
        pathJSONDataSecondVersion.setLongFlag("path-data-json-second-version");
        pathJSONDataSecondVersion.setShortFlag('s');
        pathJSONDataSecondVersion.setHelp("TODO");
        pathJSONDataSecondVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathToDiff = new FlaggedOption("path-to-diff");
        pathToDiff.setRequired(false);
        pathToDiff.setLongFlag("path-to-diff");
        pathToDiff.setShortFlag('d');
        pathToDiff.setDefault("");
        pathToDiff.setHelp("[Optional] Specify the path of a diff file. If it is not specified, it will be computed using diff command line.");
        pathToDiff.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption testsList = new FlaggedOption("tests-list");
        testsList.setRequired(false);
        testsList.setLongFlag("tests-list");
        testsList.setShortFlag('l');
        testsList.setHelp("[Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.");
        testsList.setStringParser(JSAP.STRING_PARSER);

        Switch help = new Switch("help");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setDefault("false");
        help.setHelp("[Optional] display usage.");

        try {
            jsap.registerParameter(pathDirectoryFirstVersion);
            jsap.registerParameter(pathDirectorySecondVersion);
            jsap.registerParameter(pathJSONDataFirstVersion);
            jsap.registerParameter(pathJSONDataSecondVersion);
            jsap.registerParameter(pathToDiff);
            jsap.registerParameter(testsList);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }
        return jsap;
    }


}
