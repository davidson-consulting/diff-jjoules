package fr.davidson.diff.jjoules.mark.configuration;

import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 14/06/2021
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(eu.stamp_project.diff_test_selection.configuration.Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToJSONData;

    public final String diff;

    public final String pathToTestListAsCSV;

    public Configuration(
            String pathToFirstVersion,
            String pathToSecondVersion,
            String pathToJSONData,
            String pathToDiff,
            String pathToTestListAsCSV
    ) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToJSONData = pathToJSONData;
        if (pathToDiff == null || pathToDiff.isEmpty()) {
            LOGGER.warn("No path to diff file has been specified.");
            LOGGER.warn("I'll compute a diff file using the UNIX diff command");
            LOGGER.warn("You may encounter troubles.");
            LOGGER.warn("If so, please specify a path to a correct diff file");
            LOGGER.warn("or implement a new way to compute a diff file.");
            this.diff = new DiffComputer()
                    .computeDiffWithDiffCommand(
                            new File(pathToFirstVersion + "/" + SRC_FOLDER),
                            new File(pathToSecondVersion + "/" + SRC_FOLDER)
                    );
        } else {
            this.diff = Utils.readFile(pathToDiff);
        }
        this.pathToTestListAsCSV = pathToTestListAsCSV;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", pathToJSONData='" + pathToJSONData + '\'' +
                ", diff='" + diff + '\'' +
                ", pathToTestListAsCSV='" + pathToTestListAsCSV + '\'' +
                '}';
    }
}
