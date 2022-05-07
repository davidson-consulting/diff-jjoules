package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/04/2022
 */
public interface MarkStrategy {

    public boolean applyStrategy(Configuration configuration,
                              Datas dataV1,
                              Datas dataV2,
                              Deltas deltaPerTestMethodName,
                              MethodNamesPerClassNames consideredTest);

}
