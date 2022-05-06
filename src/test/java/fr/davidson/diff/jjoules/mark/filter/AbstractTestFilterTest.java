package fr.davidson.diff.jjoules.mark.filter;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 05/05/2022
 */
public abstract class AbstractTestFilterTest {

    protected abstract TestFilter getFilter();

    protected MethodNamesPerClassNames runFilter() {
        final Configuration configuration = new Configuration();
        configuration.setOutput("target");
        return this.getFilter().filter(
                configuration,
                JSONUtils.read("src/test/resources/json/gson_data_v1.json", Datas.class),
                JSONUtils.read("src/test/resources/json/gson_data_v2.json", Datas.class),
                JSONUtils.read("src/test/resources/json/gson_deltas.json", Deltas.class)
        );
    }

}
