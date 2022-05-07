package fr.davidson.diff.jjoules.mark.strategies.code_coverage;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.AbstractCoverageMarkStrategy;
import fr.davidson.diff.jjoules.selection.NewCoverage;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class CodeCoverageMarkStrategy extends AbstractCoverageMarkStrategy {

    @Override
    public boolean applyStrategy(
            Configuration configuration,
            Datas dataV1,
            Datas dataV2,
            Deltas deltaPerTestMethodName,
            MethodNamesPerClassNames consideredTest) {
        final NewCoverage coverageV1 = this.getCoverage(configuration.getPathToFirstVersion());
        final int nbCoveredLineV1 = this.nbCoveredLine;
        final NewCoverage coverageV2 = this.getCoverage(configuration.getPathToSecondVersion());
        final int nbCoveredLineV2 = this.nbCoveredLine;
        Data deltaOmega = new Data();
        for (String testClassName : consideredTest.keySet()) {
            for (String testMethodName : consideredTest.get(testClassName)) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testMethodName);
                for (String coveredSrcClassName : coverageV1.get(testClassName).get(testMethodName).keySet()) {
                    final double weightV1 = ((double) coverageV1.get(testClassName).get(testMethodName).get(coveredSrcClassName).size()) / ((double) nbCoveredLineV1);
                    final double weightV2 = ((double) coverageV2.get(testClassName).get(testMethodName).get(coveredSrcClassName).size()) / ((double) nbCoveredLineV2);
                    deltaOmega = deltaOmega.add(deltaPerTestMethodName.get(fullQualifiedName.toString()), Math.max(weightV1, weightV2));
                }
            }
        }
        return deltaOmega.cycles <= 0;
    }
}
