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

    // TODO asking my self : should we take the medians separately or should we take the medians over the 3 measures ?
    static Map<String, Data> computeMedian(final Datas data) {
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

    static double getMedian(List<Double> values) {
        return values.size() % 2 == 0 ?
                values.get(values.size() / 2) + values.get((values.size() + 1) / 2) :
                values.get(values.size() / 2);
    }

}
