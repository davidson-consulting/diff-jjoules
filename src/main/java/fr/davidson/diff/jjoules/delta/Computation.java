package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/09/2021
 */
public class Computation {

    static Deltas computeDelta(
            Map<String, Data> mediansV1,
            Map<String, Data> mediansV2
    ) {
        final Deltas deltaPerName = new Deltas();
        for (String testMethodName : mediansV1.keySet()) {
            deltaPerName.put(testMethodName, new Delta(mediansV1.get(testMethodName), mediansV2.get(testMethodName)));
        }
        return deltaPerName;
    }

    public static Map<String, Data> computeMedian(final Datas data) {
        final Map<String, Data> medianPerTestName = new HashMap<>();
        for (String testMethodName : data.keySet()) {
            medianPerTestName.put(testMethodName,
                    new Data(
                            getMedian(data.get(testMethodName), Data::getEnergy),
                            getMedian(data.get(testMethodName), Data::getInstructions),
                            getMedian(data.get(testMethodName), Data::getDurations),
                            getMedian(data.get(testMethodName), Data::getCycles),
                            getMedian(data.get(testMethodName), Data::getCaches),
                            getMedian(data.get(testMethodName), Data::getCacheMisses),
                            getMedian(data.get(testMethodName), Data::getBranches),
                            getMedian(data.get(testMethodName), Data::getBranchMisses)
                    )
            );
        }
        return medianPerTestName;
    }

    static double getMedian(List<Data> values, Function<Data, Double> getter) {
        return getMedian(values.stream().map(getter).sorted().collect(Collectors.toList()));
    }

    public static double getMedian(List<Double> values) {
        if (values.size() == 2) {
            return (values.get(0) + values.get(1)) / 2;
        }
        final int middleCursor = values.size() / 2;
        if (values.size() % 2 == 0) {
            final double valueA = values.get(middleCursor - 1);
            final double valueB = values.get(middleCursor);
            return (valueA + valueB) / 2;

        } else {
            return values.get(middleCursor);
        }
    }
}
