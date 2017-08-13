package com.poc.servlet.macd;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poc.data.MACDCache;
import com.poc.data.MACDCacheManager;
import com.poc.macd.MACD;
import com.poc.macd.MACDBuilder;
import com.poc.slackbot.MacdWatcher;

/*
http://localhost:8181/SlackBotCreator?pair=XETHZEUR&dureeUnitaireEnMinutes=1&intervalleMoyenneLente=26&intervalleMoyenneRapide=12&intervalleSignal=9
 */

@WebServlet("/SlackBotCreator")
public class SlackBotCreatorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type
	      response.setContentType("text/html");

		
		String pair = req.getParameter("pair");//XETHZEUR
		String dureeUnitaireEnMinutes = req.getParameter("dureeUnitaireEnMinutes");//5 TODO check
		String intervalleMoyenneLente = req.getParameter("intervalleMoyenneLente");//26 TODO check
		String intervalleMoyenneRapide = req.getParameter("intervalleMoyenneRapide");//12 TODO
		String intervalleSignal = req.getParameter("intervalleSignal");//9 TODO
		
		MACD macd = new MACDBuilder()
				.setCurrencyPair(pair)
				.setDureeUnitaire(Integer.parseInt(dureeUnitaireEnMinutes))
				.setIntervalleMoyenneLente(Integer.parseInt(intervalleMoyenneLente))
				.setIntervalleMoyenneRapide(Integer.parseInt(intervalleMoyenneRapide))
				.setIntervalleSignal(Integer.parseInt(intervalleSignal))
				.build();
		
		if( MACDCache.INSTANCE.get(macd) == null) {
			MACDCache.INSTANCE.insertOrUpdate(macd);
		}
		
		MACDCacheManager.INSTANCE.refreshAtFixedRate(MACDCache.INSTANCE.get(macd));
		
		MacdWatcher watcher = new MacdWatcher(MACDCache.INSTANCE.get(macd));
		//pour l'instant fait que logger - appellera le bot

	      // Actual logic goes here.
	      PrintWriter out = response.getWriter();
	      out.println("<h1>" + macd.toString() + " watcher created</h1>");
	}


}