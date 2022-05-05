package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

import java.util.HashSet;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public class EmptyIntersectionTestFilter extends AbstractTestFilter {
    @Override
    protected MethodNamesPerClassNames _filter(Configuration configuration, Datas datasV1, Datas datasV2, Deltas deltaPerTestMethodName) {
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = datasV1.isEmptyIntersectionPerTestMethodName(datasV2);
        final MethodNamesPerClassNames consideredTests = new MethodNamesPerClassNames();
        for (String key : deltaPerTestMethodName.keySet()) {
            if (emptyIntersectionPerTestMethodName.get(key)) {
                final String[] split = key.split("#");
                if (!consideredTests.containsKey(split[0])) {
                    consideredTests.put(split[0], new HashSet<>());
                }
                consideredTests.get(split[0]).add(split[1]);
            }
        }
        return consideredTests;
    }
}
