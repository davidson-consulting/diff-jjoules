package fr.davidson.diff.jjoules.mutation.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.mutation.Main;
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
                parse.getString("root-path-dir"),
                parse.getString("method-names-per-full-qualified-names"),
                parse.getString("classpath").split(":"),
                parse.getLong("energy-to-consume")
        );
        
    }

    private static JSAP initJSAP() {
        JSAP jsap = new JSAP();

        FlaggedOption rootPathDir = new FlaggedOption("root-path-dir");
        rootPathDir.setLongFlag("root-path-dir");
        rootPathDir.setShortFlag('d');
        rootPathDir.setRequired(true);
        rootPathDir.setHelp("[Mandatory] Specify the path to root directory of the project.");
        rootPathDir.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption testsList = new FlaggedOption("method-names-per-full-qualified-names");
        testsList.setRequired(false);
        testsList.setLongFlag("method-names-per-full-qualified-names");
        testsList.setShortFlag('n');
        testsList.setHelp("[Mandatory] Specify the path to a CSV file that contains the list of methods names per full qualified names to be mutated.");
        testsList.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption classpath = new FlaggedOption("classpath");
        classpath.setRequired(true);
        classpath.setLongFlag("classpath");
        classpath.setShortFlag('c');
        classpath.setHelp("[Mandatory] Specify the classpath to execute the tests. Should be a single string, separated by ':' (double-dot)");
        classpath.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption energyToConsume = new FlaggedOption("energy-to-consume");
        energyToConsume.setLongFlag("energy-to-consume");
        energyToConsume.setShortFlag('e');
        energyToConsume.setDefault("10000");
        energyToConsume.setHelp("Specify the amount of energy to be consumed by the mutation.");
        energyToConsume.setStringParser(JSAP.LONG_PARSER);


        Switch help = new Switch("help");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setDefault("false");
        help.setHelp("[Optional] display usage.");

        try {
            jsap.registerParameter(rootPathDir);
            jsap.registerParameter(testsList);
            jsap.registerParameter(classpath);
            jsap.registerParameter(energyToConsume);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }

        return jsap;
    }

}
