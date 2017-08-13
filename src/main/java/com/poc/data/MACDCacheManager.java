package com.poc.data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.poc.macd.MACD;

/**
 * pour CRON refresh auto
 */
public enum MACDCacheManager {
	
	INSTANCE;
	
	Set<MACD> alreadyScheduled = new HashSet<>();
	
	/* 1 minute en dur... (?) */
	public void refreshAtFixedRate(MACD macd) {
		
		if( MACDCache.INSTANCE.get(macd) == null) {
			MACDCache.INSTANCE.insertOrUpdate(macd);
		}
		
		if(alreadyScheduled.contains(MACDCache.INSTANCE.get(macd))) {
			/* tourne déjà */
			return;
		}
		
		Runnable command = () -> MACDCache.INSTANCE.get(macd).refresh();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(command, 0, 1, TimeUnit.MINUTES);
		
		alreadyScheduled.add(MACDCache.INSTANCE.get(macd));
	}
	
	public String getScheduledMACD() {
		
		StringBuilder s = new StringBuilder("alreadyScheduled MACDs : ");
		for(MACD macd : alreadyScheduled) {
			s.append("[").append(macd.toString()).append("]");
		}
		return s.toString();
	}
}
