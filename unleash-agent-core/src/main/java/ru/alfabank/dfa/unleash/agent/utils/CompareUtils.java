package ru.alfabank.dfa.unleash.agent.utils;

import com.cedarsoftware.util.DeepEquals;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CompareUtils {

     public static boolean deepCompare(Object first, Object second) {
        Map<String, Object> options = new HashMap<>();
        boolean same = DeepEquals.deepEquals(first, second, options);
        if (!same) {
            String diff = (String) options.get(DeepEquals.DIFF);
            log.info("{} difference: {}", first.getClass().getSimpleName(), diff);
        }

        return same;
    }

    public static boolean equals(Object local, Object remote) {
        if (local == null && remote == null) {
            return true;
        }
        if (local == null || remote == null) {
            return false;
        }

        return local.equals(remote);
    }

    public static boolean notEquals(Object local, Object remote) {
        return !equals(local, remote);
    }
}
