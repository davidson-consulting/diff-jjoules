package fr.davidson.diff.jjoules.instrumentation.configuration;

import java.io.File;
import java.util.Arrays;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class Configuration {

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToTestListAsCSV;

    public final String[] classpathV1;

    public final String[] classpathV2;

    public final boolean junit4;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String pathToTestListAsCSV,
                         String[] classpathV1,
                         String[] classpathV2,
                         boolean junit4) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV == null || pathToTestListAsCSV.isEmpty() ? "" : new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.junit4 = junit4;
        this.classpathV1 = classpathV1;
        this.classpathV2 = classpathV2;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", pathToTestListAsCSV='" + pathToTestListAsCSV + '\'' +
                ", classpathV1=" + Arrays.toString(classpathV1) +
                ", classpathV2=" + Arrays.toString(classpathV2) +
                ", junit4=" + junit4 +
                '}';
    }
}
