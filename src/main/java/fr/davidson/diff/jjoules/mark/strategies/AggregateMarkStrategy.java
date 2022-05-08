package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 08/05/2022
 */
public class AggregateMarkStrategy implements MarkStrategy {
    @Override
    public boolean applyStrategy(
            Configuration configuration,
            Datas dataV1,
            Datas dataV2,
            Deltas deltaPerTestMethodName,
            MethodNamesPerClassNames consideredTest) {
        Data deltaOmega = new Data();
        for (String testClassName : consideredTest.keySet()) {
            for (String testMethodName : consideredTest.get(testClassName)) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testMethodName);
                deltaOmega = deltaOmega.add(deltaPerTestMethodName.get(fullQualifiedName.toString()));
            }
        }
        return deltaOmega.cycles <= 0;
    }
}
