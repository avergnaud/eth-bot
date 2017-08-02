package com.poc.servlet;

import java.io.IOException;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmp.KrakenPublicRequest;

@WebServlet("/Hello")
public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		// TODO : cache + cron
		JsonObject jsonObject = new KrakenPublicRequest().queryPublic("OHLC", "pair=XETHZEUR&interval=240");// TODO : param

		JsonArray arr = jsonObject.getJsonObject("result")
				 .getJsonArray("XETHZEUR");// TODO : param
		int taille = arr.size();
		List<JsonValue> arr2 = arr.subList(taille - 20, taille);// TODO : param
		
		response.getWriter().write(arr2.toString());
	}


}