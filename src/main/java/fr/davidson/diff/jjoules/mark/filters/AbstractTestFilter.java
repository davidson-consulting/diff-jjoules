package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

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
        configuration.setConsideredTestsNames(consideredTests);
        JSONUtils.write(Constants.joinFiles(configuration.getOutput(), PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME), consideredTests);
        return consideredTests;
    }
}
