package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class DeltaStepTest extends AbstractDiffJJoulesStepTest {



    @Test
    void test() {
        /*
            The DeltaStep generates 4 new files and init 4 fields in the Configuration :
                - Data from V1
                - Data from V2
                - Deltas computed from the Data from V1 and the Data from V2
                - The list of methods to be considered
         */
        final Configuration configuration = this.getConfiguration();
        assertTrue(configuration.getDataV1().isEmpty());
        assertTrue(configuration.getDataV2().isEmpty());
        assertTrue(configuration.getDeltas().isEmpty());
        assertTrue(configuration.getConsideredTestsNames().isEmpty());
        new DeltaStep().run(configuration);
        assertFalse(configuration.getDataV1().isEmpty());
        assertFalse(configuration.getDataV2().isEmpty());
        assertFalse(configuration.getDeltas().isEmpty());
        assertFalse(configuration.getConsideredTestsNames().isEmpty());
    }
}
