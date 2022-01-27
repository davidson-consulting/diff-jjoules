package fr.davidson.diff.jjoules.selection;

import eu.stamp_project.testrunner.EntryPoint;
import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 08/10/2021
 */
public class SelectionStepTest extends AbstractDiffJJoulesStepTest {

    @Test
    void test() {
        EntryPoint.timeoutInMs = 100000;
        final Configuration configuration = this.getConfiguration();
        configuration.getTestsList().clear();
        assertTrue(configuration.getTestsList().isEmpty());
        new SelectionStep()._run(configuration);
        assertEquals(2, configuration.getTestsList().get("fr.davidson.diff_jjoules_demo.InternalListTest").size());
    }
}
