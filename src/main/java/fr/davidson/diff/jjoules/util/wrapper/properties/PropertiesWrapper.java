package fr.davidson.diff.jjoules.util.wrapper.properties;

import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import org.powerapi.jjoules.junit5.EnergyTest;
import spoon.Launcher;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class PropertiesWrapper implements Wrapper {

    public static final String PATH_SRC_KEY = "path.source";

    public static final String PATH_TEST_SRC_KEY = "path.test.source";

    public static final String PATH_BIN_KEY = "path.binary";

    public static final String PATH_TEST_BIN_KEY = "path.test.binary";

    public static final String PATH_TO_CLASSPATH_FILE_KEY = "path.classpath";

    public static final String PATH_PROPERTIES_FILE = "diff-jjoules.properties";

    private Properties properties;

    public PropertiesWrapper() {
        this.properties = new Properties();
        try (FileReader file = new FileReader(PATH_PROPERTIES_FILE)) {
            this.properties.load(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @Override
    public void clean(String pathToRootDir) {
        try {
            final File binFolderFd = new File(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinFolder());
            if (binFolderFd.exists()) {
                deleteDirectory(binFolderFd);
                deleteDirectory(new File(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinTestFolder()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void compile(String pathToRootDir) {
        final File binFolder = new File(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinFolder());
        if (!binFolder.exists()) {
            binFolder.mkdirs();
        }
        final File testBinFolder = new File(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinTestFolder());
        if (!testBinFolder.exists()) {
            testBinFolder.mkdirs();
        }
        Launcher launcher = new Launcher();
        launcher.addInputResource(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToSrcFolder());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setBinaryOutputDirectory(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinFolder());
        launcher.run();
        launcher = new Launcher();
        final String classpath = this.buildClasspath(pathToRootDir);
        final String[] classpathSplitted = classpath.split(Constants.PATH_SEPARATOR);
        final String[] finalClasspath = new String[classpathSplitted.length];
        System.arraycopy(classpathSplitted, 0, finalClasspath, 0, classpathSplitted.length);
        finalClasspath[finalClasspath.length - 1] = pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinFolder();
        launcher.getEnvironment().setSourceClasspath(finalClasspath);
        launcher.addInputResource(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToTestFolder());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setBinaryOutputDirectory(pathToRootDir + Constants.FILE_SEPARATOR + this.getPathToBinTestFolder());
        launcher.run();
    }

    @Override
    public void cleanAndCompile(String pathToRootDir) {
        this.clean(pathToRootDir);
        this.compile(pathToRootDir);
    }

    @Override
    public String buildClasspath(String pathToRootDir) {
        return Utils.readClasspathFile(pathToRootDir + Constants.FILE_SEPARATOR + this.properties.getProperty(PATH_TO_CLASSPATH_FILE_KEY));
    }

    @Override
    public String getPathToSrcFolder() {
        return this.properties.getProperty(PATH_SRC_KEY);
    }

    @Override
    public String getPathToTestFolder() {
        return this.properties.getProperty(PATH_TEST_SRC_KEY);
    }

    @Override
    public String getPathToBinFolder() {
        return this.properties.getProperty(PATH_BIN_KEY);
    }

    @Override
    public String getPathToBinTestFolder() {
        return this.properties.getProperty(PATH_TEST_BIN_KEY);
    }

    @Override
    public String getBinaries() {
        return this.getPathToBinFolder() + Constants.PATH_SEPARATOR + this.getPathToBinTestFolder();
    }

    @Override
    public void injectJJoulesDependencies(String pathToRootDir) {
        final String pathToClasspathFile = pathToRootDir + Constants.FILE_SEPARATOR + this.properties.getProperty(PATH_TO_CLASSPATH_FILE_KEY);
        try (final FileWriter writer = new FileWriter(pathToClasspathFile, true)) {
            final String pathToJUnitJJoulesJar = new File(
                    EnergyTest.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    ).getAbsolutePath();
            writer.append(Constants.PATH_SEPARATOR).append(pathToJUnitJJoulesJar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
