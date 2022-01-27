package fr.davidson.diff.jjoules.utils.wrapper.properties;

import fr.davidson.diff.jjoules.util.Constants;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/11/2021
 */
public class AbstractPropertiesWrapperTest {

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
        try (final FileWriter write = new FileWriter("src/test/resources/v1/classpath", false)) {
            final String mavenHome = System.getProperty("user.home") + "/.m2/repository/";
            write.write(
                    Arrays.stream(CLASSPATH_ELEMENTS)
                            .map(classpathElement -> mavenHome + Constants.FILE_SEPARATOR + classpathElement)
                            .collect(Collectors.joining(Constants.PATH_SEPARATOR))
            );
        }
    }
}
