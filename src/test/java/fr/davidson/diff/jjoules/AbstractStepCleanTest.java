package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
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
                Paths.get(ROOT_PATH_V1 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH),
                Paths.get(TARGET_FOLDER_PATH_V1 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH),
                Paths.get(TARGET_FOLDER_PATH_V2 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V1 + MavenRunner.POM_XML),
                Paths.get(TARGET_FOLDER_PATH_V1 + MavenRunner.POM_XML),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V2 + MavenRunner.POM_XML),
                Paths.get(TARGET_FOLDER_PATH_V2 + MavenRunner.POM_XML),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V1 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                Paths.get(ROOT_PATH_V1 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V2 + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION),
                Paths.get(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V1 + MavenRunner.POM_XML),
                Paths.get(ROOT_PATH_V1 + MavenRunner.POM_XML),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TARGET_FOLDER_PATH_V2 + MavenRunner.POM_XML),
                Paths.get(ROOT_PATH_V2 + MavenRunner.POM_XML),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

}
