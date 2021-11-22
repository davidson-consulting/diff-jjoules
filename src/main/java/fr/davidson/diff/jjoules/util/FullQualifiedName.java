package fr.davidson.diff.jjoules.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return this.className + "#" + this.methodName;
    }

    @NotNull
    public static Set<String> toSetFullQualifiedNames(Map<String, Set<String>> testsList) {
        return testsList.keySet()
                .stream()
                .flatMap(testClassName ->
                        testsList.get(testClassName)
                                .stream()
                                .map(testMethodName ->
                                        new FullQualifiedName(testClassName, testMethodName).toString()
                                )
                ).collect(Collectors.toSet());
    }
}
