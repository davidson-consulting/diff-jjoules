package fr.davidson.diff.jjoules.utils;

import fr.davidson.diff.jjoules.util.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/02/2022
 */
public class UtilsTest {

    @Test
    void testCorrectPath() {
        assertEquals("this/is/a/path/", Utils.correctPath("this/is/a/path/"));
        assertEquals("this/is/a/path/", Utils.correctPath("this/is/a/path"));
    }
}
