package com.poc.macd;

import java.math.BigDecimal;

public class TradingPeriod {
	
	private long closingTimeEpochTimestamp;
	private BigDecimal closingPrice;
	private BigDecimal moyenneRapide;
	private BigDecimal moyenneLente;
	private BigDecimal macd;
	private BigDecimal signal;
	
	public TradingPeriod(long closingTimeEpochTimestamp, BigDecimal closingPrice) {
		this.closingTimeEpochTimestamp = closingTimeEpochTimestamp;
		this.closingPrice = closingPrice;
	}

	public BigDecimal getMoyenneRapide() {
		return moyenneRapide;
	}

	public void setMoyenneRapide(BigDecimal moyenneRapide) {
		this.moyenneRapide = moyenneRapide;
	}

	public BigDecimal getMoyenneLente() {
		return moyenneLente;
	}

	public void setMoyenneLente(BigDecimal moyenneLente) {
		this.moyenneLente = moyenneLente;
		
		//MACD
		this.macd = this.moyenneRapide.subtract(this.moyenneLente);
	}

	public BigDecimal getSignal() {
		return signal;
	}

	public void setSignal(BigDecimal signal) {
		this.signal = signal;
	}

	public long getClosingTimeEpochTimestamp() {
		return closingTimeEpochTimestamp;
	}

	public BigDecimal getClosingPrice() {
		return closingPrice;
	}

	public BigDecimal getMacd() {
		return macd;
	}
	
	
}
