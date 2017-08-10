package com.poc.data;

import javax.json.JsonObject;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public enum OHLCDataCache {

	INSTANCE;
	
	/* ehcache */
	private Cache cache;
	
	private OHLCDataCache() {
		cache = CacheManager.getInstance().getCache("com.poc.ohlc.cache");
	}
	
	public void insertOrUpdate(String key, JsonObject ohlcData) {
		cache.put(new Element(key, ohlcData));
	}
	
	public JsonObject get(String key) {
		Element elt = cache.get(key);
		return (JsonObject) elt.getObjectValue();
	}
}