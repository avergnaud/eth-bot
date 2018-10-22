package com.poc.ohlc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonObject;

import tmp.KrakenPublicRequest;

public enum OHLCCache {

	INSTANCE;
	
	/* ehcache */
	private ConcurrentHashMap<OHLC, JsonObject> cache;
	
	private OHLCCache() {
		cache = new ConcurrentHashMap<>();
	}
	
	public void insertOrUpdate(OHLC key, JsonObject krakenResponse) {
		System.out.println("[cache] OHLCCache.insertOrUpdate");
		if(key != null && krakenResponse != null) {
			// {"error":["EAPI:Rate limit exceeded"]}
			if(krakenResponse.getJsonArray("error").size() != 0) {
				System.out.println("[OHLCCache.insertOrUpdate] error " + krakenResponse.toString());
				return;
			}
			cache.put(key, krakenResponse);
		}
	}
	
	public JsonObject get(OHLC key) {
		JsonObject elt = cache.get(key);
		if(elt == null) {
			JsonObject tmp = new KrakenPublicRequest().queryPublic("OHLC", "pair="+ key.getPair() +"&interval=" + key.getGrain());
			cache.put(key, tmp);
			OHLCCacheManager.INSTANCE.start();
			return tmp;
		}
		return elt;
	}
	
	@SuppressWarnings("unchecked")
	public Set<OHLC> getAll() {
		return cache.keySet();
	}
}