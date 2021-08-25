package fr.davidson.diff.jjoules;

import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToTestListAsCSV;

    public final String[] classpathV1;

    public final String[] classpathV2;

    public final boolean junit4;

    public final int iterations;

    public final String output;

    public final String pathToJSONDelta;

    public final String pathToJSONDataV1;

    public final String pathToJSONDataV2;

    public final String diff;

    public final String pathToDeltaJSON;

    public final String pathToJSONDeltaOmega;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String pathToTestListAsCSV,
                         String[] classpathV1,
                         String[] classpathV2,
                         boolean junit4,
                         int iterations,
                         String output,
                         String pathToJSONDelta,
                         String pathToJSONDataV1,
                         String pathToJSONDataV2,
                         String pathToDiff,
                         String pathToDeltaJSON,
                         String pathToJSONDeltaOmega
    ) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV == null || pathToTestListAsCSV.isEmpty() ? "" : new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.junit4 = junit4;
        this.classpathV1 = classpathV1;
        this.classpathV2 = classpathV2;
        this.iterations = iterations;
        this.output = output;
        final File outputDirectory = new File(this.output);
        if (outputDirectory.exists()) {
            outputDirectory.delete();
        }
        outputDirectory.mkdir();
        this.pathToJSONDelta = pathToJSONDelta;
        this.pathToJSONDataV1 = pathToJSONDataV1;
        this.pathToJSONDataV2 = pathToJSONDataV2;
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
        this.pathToDeltaJSON = pathToDeltaJSON;
        this.pathToJSONDeltaOmega = pathToJSONDeltaOmega;
    }

}
