package com.poc.macd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.poc.ohlc.OHLC;
import com.poc.ohlc.OHLCCache;

/* TODO immutable ? */
public class MACD {

	/* la définition du MACD */
	private String currencyPair;
	private int dureeUnitaire;
	private int intervalleMoyenneRapide;
	private int intervalleMoyenneLente;
	private int intervalleSignal;
	
	/* constantes déduites de la définition */
	private BigDecimal kRapide;
	private BigDecimal kLent;
	
	/* son state */
	private List<TradingPeriod> listPeriods;
	private boolean bullish;
	private double acceleration;
	
	/* package private, utiliser le builder */
	MACD(String currencyPair, int dureeUnitaire, int intervalleMoyenneRapide, int intervalleMoyenneLente,
			int intervalleSignal) {
		super();
		
		this.currencyPair = currencyPair;
		this.dureeUnitaire = dureeUnitaire;
		this.intervalleMoyenneRapide = intervalleMoyenneRapide;
		this.intervalleMoyenneLente = intervalleMoyenneLente;
		this.intervalleSignal = intervalleSignal;
		
		this.kRapide = BigDecimal.valueOf(2)
				.divide(BigDecimal.valueOf(intervalleMoyenneRapide).add(BigDecimal.ONE), 10, RoundingMode.HALF_EVEN)
				.setScale(10, RoundingMode.HALF_EVEN);
		this.kLent = BigDecimal.valueOf(2)
				.divide(BigDecimal.valueOf(intervalleMoyenneLente).add(BigDecimal.ONE), 10, RoundingMode.HALF_EVEN)
				.setScale(10, RoundingMode.HALF_EVEN);
		this.listPeriods = new ArrayList<>();
	}
	
	public List<TradingPeriod> getListPeriods() {
		return listPeriods;
	}

	public void refresh() {
		
		List<TradingPeriod> temporaire = new ArrayList<>();

		OHLC ohlc = new OHLC(currencyPair, dureeUnitaire);
		JsonObject jsonObject = OHLCCache.INSTANCE.get(ohlc);
//		JsonObject jsonObject = new KrakenPublicRequest().queryPublic("OHLC", "pair=" + currencyPair + "&interval=" + dureeUnitaire);/* XETHZEUR 1440*/
		
		if(jsonObject == null) {
			/* pb requête Kraken */
			return;
		}
		
		JsonArray arr = jsonObject.getJsonObject("result").getJsonArray(currencyPair);

		for (JsonArray jv : arr.getValuesAs(JsonArray.class)) {

			long ts = jv.getJsonNumber(0).longValue() * 1000;

			BigDecimal closingPrice = new BigDecimal(jv.getString(4));

			temporaire.add(new TradingPeriod(ts, closingPrice));

		}

		alimenteMoyennesRapides(temporaire);
		
		alimenteMoyennesLentesEtMACD(temporaire);

		alimenteMoyennesSignal(temporaire);
		
		listPeriods = temporaire;
		
		TradingPeriod courante = listPeriods.get(listPeriods.size() - 1);
		bullish = courante.getMacd().compareTo(courante.getSignal()) > 0;
		
		TradingPeriod precedente = listPeriods.get(listPeriods.size() - 2);
		acceleration = courante.getMacd().subtract(precedente.getMacd()).doubleValue();
	}

	private void alimenteMoyennesRapides(List<TradingPeriod> listPeriods) {

		/* première valeur */
		TradingPeriod premierePeriodeIntervalleRapide = listPeriods.get(intervalleMoyenneRapide - 1);
		BigDecimal moyenneRapidePremierePeriodeIntervalleRapide = moyenneClosingPrices(
				listPeriods.subList(0, intervalleMoyenneRapide));
		premierePeriodeIntervalleRapide.setMoyenneRapide(moyenneRapidePremierePeriodeIntervalleRapide);

		TradingPeriod currentPeriod;
		TradingPeriod previousPeriod;
		BigDecimal currentMoyenneRapide;
		for (int i = intervalleMoyenneRapide; i < listPeriods.size(); i++) {
			/*
			 * BigDecimal est immutable, je suis obligé de créer une nouvelle
			 * instace à chaque fois ;-)
			 */
			currentPeriod = listPeriods.get(i);
			previousPeriod = listPeriods.get(i - 1);
			currentMoyenneRapide = currentPeriod.getClosingPrice().multiply(kRapide)
					.add(previousPeriod.getMoyenneRapide().multiply(BigDecimal.ONE.subtract(kRapide)))
					.setScale(10, RoundingMode.HALF_EVEN);
			currentPeriod.setMoyenneRapide(currentMoyenneRapide);
		}
	}
	
	private void alimenteMoyennesLentesEtMACD(List<TradingPeriod> listPeriods) {
		
		/* première valeur */
		TradingPeriod premierePeriodeIntervalleLent = listPeriods.get(intervalleMoyenneLente - 1);
		BigDecimal moyenneLentePremierePeriodeIntervalleLent = moyenneClosingPrices(
				listPeriods.subList(0, intervalleMoyenneLente));
		premierePeriodeIntervalleLent.setMoyenneLente(moyenneLentePremierePeriodeIntervalleLent);

		TradingPeriod currentPeriod;
		TradingPeriod previousPeriod;
		BigDecimal currentMoyenneLente;
		BigDecimal macd;
		for (int i = intervalleMoyenneLente; i < listPeriods.size(); i++) {
			/*
			 * BigDecimal est immutable, je suis obligé de créer une nouvelle
			 * instace à chaque fois ;-)
			 */
			currentPeriod = listPeriods.get(i);
			previousPeriod = listPeriods.get(i - 1);
			currentMoyenneLente = currentPeriod.getClosingPrice().multiply(kLent)
					.add(previousPeriod.getMoyenneLente().multiply(BigDecimal.ONE.subtract(kLent)))
					.setScale(10, RoundingMode.HALF_EVEN);
			currentPeriod.setMoyenneLente(currentMoyenneLente);
			
			//MACD : invariant dans le bean
		}
		
	}
	
	private void alimenteMoyennesSignal(List<TradingPeriod> listPeriods) {
		
		TradingPeriod currentPeriod;
		BigDecimal moyenneSignal;
		for (int i = intervalleMoyenneLente + intervalleSignal; i < listPeriods.size(); i++) {
			
			currentPeriod = listPeriods.get(i);
			moyenneSignal = moyenneMacd(
					listPeriods.subList(i - intervalleSignal + 1, i + 1));// [27, 36[
			currentPeriod.setSignal(moyenneSignal);
			
		}
	}

	private BigDecimal moyenneClosingPrices(List<TradingPeriod> listPeriods) {

		// TODO checks...

		BigDecimal somme = listPeriods.stream().map(TradingPeriod::getClosingPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add).setScale(10, RoundingMode.HALF_EVEN);

		return somme.divide(BigDecimal.valueOf(listPeriods.size()), 10, RoundingMode.HALF_EVEN);
	}
	
	private BigDecimal moyenneMacd(List<TradingPeriod> listPeriods) {

		// TODO checks...

		BigDecimal somme = listPeriods.stream().map(TradingPeriod::getMacd).reduce(BigDecimal.ZERO,
				BigDecimal::add).setScale(10, RoundingMode.HALF_EVEN);

		return somme.divide(BigDecimal.valueOf(listPeriods.size()), 10, RoundingMode.HALF_EVEN);
	}

	public boolean isBullish() {
		return bullish;
	}
	public double getAcceleration() {
		return acceleration;
	}

	@Override
	public String toString() {
		return this.currencyPair + "|" + this.dureeUnitaire + "|" + this.intervalleMoyenneLente
				+ "|" + this.intervalleMoyenneRapide + "|" + this.intervalleSignal;
	}

	//TODO equals et hashcode
	
}
