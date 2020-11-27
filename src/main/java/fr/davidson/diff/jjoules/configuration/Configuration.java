package fr.davidson.diff.jjoules.configuration;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class Configuration {

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToTestListAsCSV;

    public Configuration(String pathToFirstVersion, String pathToSecondVersion, String pathToTestListAsCSV) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV;
    }
}
