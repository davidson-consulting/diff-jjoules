package fr.davidson.diff.jjoules.util;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class FullQualifiedName {

    public final String className;
    public final String methodName;

    public FullQualifiedName(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public static FullQualifiedName fromString(String fullQualifiedName) {
        final String[] split = fullQualifiedName.split("#");
        return new FullQualifiedName(split[0], split[1]);
    }

}
