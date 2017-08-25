package com.poc.ohlc;

import java.util.List;

import javax.json.JsonObject;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import tmp.KrakenPublicRequest;

public enum OHLCCache {

	INSTANCE;
	
	/* ehcache */
	private Cache cache;
	
	private OHLCCache() {
		cache = CacheManager.getInstance().getCache("com.poc.ohlc.cache");
	}
	
	public void insertOrUpdate(OHLC key, JsonObject krakenResponse) {
		cache.put(new Element(key, krakenResponse));
	}
	
	public JsonObject get(OHLC key) {
		Element elt = cache.get(key);
		if(elt == null) {
			JsonObject tmp = new KrakenPublicRequest().queryPublic("OHLC", "pair="+ key.getPair() +"&interval=" + key.getGrain());
			OHLCCacheManager.INSTANCE.start();
			return tmp;
		}
		return (JsonObject) elt.getObjectValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<OHLC> getAll() {
		return (List<OHLC>)cache.getKeys();
	}
}