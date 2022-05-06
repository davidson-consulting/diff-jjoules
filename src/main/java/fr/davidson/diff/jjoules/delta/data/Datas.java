package fr.davidson.diff.jjoules.delta.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 05/07/2021
 */
public class Datas extends HashMap<String, List<Data>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Datas.class);

    public Map<String, Boolean> isEmptyIntersectionPerTestMethodName(Datas that) {
        final Map<String, Boolean> isEmptyIntersectionPerTestMethodName = new HashMap<>();
        for (String key : this.keySet()) {
            if (!that.containsKey(key)) {
                LOGGER.info("WARNING! Missing key {} in V2!", key);
                continue;
            }
            final List<Double> sortedValuesV1 = this.get(key).stream().map(data -> data.cycles).sorted().collect(Collectors.toList());
            isEmptyIntersectionPerTestMethodName.put(key,
                    that.get(key)
                            .stream()
                            .map(data -> data.cycles)
                            .noneMatch(value ->
                                    sortedValuesV1.get(0) < value && value < sortedValuesV1.get(sortedValuesV1.size() - 1)
                            )
            );
        }
        return isEmptyIntersectionPerTestMethodName;
    }


}
