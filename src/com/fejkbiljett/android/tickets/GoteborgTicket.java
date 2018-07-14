package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class GoteborgTicket extends Ticket {
	final String zones[] = {"gbg", "gbg+", "gbg++", "gbg+++", "kalv"};
	final int    price[] = {26,    50,      62,      96,      26};
	final int redprice[] = {20,    39,      47,      72,      20};  
	
	final String unknownCode[] = { 	"2T", //2015-11-21
									"4U", //2014-01-11 
									"9Q", //2014-01-08
									"4B", //2013-12-26 
									"7D", //2013-12-23, 2015-04-07
									"6M", //2011-06-30
									"3V", //2011-06-04
									"5V", //2011-05-16
									"1S", //2011-04-23
									"7Y"  //2011-04-22
							     };


	final String aChars = "+-/*";

	protected String mSender, lastThree;

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

		lastThree = data.getString("last_three");
		sZone = data.getString("zone");
		bReduced = data.getBoolean("price_reduced");
		
		if(lastThree.length()!=3)
		{
			lastThree = Utils.getRandChars(Utils.gNumbers, 3);
		}

		for(int i=0; i<zones.length; i++) {
			if (sZone.equals(zones[i]))
			{
				if (bReduced) {
					sPriceType = "SKOLUNGDOM";
					sPrice = String.format(new Locale("sv"), "%.2f", (float) redprice[i]);
					sCode = Utils.getRandChars("smd", 1);
				}
				else {
					sPriceType = "VUXEN";
					sPrice = String.format(new Locale("sv"), "%.2f", (float) price[i]);
					sCode = Utils.getRandChars("veu", 1);
				}
			}
		}
		if(sZone.startsWith("gbg")) {
			sPriceType += " GÖTEBORG";
			int plus = sZone.length()-"gbg".length();
			sPriceStr = "inom området Göteborg";
			String pluses = new String(new char[plus]).replace("\0", "+");
			switch(plus)
			{
				case 0:
					sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("tT", 1) + sCode;
					break;
				case 1:
					sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("sS", 1) + sCode;
					sPriceStr =  pluses + " " + sPriceStr + " " + pluses;
					break;
				case 2:
					sCode = Utils.getRandChars("pP", 1) + Utils.getRandChars("lL", 1) + sCode; //As of a real ticket 2015-04-07
					sPriceStr =  pluses + " " + sPriceStr + " " + pluses;
					break;
				case 3:
					//sCode = Utils.getRandChars("uU", 1) + Utils.getRandChars("sS", 1) + sCode;
					sCode = Utils.getRandChars("pP", 1) + Utils.getRandChars("uUpP", 1) + sCode; //As of real reduced tickets 2015-11-21
					sPriceStr =  pluses + " " + sPriceStr + " " + pluses;
					break;
				default:
					//nothing
					break;
						
			}
		}
		else if(sZone.startsWith("kalv")) {
			sPriceType += " KUNGÄLV";
			sPriceStr = "tätort";
			sCode = Utils.getRandChars("gG", 1) + Utils.getRandChars("äÄ", 1) + sCode;
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
		
		sCode += lastThree.substring(lastThree.length()-3);
		sCode += unknownCode[Integer.valueOf(seconds.substring(seconds.length()-8, seconds.length()-6))%unknownCode.length];
		sCode += seconds.substring(seconds.length()-6); //this is a moving number, this is incorrect but follows a good pattern
		
		mSender = generateSenderNumber();
	}

	private String generateSenderNumber() {
		String number = "VT";
		number += Utils.getRandChars(Utils.gNumbers, 4);
		number += String.format("%02d", Integer.valueOf(sTime.substring(2))+Integer.valueOf(Utils.getRandChars("123456", 1))); //The magic number starting the text is usually time sent + 1-6
		
		return number;
	}
}
