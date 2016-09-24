package simulizer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthew on 22/09/16.
 */
public class DataUtils {

    /**
     * reverse a Map to give a mapping from values to a list of keys
     * @param map the map to reverse
     * @return the reversed map
     */
    public static <K, V> Map<V, List<K>> reverseMapping(Map<K, V> map) {
        Map<V, List<K>> rev = new HashMap<>();

        for(Map.Entry<K, V> e : map.entrySet()) {
            V v = e.getValue();

            if(!rev.containsKey(v)) {
                rev.put(v, new ArrayList<>());
            }

            rev.get(v).add(e.getKey());
        }

        return rev;
    }
}
