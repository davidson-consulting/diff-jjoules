package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.mark.computation.CoverageComputation;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class CoverageComputationTest {

    @Test
    void test() {
        final String absolutePath = new File("src/test/resources/diff-jjoules-demo").getAbsolutePath();
        final String testClassName = "fr.davidson.diff_jjoules_demo.InternalListTest";
        final List<String> testMethodNames = Arrays.asList(
                "testMapEmptyList",
                "testMapOneElement",
                "testMapMultipleElement",
                "testCount",
                "testCount2"
        );
        final Coverage actual = CoverageComputation.computeCoverageForGivenVersionOfTests(
                new HashMap<String, List<String>>() {
                    {
                        put(testClassName, new ArrayList<>());
                        get(testClassName).addAll(testMethodNames);
                    }
                }, absolutePath
        );
        final Coverage expected = JSONUtils.read(absolutePath + "/coverage.json", Coverage.class);
        assertEquals(expected.toString(), actual.toString());
    }


}
