package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

import java.util.HashSet;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public abstract class AbstractTestFilter implements TestFilter {

    protected abstract MethodNamesPerClassNames _filter(Configuration configuration, Datas datasV1, Datas datasV2, Deltas deltaPerTestMethodName);

    @Override
    public MethodNamesPerClassNames filter(Configuration configuration, Datas datasV1, Datas datasV2, Deltas deltaPerTestMethodName) {
        final MethodNamesPerClassNames consideredTests = this._filter(configuration, datasV1, datasV2, deltaPerTestMethodName);
        final MethodNamesPerClassNames consideredTestsInBothVersions = new MethodNamesPerClassNames();
        for (String testClassName : consideredTests.keySet()) {
            consideredTestsInBothVersions.put(testClassName, new HashSet<>());
            for (String testMethodName : consideredTests.get(testClassName)) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testMethodName);
                if (datasV1.containsKey(fullQualifiedName.toString()) && datasV2.containsKey(fullQualifiedName.toString())) {
                    consideredTestsInBothVersions.get(testClassName).add(testMethodName);
                }
            }
        }
        configuration.setConsideredTestsNames(consideredTestsInBothVersions);
        JSONUtils.write(Constants.joinFiles(configuration.getOutput(), PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME), consideredTestsInBothVersions);
        return consideredTestsInBothVersions;
    }
}
