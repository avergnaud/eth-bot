package com.poc.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poc.macd.MACD;
import com.poc.macd.MACDBuilder;
import com.poc.macd.TradingPeriod;

@WebServlet("/MACD")
public class MACDServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		String grainString = req.getParameter("grain");
		Integer grain = Integer.parseInt(grainString);
		
		MACD macd = new MACDBuilder().setCurrencyPair("XETHZEUR")
				.setDureeUnitaire(grain)
				.setIntervalleMoyenneRapide(12)
				.setIntervalleMoyenneLente(26)
				.setIntervalleSignal(9)
				.build();
		
		// TODO ? gérer ici aussi avec un cron ?
		macd.refresh();
		List<TradingPeriod> liste = macd.getListPeriods();
		
		int taille = liste.size();
		List<TradingPeriod> listeRaccourcie = liste.subList(taille - 50, taille);// TODO : param
		
		JsonObject courbeMacd = Json.createObjectBuilder()
				.add("values", createMACDValues(listeRaccourcie))
				.add("key", "MACD")
				.add("color", "green")
				.build();
		JsonObject courbeSignal = Json.createObjectBuilder()
				.add("values", createSignalValues(listeRaccourcie))
				.add("key", "SIGNAL")
				.add("color", "red")
				.build();
		JsonArray sortie = Json.createArrayBuilder().add(courbeMacd).add(courbeSignal).build();
		
		JsonWriterFactory writerFactory = Json
				.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
		
		StringWriter sw = new StringWriter();
		JsonWriter writer = writerFactory.createWriter(sw);
		writer.writeArray(sortie);
		writer.close();

		response.getWriter().write(sw.toString());
		
	}

	private JsonArray createMACDValues(List<TradingPeriod> list) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (TradingPeriod tradingPeriod : list) {
			jsonArrayBuilder.add(Json.createObjectBuilder().add("x", tradingPeriod.getClosingTimeEpochTimestamp())
					.add("y", tradingPeriod.getMacd()));
		}
		return jsonArrayBuilder.build();
	}

	private JsonArray createSignalValues(List<TradingPeriod> list) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (TradingPeriod tradingPeriod : list) {
			jsonArrayBuilder.add(Json.createObjectBuilder().add("x", tradingPeriod.getClosingTimeEpochTimestamp())
					.add("y", tradingPeriod.getSignal()));
		}
		return jsonArrayBuilder.build();
	}

}