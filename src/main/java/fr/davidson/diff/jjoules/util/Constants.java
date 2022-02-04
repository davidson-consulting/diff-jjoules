package fr.davidson.diff.jjoules.util;

import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 22/11/2021
 */
public class Constants {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String joinFiles(String... filenames) {
        final String joinedFiles = String.join(FILE_SEPARATOR, filenames);
        return joinedFiles.endsWith(FILE_SEPARATOR) ? joinedFiles : joinedFiles + FILE_SEPARATOR;
    }

    public static String joinFiles(List<String> filenames) {
        return joinFiles(filenames.toArray(new String[0]));
    }

    public static String joinPaths(String... pathnames) {
        return String.join(PATH_SEPARATOR, pathnames);
    }

    public static String joinPaths(List<String> pathnames) {
        return joinPaths(pathnames.toArray(new String[0]));
    }

    public static String joinLines(String... lines) {
        return String.join(NEW_LINE, lines);
    }

    public static String joinLines(List<String> lines) {
        return joinLines(lines.toArray(new String[0]));
    }

}
