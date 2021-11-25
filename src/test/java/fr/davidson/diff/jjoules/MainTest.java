package fr.davidson.diff.jjoules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.junit.Assert.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class MainTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testPrintUsage() {
        exit.expectSystemExitWithStatus(0);
        Main.main(new String[]{"--help"});
        assertTrue(systemOutRule.getLog().startsWith("Usage: fr.davidson.diff.jjoules.Main "));
        assertTrue(systemErrRule.getLog().isEmpty());
    }

    @Test
    public void testPrintVersion() {
        exit.expectSystemExitWithStatus(0);
        Main.main(new String[]{"--version"});
        assertTrue(systemOutRule.getLog().startsWith("Usage: fr.davidson.diff.jjoules.Main "));
        assertTrue(systemErrRule.getLog().isEmpty());
    }

    @Test
    public void testPrintUsageInCaseOfError() {
        exit.expectSystemExitWithStatus(-1);
        Main.main(new String[]{"--this-is-not-a-correct-flag"});
        assertTrue(systemErrRule.getLog().startsWith("Usage: fr.davidson.diff.jjoules.Main "));
        assertTrue(systemOutRule.getLog().isEmpty());
    }
}
