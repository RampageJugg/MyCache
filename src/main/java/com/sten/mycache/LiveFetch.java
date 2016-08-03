package com.sten.mycache;

/**
 * interface for fetch object
 *
 * @Created lizhiliang
 */
@FunctionalInterface
public interface LiveFetch<T> {

    T fetch() ;
}
