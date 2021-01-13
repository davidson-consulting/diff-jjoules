package fr.davidson.diff.jjoules.localization.configuration;

import com.martiansoftware.jsap.*;
import fr.davidson.diff.jjoules.instrumentation.Main;
import fr.davidson.diff.jjoules.localization.output.Report;
import fr.davidson.diff.jjoules.localization.output.ReportEnum;
import fr.davidson.diff.jjoules.localization.select.SelectorEnum;
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
                parse.getString("path-data-json-first-version"),
                parse.getString("path-data-json-second-version"),
                parse.getString("tests-list"),
                parse.getString("path-to-diff"),
                ReportEnum.fromReportEnumValue(parse.getString("report"), parse.getString("output-path")),
                SelectorEnum.fromSelectorEnumValue(parse.getString("selector"))
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

        FlaggedOption testsList = new FlaggedOption("tests-list");
        testsList.setRequired(false);
        testsList.setLongFlag("tests-list");
        testsList.setShortFlag('l');
        testsList.setHelp("[Optional] Specify the path to a CSV file that contains the list of tests to be instrumented.");
        testsList.setStringParser(JSAP.STRING_PARSER);

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

        FlaggedOption report = new FlaggedOption("report");
        report.setRequired(false);
        report.setLongFlag("report");
        report.setShortFlag('r');
        report.setDefault("JSON");
        report.setHelp("TODO");
        report.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption selector = new FlaggedOption("selector");
        selector.setRequired(false);
        selector.setLongFlag("selector");
        selector.setShortFlag('z');
        selector.setDefault("LargestImpact");
        selector.setHelp("TODO");
        selector.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption outputPath = new FlaggedOption("output-path");
        outputPath.setRequired(false);
        outputPath.setLongFlag("output-path");
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
            jsap.registerParameter(testsList);
            jsap.registerParameter(pathJSONDataFirstVersion);
            jsap.registerParameter(pathJSONDataSecondVersion);
            jsap.registerParameter(testsList);
            jsap.registerParameter(pathToDiff);
            jsap.registerParameter(report);
            jsap.registerParameter(selector);
            jsap.registerParameter(outputPath);
            jsap.registerParameter(help);
        } catch (JSAPException e) {
            e.printStackTrace();
            usage();
        }

        return jsap;
    }

}
