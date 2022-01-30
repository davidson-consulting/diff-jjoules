package fr.davidson.diff.jjoules.suspect;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 08/10/2021
 */
public class SuspectStepTest extends AbstractDiffJJoulesStepTest {

    @Test
    void test() {
        final Configuration configuration = this.getConfiguration();
        final ExecsLines execDeletions = JSONUtils.read("src/test/resources/json/exec_deletions.json", ExecsLines.class);
        final ExecsLines execAdditions = JSONUtils.read("src/test/resources/json/exec_additions.json", ExecsLines.class);
        configuration.setExecLinesDeletions(execDeletions);
        configuration.setExecLinesAdditions(execAdditions);

        configuration.setTestsList(
                new HashMap<String, Set<String>>() {
                    {
                        put(FULL_QUALIFIED_NAME_TEST_CLASS, new HashSet<>());
                        get(FULL_QUALIFIED_NAME_TEST_CLASS).addAll(
                                Arrays.asList("testCount", "testCountFailing")
                        );
                    }
                }
        );
        assertTrue(configuration.getScorePerLineV1().isEmpty());
        assertTrue(configuration.getScorePerLineV2().isEmpty());
        new SuspectStep()._run(configuration);
        assertTrue(configuration.getScorePerLineV1().isEmpty());
        assertFalse(configuration.getScorePerLineV2().isEmpty());
    }
}
