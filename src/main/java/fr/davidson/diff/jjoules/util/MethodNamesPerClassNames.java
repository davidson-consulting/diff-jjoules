package fr.davidson.diff.jjoules.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 28/02/2022
 */
public class MethodNamesPerClassNames extends HashMap<String, Set<String>> {

    public static MethodNamesPerClassNames from(Map<String, Set<String>> source) {
        final MethodNamesPerClassNames from = new MethodNamesPerClassNames();
        from.putAll(source);
        return from;
    }

    public String[] toFullQualifiedNameMethods() {
        return this.keySet()
                .stream()
                .flatMap(className ->
                        this.get(className).stream().map(methodName -> new FullQualifiedName(className, methodName).toString())
                ).toArray(String[]::new);
    }

}
