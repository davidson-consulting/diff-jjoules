package fr.davidson.diff.jjoules.utils;

import fr.davidson.diff.jjoules.util.CSVFileManager;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/11/2021
 */
public class CSVFileManagerTest {

    @Test
    void testWriteFile() throws Exception {
        final List<String> lines = Arrays.asList(
                "testClassName;testMethodName;testMethodNam2",
                "testClassName;testMethodName;testMethodNam2"
        );
        CSVFileManager.writeFile("target/testList.csv",lines);
        try (final BufferedReader reader = new BufferedReader(new FileReader("target/testList.csv"))) {
            assertEquals(lines, reader.lines().collect(Collectors.toList()));
        }
    }

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
        final Map<String, Set<String>> testList = CSVFileManager.readFile("src/test/resources/json/testsList.csv");
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
