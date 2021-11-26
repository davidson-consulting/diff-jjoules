package fr.davidson.diff.jjoules.utils.wrapper.maven;

import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class MavenWrapperInjectionTest {

    @BeforeEach
    void setUp() throws IOException {
        Files.copy(
                Paths.get("src/test/resources/diff-jjoules-demo/pom.xml"),
                Paths.get("src/test/resources/diff-jjoules-demo/pom.xml.backup"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.copy(
                Paths.get("src/test/resources/diff-jjoules-demo/pom.xml.backup"),
                Paths.get("src/test/resources/diff-jjoules-demo/pom.xml"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Test
    void testInjection() {
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        try (final BufferedReader reader = new BufferedReader(new FileReader(("src/test/resources/diff-jjoules-demo/pom.xml")))) {
            assertFalse(reader.lines().anyMatch(line -> line.contains("junit-jjoules")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wrapper.injectJJoulesDependencies("src/test/resources/diff-jjoules-demo/");
        try (final BufferedReader reader = new BufferedReader(new FileReader(("src/test/resources/diff-jjoules-demo/pom.xml")))) {
            assertTrue(reader.lines().anyMatch(line -> line.contains("junit-jjoules")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
