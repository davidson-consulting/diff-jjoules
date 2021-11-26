package fr.davidson.diff.jjoules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class AbstractStepCleanTest extends AbstractDiffJJoulesStepTest {

    @Override
    @BeforeEach
    protected void setUp() throws IOException {
        super.setUp();
        Files.copy(
                Paths.get(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH),
                Paths.get(TARGET_FOLDER_PATH_V1 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH),
                Paths.get(TARGET_FOLDER_PATH_V2 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V1 + "pom.xml"),
                Paths.get(TARGET_FOLDER_PATH_V1 + "pom.xml"),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V2 + "pom.xml"),
                Paths.get(TARGET_FOLDER_PATH_V2 + "pom.xml"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V1 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                Paths.get(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V2 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                Paths.get(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V1 + "pom.xml"),
                Paths.get(ROOT_PATH_V1 + "pom.xml"),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V2 + "pom.xml"),
                Paths.get(ROOT_PATH_V2 + "pom.xml"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

}
