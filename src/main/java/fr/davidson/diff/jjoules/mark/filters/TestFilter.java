package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public interface TestFilter {

    String PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME = "consideredTestMethods.json";

    public MethodNamesPerClassNames filter(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName);

}
