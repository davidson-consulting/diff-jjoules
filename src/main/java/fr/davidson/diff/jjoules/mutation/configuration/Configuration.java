package fr.davidson.diff.jjoules.mutation.configuration;

import java.io.File;
import java.util.Arrays;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class Configuration {

    public final String rootPathDir;

    public final String pathToMethodNames;

    public final String[] classpath;
    
    public final long energyToConsume;

    public Configuration(String rootPathDir,
                         String pathToMethodNames,
                         String[] classpath,
                         long energyToConsume) {
        this.rootPathDir = rootPathDir;
        this.pathToMethodNames = pathToMethodNames == null || pathToMethodNames.isEmpty() ? "" : new File(pathToMethodNames).isAbsolute() ? pathToMethodNames : this.rootPathDir + "/" + pathToMethodNames;
        this.classpath = classpath;
        this.energyToConsume = energyToConsume;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "rootPathDir='" + rootPathDir + '\'' +
                ", pathToMethodNames='" + pathToMethodNames + '\'' +
                ", classpath=" + Arrays.toString(classpath) +
                '}';
    }
}
