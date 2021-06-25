package fr.davidson.diff.jjoules.delta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Delta extends Data {

    public final Data dataV1;

    public final Data dataV2;

    public Delta(Data dataV1, Data dataV2) {
        super(
                dataV2.energy - dataV1.energy,
                dataV2.instructions - dataV1.instructions,
                dataV2.durations - dataV1.durations
        );
        this.dataV1 = dataV1;
        this.dataV2 = dataV2;
    }

    public static Map<String, Delta> from(Map read) {
        final Map<String, Delta> map = new HashMap<>();
        for (Object key : read.keySet()) {
            final Map<String, ?> current = (Map<String, ?>)read.get(key);
            map.put(key.toString(),
                    new Delta(
                            new Data((Map<String, ?>)current.get("dataV1")),
                            new Data((Map<String, ?>)current.get("dataV2"))
                    ));
        }
        return map;
    }
}
