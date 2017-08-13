package com.poc.macd;

import com.poc.data.MACDCache;

/**
 * builder pattern pour MACD
 */
public class MACDBuilder {

	private String currencyPair;
	private int dureeUnitaire;
	private int intervalleMoyenneRapide;
	private int intervalleMoyenneLente;
	private int intervalleSignal;
	
	/** exemple XETHZEUR */
	public MACDBuilder setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
		return this;
	}
	
	/** exemple 1440 (24h) */
	public MACDBuilder setDureeUnitaire(int minutes) {
		this.dureeUnitaire = minutes;
		return this;
	}
	
	/** exemple 12 */
	public MACDBuilder setIntervalleMoyenneRapide(int intervalleRapide) {
		this.intervalleMoyenneRapide = intervalleRapide;
		return this;
	}

	/** exemple 26 */
	public MACDBuilder setIntervalleMoyenneLente(int intervalleLent) {
		this.intervalleMoyenneLente = intervalleLent;
		return this;
	}
	
	/** exemple 9 */
	public MACDBuilder setIntervalleSignal(int intervalleSignal) {
		this.intervalleSignal = intervalleSignal;
		return this;
	}

	public MACD build() {
		MACD candidat = new MACD(currencyPair, dureeUnitaire, intervalleMoyenneRapide, intervalleMoyenneLente, intervalleSignal);
		if( MACDCache.INSTANCE.get(candidat) != null) {
			return MACDCache.INSTANCE.get(candidat);
		} else {
			MACDCache.INSTANCE.insertOrUpdate(candidat);
			return candidat;
		}
	}
	
}
