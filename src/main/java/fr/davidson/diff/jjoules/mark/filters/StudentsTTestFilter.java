package fr.davidson.diff.jjoules.mark.filters;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.apache.commons.math3.stat.inference.TTest;

import java.util.HashSet;
import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public class StudentsTTestFilter extends AbstractTestFilter {

    @Override
    protected MethodNamesPerClassNames _filter(Configuration configuration, Datas datasV1, Datas datasV2, Deltas deltaPerTestMethodName) {
        final MethodNamesPerClassNames consideredTestMethod = new MethodNamesPerClassNames();
        final TTest tTest = new TTest();
        for (String testNameV1 : datasV1.keySet()) {
            if (datasV2.containsKey(testNameV1)) {
                final List<Data> dataV1 = datasV1.get(testNameV1);
                final List<Data> dataV2 = datasV2.get(testNameV1);
                final double[] cyclesV1 = new double[dataV1.size()];
                final double[] cyclesV2 = new double[dataV2.size()];
                for (int i = 0; i < dataV1.size(); i++) {
                    cyclesV1[i] = dataV1.get(i).cycles;
                    cyclesV2[i] = dataV2.get(i).cycles;
                }
                final double pvalue = tTest.t(cyclesV1, cyclesV2);
                if (pvalue <= 0.05) {
                    final FullQualifiedName fullQualifiedName = FullQualifiedName.fromString(testNameV1);
                    if (!consideredTestMethod.containsKey(fullQualifiedName.className)) {
                        consideredTestMethod.put(fullQualifiedName.className, new HashSet<>());
                    }
                    consideredTestMethod.get(fullQualifiedName.className).add(fullQualifiedName.methodName);
                }
            }
        }
        return consideredTestMethod;
    }
}
