package com.example.objectdiff.diff;;

/**
 * @author hao.wang
 */
public interface Visitor {
    void visit(String fullPath, DiffNode node);
}
