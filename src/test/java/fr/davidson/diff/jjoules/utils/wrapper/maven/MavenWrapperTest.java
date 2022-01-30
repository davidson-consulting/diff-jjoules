package fr.davidson.diff.jjoules.utils.wrapper.maven;

import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class MavenWrapperTest {

    @Test
    void testCleanAndCompile() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        wrapper.cleanAndCompile("src/test/resources/v1/");
        assertTrue(new File("src/test/resources/v1/target").exists());
    }

    @Test
    void testClean() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        wrapper.clean("src/test/resources/v1/");
        assertFalse(new File("src/test/resources/v1/target").exists());
    }

    @Test
    void testCompile() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        wrapper.compile("src/test/resources/v1/");
        assertTrue(new File("src/test/resources/v1/target").exists());
    }

    @Test
    void testBuildClasspath() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        String classpath = wrapper.buildClasspath("src/test/resources/v1/");
        assertTrue(classpath.contains("org/junit/jupiter/junit-jupiter-api"));
        classpath = wrapper.buildClasspath("src/test/resources/v1");
        assertTrue(classpath.contains("org/junit/jupiter/junit-jupiter-api"));
    }

    @Test
    void testConstants() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
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
