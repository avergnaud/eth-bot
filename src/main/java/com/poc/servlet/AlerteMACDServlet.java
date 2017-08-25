package com.poc.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import javax.json.Json;
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

@WebServlet("/AlerteMACD")
public class AlerteMACDServlet extends HttpServlet {

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
		
		// "refresh" sans appel à kraken, cf OHLC cache
		macd.refresh();
		
		JsonObject sortie = Json.createObjectBuilder()
				.add("isBullish", macd.isBullish())
				.add("acceleration", macd.getAcceleration())
				.build();
		
		JsonWriterFactory writerFactory = Json
				.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
		
		StringWriter sw = new StringWriter();
		JsonWriter writer = writerFactory.createWriter(sw);
		writer.writeObject(sortie);
		writer.close();

		response.getWriter().write(sw.toString());
		
	}

}