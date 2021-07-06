package fr.davidson.diff.jjoules.markdown.configuration;

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
                parse.getString("path-delta-omega"),
                parse.getString("path-delta-json"),
                parse.getString("path-data-json-first-version"),
                parse.getString("path-data-json-second-version")
        );
    }

    private static JSAP initJSAP() {
        JSAP jsap = new JSAP();

        FlaggedOption pathDirectoryFirstVersion = new FlaggedOption("path-delta-omega");
        pathDirectoryFirstVersion.setLongFlag("path-delta-omega");
        pathDirectoryFirstVersion.setRequired(true);
        pathDirectoryFirstVersion.setHelp("TODO");
        pathDirectoryFirstVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathDeltaJSON = new FlaggedOption("path-delta-json");
        pathDeltaJSON.setRequired(true);
        pathDeltaJSON.setLongFlag("path-delta-json");
        pathDeltaJSON.setHelp("TODO");
        pathDeltaJSON.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathJSONDataFirstVersion = new FlaggedOption("path-data-json-first-version");
        pathJSONDataFirstVersion.setRequired(true);
        pathJSONDataFirstVersion.setLongFlag("path-data-json-first-version");
        pathJSONDataFirstVersion.setHelp("TODO");
        pathJSONDataFirstVersion.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption pathJSONDataSecondVersion = new FlaggedOption("path-data-json-second-version");
        pathJSONDataSecondVersion.setRequired(true);
        pathJSONDataSecondVersion.setLongFlag("path-data-json-second-version");
        pathJSONDataSecondVersion.setHelp("TODO");
        pathJSONDataSecondVersion.setStringParser(JSAP.STRING_PARSER);

        Switch help = new Switch("help");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setDefault("false");
        help.setHelp("[Optional] display usage.");

        try {
            jsap.registerParameter(pathDirectoryFirstVersion);
            jsap.registerParameter(pathDeltaJSON);
            jsap.registerParameter(pathJSONDataFirstVersion);
            jsap.registerParameter(pathJSONDataSecondVersion);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }
        return jsap;
    }


}
