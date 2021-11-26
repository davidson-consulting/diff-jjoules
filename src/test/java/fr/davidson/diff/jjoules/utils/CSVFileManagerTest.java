package fr.davidson.diff.jjoules.utils;

import fr.davidson.diff.jjoules.util.CSVFileManager;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/11/2021
 */
public class CSVFileManagerTest {

    @Test
    void testExpectException() {
        try {
            final Map<String, Set<String>> testList = CSVFileManager.readFile("does-not-exist");
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            assertTrue(expected.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    void testNull() {
        final Map<String, Set<String>> testList = CSVFileManager.readFile(null);
        assertTrue(testList.isEmpty());
    }

    @Test
    void testEmpty() {
        final Map<String, Set<String>> testList = CSVFileManager.readFile("");
        assertTrue(testList.isEmpty());
    }

    @Test
    void test() {
        final Map<String, Set<String>> testList = CSVFileManager.readFile("src/test/resources/diff-jjoules-demo/testsList.csv");
        assertTrue(testList.containsKey("fr.davidson.TestClass"));
        assertTrue(testList.containsKey("fr.davidson.subpackage.TestClass"));
        assertEquals(2, testList.get("fr.davidson.subpackage.TestClass").size());
        assertEquals(2, testList.get("fr.davidson.TestClass").size());
        assertTrue(testList.get("fr.davidson.TestClass").contains("testMethodOne"));
        assertTrue(testList.get("fr.davidson.TestClass").contains("testMethodTwo"));
        assertTrue(testList.get("fr.davidson.subpackage.TestClass").contains("testMethodOne"));
        assertTrue(testList.get("fr.davidson.subpackage.TestClass").contains("testMethodTwo"));
    }
}
