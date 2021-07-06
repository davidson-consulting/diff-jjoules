package fr.davidson.diff.jjoules.markdown.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 14/06/2021
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(eu.stamp_project.diff_test_selection.configuration.Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToJSONDelta;

    public final String pathToJSONDeltaOmega;

    public final String pathToJSONDataV1;

    public final String pathToJSONDataV2;

    public Configuration(
            String pathToJSONDeltaOmega,
            String pathToJSONDelta,
            String pathToJSONDataV1,
            String pathToJSONDataV2) {
        this.pathToJSONDeltaOmega = pathToJSONDeltaOmega;
        this.pathToJSONDelta = pathToJSONDelta;
        this.pathToJSONDataV1 = pathToJSONDataV1;
        this.pathToJSONDataV2 = pathToJSONDataV2;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToDeltaOmega='" + pathToJSONDeltaOmega + '\'' +
                ", pathToDeltaJSON='" + pathToJSONDelta + '\'' +
                '}';
    }
}
