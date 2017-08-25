package com.poc.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poc.macd.MACDBuilder;
import com.poc.ohlc.OHLC;
import com.poc.ohlc.OHLCCache;
import com.poc.ohlc.OHLCCacheManager;

@WebServlet("/OHLC")
public class OHLCServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	{OHLCCacheManager.INSTANCE.start();}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		String grainString = req.getParameter("grain");

		//cache
		new MACDBuilder().setCurrencyPair("XETHZEUR")
				.setDureeUnitaire(Integer.parseInt(grainString))
				.setIntervalleMoyenneRapide(12)
				.setIntervalleMoyenneLente(26)
				.setIntervalleSignal(9)
				.build();
		
		// TODO ?
		OHLC ohlc = new OHLC("XETHZEUR", Integer.parseInt(grainString));//TODO
		JsonObject jsonObject = OHLCCache.INSTANCE.get(ohlc);
//		JsonObject jsonObject = new KrakenPublicRequest().queryPublic("OHLC", "pair=XETHZEUR&interval=" + grainString);// TODO

		JsonArray arr = jsonObject.getJsonObject("result").getJsonArray("XETHZEUR");// TODO
		int taille = arr.size();
		List<JsonValue> liste = arr.subList(taille - 50, taille);// TODO : param

		JsonArray arr2 = createJsonArrayFromList(liste);

		JsonWriterFactory writerFactory = Json
				.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));

		JsonArray expected2 = Json.createArrayBuilder().add(Json.createObjectBuilder().add("values", arr2)).build();

		StringWriter sw = new StringWriter();
		JsonWriter writer = writerFactory.createWriter(sw);
		writer.writeArray(expected2);
		writer.close();

		response.getWriter().write(sw.toString());
	}

	private JsonArray createJsonArrayFromList(List<JsonValue> list) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (JsonValue value : list) {
			JsonArray jsonArray = (JsonArray) value;
			jsonArrayBuilder.add(Json.createObjectBuilder().add("date", jsonArray.get(0)).add("open", jsonArray.get(1))
					.add("high", jsonArray.get(2)).add("low", jsonArray.get(3)).add("close", jsonArray.get(4))
					.add("volume", jsonArray.get(6)).add("adjusted", jsonArray.get(5)));
		}

		return jsonArrayBuilder.build();
	}

}