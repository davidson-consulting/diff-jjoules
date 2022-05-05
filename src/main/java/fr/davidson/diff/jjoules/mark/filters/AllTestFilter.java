package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

import java.util.HashSet;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public class AllTestFilter extends AbstractTestFilter {

    @Override
    protected MethodNamesPerClassNames _filter(Configuration configuration, Datas datasV1, Datas datasV2, Deltas deltaPerTestMethodName) {
        final MethodNamesPerClassNames consideredTestMethod = new MethodNamesPerClassNames();
        for (String testMethodName : deltaPerTestMethodName.keySet()) {
            final FullQualifiedName fullQualifiedName = FullQualifiedName.fromString(testMethodName);
            if (!consideredTestMethod.containsKey(fullQualifiedName.className)) {
                consideredTestMethod.put(fullQualifiedName.className, new HashSet<>());
            }
            consideredTestMethod.get(fullQualifiedName.className).add(fullQualifiedName.methodName);
        }
        return consideredTestMethod;
    }

}
