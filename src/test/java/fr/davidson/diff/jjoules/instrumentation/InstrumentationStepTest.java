package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.AbstractStepCleanTest;
import fr.davidson.diff.jjoules.util.Utils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class InstrumentationStepTest extends AbstractStepCleanTest {

    @Test
    void test() throws Exception {
        /*
            After the Instrumentation Step, the test class should contains method calls to TLPCSensor
         */
        final InstrumentationStep instrumentationStep = new InstrumentationStep();
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH))) {
            final String content = reader.lines().collect(Collectors.joining("\n"));
            assertFalse(content.contains("TLPCSensor"), content);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH))) {
            final String content = reader.lines().collect(Collectors.joining("\n"));
            assertFalse(content.contains("TLPCSensor"), content);
        }
        instrumentationStep._run(this.getConfiguration());
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + TEST_PATH + TEST_CLASS_PATH))) {
            final String content = reader.lines().collect(Collectors.joining("\n"));
            assertTrue(content.contains("TLPCSensor"), content);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + TEST_PATH + TEST_CLASS_PATH))) {
            final String content = reader.lines().collect(Collectors.joining("\n"));
            assertTrue(content.contains("TLPCSensor"), content);
        }
    }
}
