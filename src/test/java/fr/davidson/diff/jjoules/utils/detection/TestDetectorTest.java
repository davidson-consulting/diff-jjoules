package fr.davidson.diff.jjoules.utils.detection;

import fr.davidson.diff.jjoules.util.coverage.detection.TestDetector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 22/11/2021
 */
public class TestDetectorTest {

    @Test
    void test() {
        final List<String> allFullQualifiedNameTestClasses =
                new TestDetector("src/test/resources/v1/src/test/java/").getAllFullQualifiedNameTestClasses();
        assertEquals(1, allFullQualifiedNameTestClasses.size());
        assertEquals("fr.davidson.diff_jjoules_demo.InternalListTest", allFullQualifiedNameTestClasses.get(0));
    }
}
