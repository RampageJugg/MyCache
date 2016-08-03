package com.sten.mycache;


import com.fangdd.newhouse.common.log.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * LiveMapManager manager some object map , fetch object from cache or from
 * input method by key . If cache expired , return last object and refresh cache
 * asynchronous .
 *
 * @Created lizhiliang
 */
public class LiveMapManager<T> {

    private long cacheMillis;
    private Map<String,LiveCache<T>> liveCacheMap;
    private LiveMapFetch<T> liveMapFetch;

    private Logger logger = LoggerFactory.getLogger(LiveMapManager.class) ;


    private boolean refresh = false ;

    public LiveMapManager(long cacheMillis, LiveMapFetch<T> liveMapFetch) {
        this.cacheMillis = cacheMillis ;
        this.liveMapFetch = liveMapFetch ;
    }

    /**
     * fetch cache ; if cache expired , synchronous fetch
     * @return
     */
    public T getCache(String key) {

        initLiveCache();

        T t ;
        if(liveCacheMap.containsKey(key) && (t = liveCacheMap.get(key).getElement()) != null) {
            return t ;
        } else {
            t = liveMapFetch.fetch(key) ;
            if(t != null) {
                LiveCache<T> liveAccess = new LiveCache<T>(cacheMillis, t) ;
                liveCacheMap.put(key, liveAccess) ;
                return t ;
            }
        }

        return null ;
    }

    /**
     * fetch cache ; if cache expired , return old cache and asynchronous fetch
     * @return
     */
    public T getCacheIfNecessary(String key) {

        initLiveCache();

        T t ;
        if(liveCacheMap.containsKey(key) && (t = liveCacheMap.get(key).getElement()) != null) {
            return t ;
        } else {
            if(liveCacheMap.containsKey(key)) {
                refreshCache(key) ;
                return liveCacheMap.get(key).getElementIfNecessary() ;
            } else {
                t = liveMapFetch.fetch(key) ;
                if(t != null) {
                    LiveCache<T> liveAccess = new LiveCache<T>(cacheMillis, t) ;
                    liveCacheMap.put(key, liveAccess) ;
                    return t ;
                }
            }
        }
        return t ;
    }

    /**
     * init liveCache
     */
    private void initLiveCache() {
        if(liveCacheMap == null) {
            liveCacheMap = new HashMap<>() ;
        }
    }

    /**
     * asynchronous refresh cache
     */
    private void refreshCache(String key) {

        if(refresh)
            return ;
        refresh = true ;
        try {
            Thread thread = new Thread(() -> {
                try {
                    T t = liveMapFetch.fetch(key);
                    if (t != null) {
                        LiveCache<T> liveAccess = new LiveCache<>(cacheMillis, t);
                        liveCacheMap.put(key, liveAccess);
                    }
                } catch (Exception e) {
                    logger.error("LiveMapManager.refreshCache thread error.key:",e);
                } finally {
                    refresh = false ;
                }
            }) ;
            thread.start();
        } catch (Exception e) {
            logger.error("LiveMapManager.refreshCache error.key:" + key, e);
        }
    }

}
