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
public class PropertiesWrapperTest extends AbstractPropertiesWrapperTest {

    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
        try {
            Files.walk(Paths.get("src/test/resources/diff-jjoules-demo/target"))
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (NoSuchFileException ignored) {
            // ignored
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
