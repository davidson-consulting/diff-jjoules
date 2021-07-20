package fr.davidson.diff.jjoules.delta.data;

import org.pitest.util.Log;
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
public class Datas extends HashMap<String, List<Data>>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(Datas.class);

    public Map<String, Boolean> isEmptyIntersectionPerTestMethodName(Datas that) {
        final Map<String, Boolean> isEmptyIntersectionPerTestMethodName = new HashMap<>();
        LOGGER.info("Computing isEmptyIntersectionPerTestMethodName for {}", this.keySet().toString());
        for (String key : this.keySet()) {
            if (!that.containsKey(key)) {
                LOGGER.info("WARNING! Missing key {} in V2!", key);
                continue;
            }
            final List<Double> sortedValuesV1 = this.get(key).stream().map(data -> data.instructions).sorted().collect(Collectors.toList());
            LOGGER.info("Sorted V1: {}", sortedValuesV1);
            LOGGER.info("V2: {}", that.get(key).stream()
                    .map(data -> data.instructions).collect(Collectors.toList()));
            isEmptyIntersectionPerTestMethodName.put(key,
                    that.get(key)
                            .stream()
                            .map(data -> data.instructions)
                            .peek(data -> LOGGER.info("instr {}", data))
                            .noneMatch(value ->
                                    sortedValuesV1.get(0) <= value && sortedValuesV1.get(sortedValuesV1.size() - 1) >= value
                            )
            );
        }
        LOGGER.info("isEmptyIntersectionPerTestMethodName {}", isEmptyIntersectionPerTestMethodName.toString());
        return isEmptyIntersectionPerTestMethodName;
    }


}
