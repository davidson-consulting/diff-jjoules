package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import fr.davidson.diff.jjoules.util.Constants;
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

        new File(ROOT_PATH_V1 + InstrumentationProcessor.FOLDER_MEASURES_PATH).mkdir();
        new File(ROOT_PATH_V2 + InstrumentationProcessor.FOLDER_MEASURES_PATH).mkdir();

        Files.copy(
                Paths.get(Constants.joinFiles(TEST_RESOURCES_JSON_PATH, "v1", InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
                Paths.get(Constants.joinFiles(ROOT_PATH_V1, InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(Constants.joinFiles(TEST_RESOURCES_JSON_PATH, "v2", InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
                Paths.get(Constants.joinFiles(ROOT_PATH_V2, InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
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
