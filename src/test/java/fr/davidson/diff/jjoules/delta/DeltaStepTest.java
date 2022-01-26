package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class DeltaStepTest extends AbstractDiffJJoulesStepTest {

    @Override
    @BeforeEach
    public void setUp() throws java.io.IOException {
        super.setUp();

    }

    @Test
    void test() throws IOException {
        /*
            The DeltaStep generates 4 new files and init 4 fields in the Configuration :
                - Data from V1
                - Data from V2
                - Deltas computed from the Data from V1 and the Data from V2
                - The list of methods to be considered
         */
        final Configuration configuration = this.getConfiguration();

        new File(ROOT_PATH_V1 + PATH_DIFF_JJOULES_MEASUREMENTS).mkdir();
        new File(ROOT_PATH_V2 + PATH_DIFF_JJOULES_MEASUREMENTS).mkdir();

        Files.copy(
                Paths.get(TEST_RESOURCES_JSON_PATH + "v1/" + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                Paths.get(ROOT_PATH_V1 + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(TEST_RESOURCES_JSON_PATH + "v2/" + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                Paths.get(ROOT_PATH_V2 + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                StandardCopyOption.REPLACE_EXISTING
        );

        configuration.setIterations(1);
        assertTrue(configuration.getDataV1().isEmpty());
        assertTrue(configuration.getDataV2().isEmpty());
        assertTrue(configuration.getDeltas().isEmpty());
        assertTrue(configuration.getConsideredTestsNames().isEmpty());
        new DeltaStep()._run(configuration);
        assertFalse(configuration.getDataV1().isEmpty());
        assertFalse(configuration.getDataV2().isEmpty());
        assertFalse(configuration.getDeltas().isEmpty());
        assertFalse(configuration.getConsideredTestsNames().isEmpty());
    }
}
