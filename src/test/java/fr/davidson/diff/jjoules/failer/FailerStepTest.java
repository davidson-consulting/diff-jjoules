package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.AbstractStepCleanTest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class FailerStepTest extends AbstractStepCleanTest {

    @Test
    void test() throws Exception {
        /*
            After applying the FailerStep, the test should contains 6 calls to junit.framework.Assert.fail();
         */
        final FailerStep failerStep = new FailerStep();
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH))) {
            assertFalse(reader.lines().collect(Collectors.joining("\n")).contains("junit.framework.Assert.fail();"));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH))) {
            assertFalse(reader.lines().collect(Collectors.joining("\n")).contains("junit.framework.Assert.fail();"));
        }
        failerStep._run(this.getConfiguration());
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH))) {
            assertEquals(6L, reader.lines().filter(line -> line.contains("junit.framework.Assert.fail();")).count());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH))) {
            assertEquals(6L, reader.lines().filter(line -> line.contains("junit.framework.Assert.fail();")).count());
        }
    }


}
