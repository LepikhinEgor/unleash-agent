package ru.baldenna.unleashagent.core.utils;

public class CompareUtils {

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
