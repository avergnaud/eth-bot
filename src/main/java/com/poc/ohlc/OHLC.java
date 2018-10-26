package com.poc.ohlc;

import java.util.Objects;

import javax.json.JsonObject;

import tmp.KrakenPublicRequest;

public class OHLC {

	/* exemple XETHZEUR */
	private final String pair;
	/* exemple 5 */
	private final int grain;
	private JsonObject dataFromKraken;

	public OHLC(String pair, int grain) {
		super();
		this.pair = Objects.requireNonNull(pair);
		this.grain = Objects.requireNonNull(grain);
	}

	public String getPair() {
		return pair;
	}
	public int getGrain() {
		return grain;
	}
	public JsonObject getDataFromKraken() {
		return dataFromKraken;
	}

	@Override
	public int hashCode() {
		return pair.hashCode() + grain;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof OHLC)) {
			return false;
		}
		OHLC other = (OHLC) obj;
		return (pair.equals(other.getPair())) && (grain == other.getGrain());
	}

	@Override
	public String toString() {
		return "OHLC [pair=" + pair + ", grain=" + grain + "]";
	}
	
	/** */
	public JsonObject refresh() {
		//System.out.println("[cache] OHLC.refresh - " + pair + " " + grain);
		JsonObject dataFromKraken = new KrakenPublicRequest().queryPublic("OHLC", "pair="+ pair +"&interval=" + grain);
		if(dataFromKraken != null) {
			/* cas timeout Kraken */
			this.dataFromKraken = dataFromKraken;
		} else {
			System.out.println("[cache] OHLC.refresh ! dataFromKraken null - " + pair + " " + grain);
		}
		return this.dataFromKraken;
	}
}
