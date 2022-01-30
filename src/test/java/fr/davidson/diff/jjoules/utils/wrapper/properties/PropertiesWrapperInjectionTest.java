package fr.davidson.diff.jjoules.utils.wrapper.properties;

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
 * on 26/11/2021
 */
public class PropertiesWrapperInjectionTest extends AbstractPropertiesWrapperTest {

    @BeforeEach
    void setUp() throws IOException {
        Files.copy(
                Paths.get("src/test/resources/v1/classpath"),
                Paths.get("src/test/resources/v1/classpath.backup"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.copy(
                Paths.get("src/test/resources/v1/classpath.backup"),
                Paths.get("src/test/resources/v1/classpath"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Test
    void testInjection() {
        final Wrapper wrapper = WrapperEnum.PROPERTIES.getWrapper();
        try (final BufferedReader reader = new BufferedReader(new FileReader(("src/test/resources/v1/classpath")))) {
            assertFalse(reader.lines().anyMatch(line -> line.contains("junit-jjoules")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wrapper.injectDependencies("src/test/resources/v1/");
        try (final BufferedReader reader = new BufferedReader(new FileReader(("src/test/resources/v1/classpath")))) {
            assertTrue(reader.lines().anyMatch(line -> line.contains("junit-jjoules")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
