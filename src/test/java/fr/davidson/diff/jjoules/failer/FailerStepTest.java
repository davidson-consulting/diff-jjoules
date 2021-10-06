package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class FailerStepTest extends AbstractDiffJJoulesStepTest {


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
    }

    @Test
    void test() throws Exception {
        /*

         */
        final FailerStep failerStep = new FailerStep();
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertFalse(reader.lines().collect(Collectors.joining("\n")).contains("junit.framework.Assert.fail();"));
        }
        failerStep.run(this.getConfiguration());
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertEquals(6L, reader.lines().filter(line -> line.contains("junit.framework.Assert.fail();")).count());
        }
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
    }
}
