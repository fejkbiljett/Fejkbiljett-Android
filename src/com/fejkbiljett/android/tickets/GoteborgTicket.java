package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class GoteborgTicket extends Ticket {
	final String zones[] = {"gbg", "gbg+", "gbg++", "gbg+++", "kalv"};
	final int    price[] = {25,    49,      60,      93,      25};
	final int redprice[] = {19,    37,      46,      70,      10};  

	final String aChars = "+-/*";

	protected String mSender, myNumber;

	protected String sZone, sPrice, sPriceType, sPriceStr, sValidTime, sValidDate, sTime,
			sMonth, sDay, sCode = "";

	protected boolean bReduced;

	@Override
	public String getMessage() {
		return mSender.substring(mSender.length()-2) + Utils.getRandChars(aChars, 1) + " "
				+ "--VÄSTTRAFIK-- giltig till "	+ sValidTime + " " + sValidDate + "\n"
				+ sPriceType + " " + sPriceStr + "\n"
				+ sPrice + "kr (inkl.6% moms) " + sTime + sMonth + sDay + sCode;
	}

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		String message = "";
		if(sZone.startsWith("gbg")) {
			int plus = sZone.length()-"gbg".length();
			message = "G" + new String(new char[plus]).replace("\0", "P");
		} else if(sZone.startsWith("kalv"))
		{
			message = "KU";
		}
		message += (bReduced ? "S" : "V");
		return message;
	}

	@Override
	public String getNumberOut() {
		return "0707262728";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();

		myNumber = data.getString("my_number");
		sZone = data.getString("zone");
		bReduced = data.getBoolean("price_reduced");

		for(int i=0; i<zones.length; i++) {
			if (sZone.equals(zones[i]))
			{
				if (bReduced) {
					sPriceType = "SKOLUNGDOM";
					sPrice = String.valueOf(redprice[i]);
					sCode = Utils.getRandChars("veu", 1); //fixme!
				}
				else {
					sPriceType = "VUXEN";
					sPrice = String.valueOf(price[i]);
					sCode = Utils.getRandChars("veu", 1);
				}
			}
		}
		if(sZone.startsWith("gbg")) {
			sPriceType += " GÖTEBORG";
			int plus = sZone.length()-"gbg".length();
			sPriceStr = "inom området Göteborg";
			switch(plus)
			{
				case 0:
					sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("tT", 1)+sCode;
					break;
				default:
					sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("sS", 1)+sCode;
					String pluses = new String(new char[plus]).replace("\0", "+");
					sPriceStr =  pluses + " " + sPriceStr.replace("området ", "") + "s kommun " + pluses;	
					break;
			}
		}
		else if(sZone.startsWith("kalv")) {
			sPriceType += " KUNGÄLV";
			sPriceStr = "tätort";
			sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("äÄ", 1)+sCode;
		}
			
		// Date and time
		sTime = new SimpleDateFormat("HHmm").format(now.getTime());
		sDay = new SimpleDateFormat("dd").format(now.getTime());
		sMonth = String.valueOf( Utils.gAlphabet.toCharArray()[cal.get(Calendar.MONTH)] );

		// Tickets are valid for 90 or 180 minutes
		if (sZone.endsWith("+")) {
			cal.add(Calendar.MINUTE, 180);
		} else {
			cal.add(Calendar.MINUTE, 90);
		}
		sValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		sValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		String seconds = String.valueOf(now.getTime()/1000);
		
		sCode += myNumber.substring(myNumber.length()-3);
		sCode += "9Q"; //was this at 2014-01-08
		sCode += seconds.substring(seconds.length()-6); //this is a moving number, this is incorrect but follows a good pattern
		
		mSender = generateSenderNumber();
	}

	private String generateSenderNumber() {
		String number = "72450";
		number += "0";
		number += Utils.getRandChars(Utils.gNumbers, 1);
		number += Integer.valueOf(sTime.substring(2))+Integer.valueOf(Utils.getRandChars("123456", 1)); //The magic number starting the text is usually time sent + 1-6
		
		return number;
	}
}