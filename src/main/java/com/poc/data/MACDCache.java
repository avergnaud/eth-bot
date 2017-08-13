package com.poc.data;

import com.poc.macd.MACD;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public enum MACDCache {

	INSTANCE;
	
	/* ehcache */
	private Cache cache;
	
	private MACDCache() {
		cache = CacheManager.getInstance().getCache("com.poc.macd.cache");
	}
	
	public void insertOrUpdate(MACD macd) {
		String key = macd.toString();
		cache.put(new Element(key, macd));
	}
	
	public MACD get(MACD macd) {
		String key = macd.toString();
		Element elt = cache.get(key);
		if(elt != null) {
			return (MACD) elt.getObjectValue();
		}
		return null;
	}

}
