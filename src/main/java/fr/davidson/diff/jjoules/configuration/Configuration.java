package fr.davidson.diff.jjoules.configuration;

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

    public final boolean junit4;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String pathToTestListAsCSV,
                         String[] classpath,
                         boolean junit4) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.junit4 = junit4;
        this.classpath = classpath;
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
