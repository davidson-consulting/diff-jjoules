package fr.davidson.diff.jjoules.failer.configuration;

import java.io.File;
import java.util.Arrays;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class Configuration {

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String[] classpathV1;

    public final String[] classpathV2;

    public final String pathToDeltaJSON;

    public Configuration(
            String pathToFirstVersion,
            String pathToSecondVersion,
            String[] classpathV1,
            String[] classpathV2,
            String pathToDeltaJSON
    ) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.classpathV1 = classpathV1;
        this.classpathV2 = classpathV2;
        this.pathToDeltaJSON = pathToDeltaJSON;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", classpathV1=" + Arrays.toString(classpathV1) +
                ", classpathV2=" + Arrays.toString(classpathV2) +
                ", pathToDeltaJSON='" + pathToDeltaJSON + '\'' +
                '}';
    }
}
