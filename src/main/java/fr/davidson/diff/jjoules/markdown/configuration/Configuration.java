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

    public final String pathToDeltaJSON;

    public final String pathToDeltaOmega;

    public Configuration(
            String pathToDeltaOmega,
            String pathToDeltaJSON) {
        this.pathToDeltaOmega = pathToDeltaOmega;
        this.pathToDeltaJSON = pathToDeltaJSON;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToDeltaOmega='" + pathToDeltaOmega + '\'' +
                ", pathToDeltaJSON='" + pathToDeltaJSON + '\'' +
                '}';
    }
}
