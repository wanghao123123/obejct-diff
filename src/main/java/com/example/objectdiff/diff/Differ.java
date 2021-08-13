package com.example.objectdiff.diff;;

import java.util.Optional;

/**
 * @author hao.wang
 */
public interface Differ {

    Optional<DiffNode> diff(Object from, Object to);

    Optional<DiffNode> diff(Object from, Object to, Filter filter);

}
