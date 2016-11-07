package utils;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by user on 08.11.16.
 *
 * Helps sort map by value
 */
public class SortedByValueMap {
    public static <K, V extends Comparable<V>> SortedMap<K, V> sortByValues(final SortedMap<K, V> map) {
        SortedMap<K, V> sortedByValues = new TreeMap<>((K k1, K k2) -> {
            int compare = map.get(k2).compareTo(map.get(k1));
            if (compare == 0) return 1;
            else return compare;
        });
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    private SortedByValueMap(){}
}
