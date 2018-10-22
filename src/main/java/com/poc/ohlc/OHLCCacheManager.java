package com.poc.ohlc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum OHLCCacheManager {
	
	INSTANCE;
	
	private boolean started = false;
	private String pair = "XETHZEUR";//TODO
	private int[] grainsEnMinutes = {1,5,15,30,60,240,1440,10080,21600};
	private OHLC[] ohlcs = new OHLC[9];
	
	private OHLCCacheManager() {
		
		for(int i=0; i < grainsEnMinutes.length; i++) {
			OHLC ohlc = new OHLC(pair,grainsEnMinutes[i]);
//			JsonObject krakenResponse = ohlc.refresh();
			ohlcs[i] = ohlc;
		}
		
	}
	
	public void start() {
		if(started) {
			return;
		}
//		int i = 0;
		for(int i=0; i< ohlcs.length; i++) {
			OHLC ohlc = ohlcs[i];
			Runnable command = () -> OHLCCache.INSTANCE.insertOrUpdate(ohlc, ohlc.refresh());
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			/* pour éviter de faire deux requêtes dans la même seconde */
			try {
				Thread.sleep(10_000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			//service.scheduleAtFixedRate(command, i, 3, TimeUnit.MINUTES);
			service.scheduleWithFixedDelay(command, i, 2, TimeUnit.MINUTES);
		}
		started = true;
	}
}
