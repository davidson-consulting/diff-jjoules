package fr.davidson.diff.jjoules.utils;

import fr.davidson.diff.jjoules.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/02/2022
 */
public class ConstantsTest {

    @Test
    void testJoinFiles() {
        assertEquals("src/test/resources/", Constants.joinFiles("src", "test", "resources"));
        assertEquals("src/test/resources/", Constants.joinFiles("src", "test", "resources/"));
        assertEquals("src/test/resources/", Constants.joinFiles(
                Arrays.stream(new String[]{"src", "test", "resources"})
                        .collect(Collectors.toList()))
        );
        assertEquals("src/test/resources/", Constants.joinFiles(
                Arrays.stream(new String[]{"src", "test", "resources/"})
                        .collect(Collectors.toList()))
        );
    }

    @Test
    void testJoinLines() {
        assertEquals("src" + Constants.NEW_LINE + "test", Constants.joinLines("src", "test"));
        assertEquals("src" + Constants.NEW_LINE + "test", Constants.joinLines(
                Arrays.stream(new String[]{"src", "test"})
                        .collect(Collectors.toList()))
        );
    }

    @Test
    void testJoinPath() {
        assertEquals("src" + Constants.PATH_SEPARATOR + "test", Constants.joinPaths("src", "test"));
        assertEquals("src" + Constants.PATH_SEPARATOR + "test", Constants.joinPaths(
                Arrays.stream(new String[]{"src", "test"})
                        .collect(Collectors.toList()))
        );
    }

}
