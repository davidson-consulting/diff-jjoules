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

    public final boolean junit4;

    public Configuration(String pathToFirstVersion, String pathToSecondVersion, String pathToTestListAsCSV, boolean junit4) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV;
        this.junit4 = junit4;
    }
}
