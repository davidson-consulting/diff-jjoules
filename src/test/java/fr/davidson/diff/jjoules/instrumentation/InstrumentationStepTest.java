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

         */
        final InstrumentationStep instrumentationStep = new InstrumentationStep();
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertFalse(reader.lines().collect(Collectors.joining("\n")).contains("@EnergyTest"));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertFalse(reader.lines().collect(Collectors.joining("\n")).contains("@EnergyTest"));
        }
        instrumentationStep.run(this.getConfiguration());
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V1 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertTrue(reader.lines().collect(Collectors.joining("\n")).contains("@EnergyTest"));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOT_PATH_V2 + Utils.TEST_FOLDER_PATH + TEST_CLASS_PATH))) {
            assertTrue(reader.lines().collect(Collectors.joining("\n")).contains("@EnergyTest"));
        }
    }
}
