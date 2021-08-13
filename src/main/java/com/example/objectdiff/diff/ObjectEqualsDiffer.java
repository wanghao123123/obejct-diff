package com.example.objectdiff.diff;;

import java.util.Optional;

/**
 * @author hao.wang
 */
public class ObjectEqualsDiffer extends AbstractDiffer {
    @Override
    public Optional<DiffNode> doDiff(DiffNode parentNode, String propertyName, Object origin, Object target) {
        return immutableObjectDiff(parentNode, propertyName, origin, target);
    }
}
