package fr.davidson.diff.jjoules;


import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import org.junit.jupiter.api.Test;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class MainTest {

    @Test
    @ExpectSystemExitWithStatus(0)
    public void testPrintUsage() {
        Main.main(new String[]{"--help"});
    }

    @Test
    @ExpectSystemExitWithStatus(0)
    public void testPrintVersion() {
        Main.main(new String[]{"--version"});
    }

    @Test
    @ExpectSystemExitWithStatus(-1)
    public void testPrintUsageInCaseOfError() {
        Main.main(new String[]{"--this-is-not-a-correct-flag"});
    }
}
