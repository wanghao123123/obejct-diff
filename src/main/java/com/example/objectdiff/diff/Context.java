package com.example.objectdiff.diff;;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hao.wang
 */
public class Context {

    private Set<String> excludeProperties;

    private Set<Tuple2<Object, Object>> tracker;

    public Context() {
        tracker = new HashSet<>();
        excludeProperties = new HashSet<>();
    }

    public Set<String> getExcludeProperties() {
        return excludeProperties;
    }

    public Set<Tuple2<Object, Object>> getTracker() {
        return tracker;
    }

}
