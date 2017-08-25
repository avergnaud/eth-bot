package com.poc.ohlc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum OHLCCacheManager {
	
	INSTANCE;
	
	private boolean started = false;
	private String pair = "XETHZEUR";//TODO
	private int[] grainsEnMinutes = {1,5,15,30,60,240,1440,10080,21600};
	
	private OHLCCacheManager() {
		
		for(int i : grainsEnMinutes) {
			OHLC ohlc = new OHLC(pair,i);
//			JsonObject krakenResponse = ohlc.refresh();
			OHLCCache.INSTANCE.insertOrUpdate(ohlc, null);
		}
		
	}
	
	public void start() {
		if(started) {
			return;
		}
		int i = 0;
		for(OHLC ohlc : OHLCCache.INSTANCE.getAll()) {
			Runnable command = () -> OHLCCache.INSTANCE.insertOrUpdate(ohlc, ohlc.refresh());;
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(command, i++, 120, TimeUnit.SECONDS);
		}
		started = true;
	}
}
