package fr.davidson.diff.jjoules.mark.strategies.diff_coverage;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategy;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class DiffCoverageMarkStrategy implements MarkStrategy {
    @Override
    public boolean applyStrategy(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName, MethodNamesPerClassNames consideredTest) {
        return false;
    }
}
