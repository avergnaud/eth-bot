package com.poc.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import tmp.KrakenPublicRequest;

@WebServlet("/OHLC")
public class OHLCServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		JsonObject jsonObject = new KrakenPublicRequest().queryPublic("OHLC", "pair=XETHZEUR&interval=1440");// TODO
																												// :
																												// param

		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
		NumberFormat nf = DecimalFormat.getInstance(Locale.FRANCE);

		JsonArray arr = jsonObject.getJsonObject("result").getJsonArray("XETHZEUR");// TODO
																					// :
																					// param
		try(PrintWriter pw = new PrintWriter("out.csv")) {
			
			for (JsonArray jv : arr.getValuesAs(JsonArray.class)) {
				
				long ts = jv.getJsonNumber(0).longValue() *1000;
				LocalDate date = Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDate();
				String formattedDate = dtf.format(date);
				
				Number n = nf.parse(jv.getString(4));
//				double d = n.doubleValue();
				
				pw.println(formattedDate + "," + n);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		// TODO : cache + cron
		JsonObject jsonObject = new KrakenPublicRequest().queryPublic("OHLC", "pair=XETHZEUR&interval=1440");// TODO
																												// :
																												// param

		JsonArray arr = jsonObject.getJsonObject("result").getJsonArray("XETHZEUR");// TODO
																					// :
																					// param
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