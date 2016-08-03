package com.sten.mycache;

/**
 * LiveCache cache one element cacheMillis time
 *
 * @Created lizhliang
 *
 */
public class LiveCache<T> {

	private final long cacheMillis;
	private final T element;
	private final long createTime;
	
	public LiveCache(long cacheMillis, T element) {
		this.cacheMillis = cacheMillis;
		this.element = element;
		this.createTime = System.currentTimeMillis();
	}
	
	public T getElement() {
		long currentTime = System.currentTimeMillis();
		if(cacheMillis > 0 && currentTime - createTime > cacheMillis) {
			return null;
		} else {
			return element;
		}
	}
	
	public T getElementIfNecessary() {
		return element;
	}
}
