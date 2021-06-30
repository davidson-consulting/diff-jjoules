package fr.davidson.diff.jjoules.delta.configuration;

import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Configuration {

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToTestListAsCSV;

    public final String classpathV1;

    public final String classpathV2;

    public final int iterations;

    public final String output;

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String pathToTestListAsCSV,
                         String classpathV1,
                         String classpathV2,
                         int iterations,
                         String output) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV == null || pathToTestListAsCSV.isEmpty() ? "" : new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.classpathV1 = classpathV1 + ":target/classes:target/test-classes";
        this.classpathV2 = classpathV2 + ":target/classes:target/test-classes";
        this.iterations = iterations;
        this.output = output;
        final File outputDirectory = new File(this.output);
        if (outputDirectory.exists()) {
            outputDirectory.delete();
        }
        outputDirectory.mkdir();
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", pathToTestListAsCSV='" + pathToTestListAsCSV + '\'' +
                ", classpathV1=" + classpathV1 +
                ", classpathV2=" + classpathV2 +
                ", iterations=" + iterations +
                ", output='" + output + '\'' +
                '}';
    }
}
