package com.sten.mycache;

/**
 * interface for fetch object by key
 *
 * @Created lizhiliang
 */
@FunctionalInterface
public interface LiveMapFetch<T> {

    T fetch(String key) ;
}
