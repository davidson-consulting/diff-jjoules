package fr.davidson.diff.jjoules.suspect;

import fr.davidson.diff.jjoules.suspect.fl.FlacocoRunner;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class FlacocoRunnerTest {

    @Test
    void test() {
        final String absolutePath = new File("src/test/resources/diff-jjoules-demo").getAbsolutePath();
        MavenRunner.runCleanAndCompile(absolutePath);
        final String classpath = Utils.readClasspathFile(absolutePath + "/classpath");
        final String testClassName = "fr.davidson.diff_jjoules_demo.InternalListTest";
        final List<String> testMethodNames = Arrays.asList(
                "testMapEmptyList",
                "testMapOneElement",
                "testMapMultipleElement",
                "testCount",
                "testCount2",
                "testCount2Failing"
        );
        final Set<String> testMethodsToRun = new HashSet<>();
        for (String testMethodName : testMethodNames) {
            testMethodsToRun.add(testClassName + "#" + testMethodName);
        }
        final Map<Location, Suspiciousness> suspiciousnessMap = FlacocoRunner.run(
                false,
                classpath,
                absolutePath,
                testMethodsToRun
        );
        assertFalse(suspiciousnessMap.isEmpty());
    }
}
