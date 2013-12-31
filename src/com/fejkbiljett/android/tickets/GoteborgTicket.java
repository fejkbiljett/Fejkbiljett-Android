package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class GoteborgTicket extends Ticket {
	private int[] gNumbers = new int[4];

	final char[] aChars = { '+', '-', '/', '*' };

	protected String mSender;

	protected String sZone, sPrice, sPriceType, sValidTime, sValidDate, sTime,
			sMonth, sDay, sCode = "";

	protected boolean bReduced;

	@Override
	public String getMessage() {
		return ""
				+ gNumbers[2]
				+ gNumbers[3]
				+ aChars[(int) Math.floor(Math.random() * 4)]
				+ " "
				+ "--VÄSTTRAFIK-- giltig till "
				+ sValidTime
				+ " "
				+ sValidDate
				+ "\n"
				+ sPriceType
				+ " GÖTEBORG"
				+ (!sZone.equals("") ? " " + sZone : " ")
				+ (sZone.equals("") ? "inom Göteborgs kommun"
						: " inom området Göteborg " + sZone) + "\n" + sPrice
				+ "kr (inkl.6% moms) " + sTime + sMonth + sDay + sCode;
	}

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return "G" + sZone.replace("+", "P") + (bReduced ? "S" : "V");
	}

	@Override
	public String getNumberOut() {
		return "72450";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		mSender = generateSenderNumber();

		Calendar cal = Calendar.getInstance();
		Date now = new Date();

		sZone = data.getString("zone");

		bReduced = data.getBoolean("price_reduced");

		if (bReduced) {
			sPriceType = "SKOLUNGDOM";
			if (sZone.equals(""))
				sPrice = "17,00";
			else if (sZone.equals("+"))
				sPrice = "33,00";
			else if (sZone.equals("++"))
				sPrice = "41,00";
			else
				sPrice = "63,00";
		} else {
			sPriceType = "VUXEN";
			if (sZone.equals(""))
				sPrice = "22,00";
			else if (sZone.equals("+"))
				sPrice = "44,00";
			else if (sZone.equals("++"))
				sPrice = "55,00";
			else
				sPrice = "84,00";
		}

		// Date and time
		sTime = new SimpleDateFormat("HHmm").format(now.getTime());
		sDay = new SimpleDateFormat("dd").format(now.getTime());
		sMonth = String.valueOf( Utils.gAlphabet.toCharArray()[cal.get(Calendar.MONTH)] );

		// Tickets are valid for 90 or 180 minutes
		if (sZone.equals("")) {
			cal.add(Calendar.MINUTE, 90);
		} else {
			cal.add(Calendar.MINUTE, 180);
		}
		sValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		sValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		sCode += Utils.getRandChars(Utils.gAlphabet.toLowerCase(), 3);
		sCode += Utils.generateRandomString(4, false);
		sCode += Utils.getRandChars(Utils.gAlphabet, 1);
		sCode += Integer
				.toString((int) ((Math.random() * (996871 - 962139)) + 962139));
	}

	private String generateSenderNumber() {
		String number = "72450";

		number += Utils.generateRandomString(4, false);
		
		return number;
	}
}