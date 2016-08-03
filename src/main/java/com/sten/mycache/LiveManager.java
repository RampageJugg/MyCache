package com.sten.mycache;


import com.fangdd.newhouse.common.log.LoggerFactory;
import org.slf4j.Logger;

/**
 * LiveManager manager some object , fetch object from cache or from
 * input method . If cache expired , return last object and refresh cache
 * asynchronous .
 *
 * @Created lizhiliang
 */
public class LiveManager<T> {

    private long cacheMillis;
    private LiveCache<T> liveCache;
    private LiveFetch<T> liveFetch ;

    private Logger logger = LoggerFactory.getLogger(LiveManager.class) ;


    private boolean refresh = false ;

    public LiveManager(long cacheMillis, LiveFetch<T> liveFetch) {
        this.cacheMillis = cacheMillis ;
        this.liveFetch = liveFetch ;
    }

    /**
     * fetch cache ; if cache expired , synchronous fetch
     * @return
     */
    public T getCache() {

        initLiveCache();

        if(liveCache != null) {
            T t  ;
            if((t= liveCache.getElement()) != null) {
                return t ;
            } else {
                t = liveFetch.fetch() ;
                if(t != null) {
                    liveCache = new LiveCache<T>(cacheMillis, t) ;
                    return t ;
                }
            }
        }

        return null ;
    }

    /**
     * fetch cache ; if cache expired , return old cache and asynchronous fetch
     * @return
     */
    public T getCacheIfNecessary() {

        initLiveCache();

        if(liveCache != null) {
            T t  ;
            if((t= liveCache.getElement()) != null) {
                return t ;
            } else {
                refreshCache() ;
                return liveCache.getElementIfNecessary() ;
            }
        }

        return null ;
    }

    /**
     * init liveCache
     */
    private void initLiveCache() {
        if(liveCache == null) {
            T t = liveFetch.fetch() ;
            if(t != null) {
                liveCache = new LiveCache<T>(cacheMillis, t) ;
            }
        }
    }

    /**
     * asynchronous refresh cache
     */
    private void refreshCache() {

        if(refresh)
            return ;
        refresh = true ;
        try {
            Thread thread = new Thread(() -> {
                try {
                    T t = liveFetch.fetch();
                    if (t != null) {
                        liveCache = new LiveCache<>(cacheMillis, t);
                    }
                } catch (Exception e){
                    logger.error("LiveManager.refreshCache thread error.", e);
                } finally {
                    refresh = false ;
                }
            }) ;
            thread.start();
        } catch (Exception e) {
            logger.error("LiveManager.refreshCache error.", e);
        }
    }

}
