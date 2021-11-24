package fr.davidson.diff.jjoules.utils.wrapper.properties;

import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class PropertiesWrapperTest {

    private final String[] CLASSPATH_ELEMENTS = new String[]{
            "org/junit/jupiter/junit-jupiter-api/5.5.2/junit-jupiter-api-5.5.2.jar",
            "org/apiguardian/apiguardian-api/1.1.0/apiguardian-api-1.1.0.jar",
            "org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar",
            "org/junit/platform/junit-platform-commons/1.5.2/junit-platform-commons-1.5.2.jar",
            "org/junit/jupiter/junit-jupiter-engine/5.5.2/junit-jupiter-engine-5.5.2.jar",
            "org/junit/platform/junit-platform-engine/1.5.2/junit-platform-engine-1.5.2.jar",
            "org/junit/platform/junit-platform-runner/1.3.2/junit-platform-runner-1.3.2.jar",
            "org/junit/platform/junit-platform-launcher/1.3.2/junit-platform-launcher-1.3.2.jar",
            "org/junit/platform/junit-platform-suite-api/1.3.2/junit-platform-suite-api-1.3.2.jar",
            "junit/junit/4.12/junit-4.12.jar", "org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
    };

    @BeforeEach
    void setUp() throws IOException {
        try {
            Files.walk(Paths.get("src/test/resources/diff-jjoules-demo/target"))
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (NoSuchFileException ignored) {
            // ignored
        }
        try (final FileWriter write = new FileWriter("src/test/resources/diff-jjoules-demo/classpath", false)) {
            final String mavenHome = System.getProperty("user.home") + "/.m2/repository/";
            write.write(
                    Arrays.stream(CLASSPATH_ELEMENTS)
                            .map(classpathElement -> mavenHome + Constants.FILE_SEPARATOR + classpathElement)
                            .collect(Collectors.joining(Constants.PATH_SEPARATOR))
            );
        }
    }

    @Test
    void testCleanAndCompile() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        wrapper.cleanAndCompile("src/test/resources/diff-jjoules-demo/");
        assertTrue(new File("src/test/resources/diff-jjoules-demo/target/classes/").exists());
        assertTrue(new File("src/test/resources/diff-jjoules-demo/target/test-classes/").exists());
    }

    @Test
    void testClean() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        wrapper.clean("src/test/resources/diff-jjoules-demo/");
        assertFalse(new File("src/test/resources/diff-jjoules-demo/target/classes/").exists());
        assertFalse(new File("src/test/resources/diff-jjoules-demo/target/test-classes/").exists());
    }

    @Test
    void testCompile() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        wrapper.compile("src/test/resources/diff-jjoules-demo/");
        assertTrue(new File("src/test/resources/diff-jjoules-demo/target").exists());
    }

    @Test
    void testBuildClasspath() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        String classpath = wrapper.buildClasspath("src/test/resources/diff-jjoules-demo/");
        assertTrue(classpath.contains("org/junit/jupiter/junit-jupiter-api/5.5.2/junit-jupiter-api-5.5.2.jar"));
        classpath = wrapper.buildClasspath("src/test/resources/diff-jjoules-demo");
        assertTrue(classpath.contains("org/junit/jupiter/junit-jupiter-api/5.5.2/junit-jupiter-api-5.5.2.jar"));
    }

    @Test
    void testConstants() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        assertEquals(
                "src" + Constants.FILE_SEPARATOR + "main" + Constants.FILE_SEPARATOR + "java" + Constants.FILE_SEPARATOR + "",
                wrapper.getPathToSrcFolder()
        );
        assertEquals(
                "src" + Constants.FILE_SEPARATOR + "test" + Constants.FILE_SEPARATOR + "java" + Constants.FILE_SEPARATOR + "",
                wrapper.getPathToTestFolder()
        );
        assertEquals(
                "target" + Constants.FILE_SEPARATOR + "classes" + Constants.FILE_SEPARATOR + "",
                wrapper.getPathToBinFolder()
        );
        assertEquals(
                "target" + Constants.FILE_SEPARATOR + "test-classes" + Constants.FILE_SEPARATOR + "",
                wrapper.getPathToBinTestFolder()
        );
        assertEquals(
                "target" + Constants.FILE_SEPARATOR + "classes" + Constants.FILE_SEPARATOR + Constants.PATH_SEPARATOR
                        + "target" + Constants.FILE_SEPARATOR + "test-classes" + Constants.FILE_SEPARATOR + "",
                wrapper.getBinaries()
        );
    }

}
