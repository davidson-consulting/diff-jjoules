package fr.davidson.diff.jjoules.util;

import java.util.*;

public class Utils {

    public static <T> void addToGivenMap(final String key, T value, Map<String, List<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new ArrayList<>());
        }
        givenMap.get(key).add(value);
    }

    public static <T> void addToGivenMap(final String key, List<T> values, Map<String, List<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new ArrayList<>());
        }
        givenMap.get(key).addAll(values);
    }

    public static <T> void addToGivenMapSet(final String key, List<T> values, Map<String, Set<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new HashSet<>());
        }
        givenMap.get(key).addAll(values);
    }

}
