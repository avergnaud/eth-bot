package com.poc.slackbot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.poc.data.MACDCache;
import com.poc.macd.MACD;

public class MacdWatcher {

	MACD macd;
	boolean latestState;
	ScheduledExecutorService service;
	
	public MacdWatcher(MACD macd) {
		this.macd = MACDCache.INSTANCE.get(macd);
		latestState = this.macd.isEstBullish();
		service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(this::watch, 0, 1, TimeUnit.MINUTES);
	}
	
	/* ce qu'on fait toutes les minutes : */
	private void watch() {
		boolean watchedState = macd.isEstBullish();
		if(watchedState != latestState) {
			String estBullishString = watchedState ? "bullish":"bearish";
			System.out.println("! MACD changed state - now is " + estBullishString + " (" + macd.toString() + ")");
			latestState = watchedState;
		}
	}
}
