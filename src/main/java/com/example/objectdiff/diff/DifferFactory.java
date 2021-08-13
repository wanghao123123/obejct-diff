package com.example.objectdiff.diff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hao.wang
 */
public class DifferFactory {

    private static final DifferFactory INSTANCE = new DifferFactory();

    private DifferFactory() {

    }

    public static DifferFactory getInstance() {
        return INSTANCE;
    }

    private final DifferClassFactory differClassFactory = DifferClassFactory.getInstance();

    private final Map<Class<?>,Differ> cache = new ConcurrentHashMap<>();

    public Differ getDiffer(Class<?> type) {
        Differ diff = cache.get(type);
        if (diff != null) {
            return diff;
        }
        DifferClassWrapper wrapper = differClassFactory.getDifferClassWrapper(type);
        AbstractDiffer abstractDiffer = null;
        try {
            abstractDiffer = (AbstractDiffer) wrapper.getDifferClass().newInstance();
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }
        Differ preDiff = cache.putIfAbsent(type, abstractDiffer);
        if (preDiff == null) {
            doInjection(abstractDiffer, wrapper);
        }
        return cache.get(type);
    }

    private void doInjection(AbstractDiffer differ, DifferClassWrapper wrapper) {
        if (wrapper.getDependClasses() == null || wrapper.getDependClasses().isEmpty()) {
            return;
        }
        for (Class<?> dependClass : wrapper.getDependClasses()) {
            AtomicReference<AbstractDiffer> reference = new AtomicReference<>();
            reference.set((AbstractDiffer) getDiffer(dependClass));
            differ.setDiffer(dependClass.getName(), reference);
        }
    }

}
