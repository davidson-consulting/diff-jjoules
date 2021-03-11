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

    public final String[] classpath;

    public final String[] classpathV1;

    public final String[] classpathV2;

    public final boolean junit4;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String pathToTestListAsCSV,
                         String[] classpath,
                         String[] classpathV2,
                         boolean junit4) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV == null || pathToTestListAsCSV.isEmpty() ? "" : new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.junit4 = junit4;
        this.classpath = classpath;
        this.classpathV1 = classpath;
        this.classpathV2 = classpathV2;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", pathToTestListAsCSV='" + pathToTestListAsCSV + '\'' +
                ", classpath=" + Arrays.toString(classpath) +
                ", junit4=" + junit4 +
                '}';
    }
}
