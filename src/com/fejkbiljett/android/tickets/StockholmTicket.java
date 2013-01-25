package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class StockholmTicket extends Ticket {
	private int[] gNumbers = new int[4];

	private int[] gPrices = new int[] { 36, 54, 72, 36 };

	private double gPriceMod = 0.55;

	protected String mSender;

	protected boolean mReduced;

	protected String mZones = "", mTimeNow, mTime, mDay, mDate, mMonth,
			sPriceText, sPriceType;

	protected int iPrice;

	@Override
	public String getMessage() {
		return sPriceType
				+ "-"
				+ mZones
				+ " "
				+ mTime
				+ " "
				+ gNumbers[2]
				+ gNumbers[3]
				+ "\n\n"
				// + "+'" + Utils.generateRandomString() + "'+\n"
				// + "+'" + Utils.generateRandomString() + "'+\n"
				// + "+'" + Utils.generateRandomString() + "'+\n"
				+ "SL biljett giltig till " + mTime + " " + mDate + "\n"
				+ sPriceText + " " + iPrice + " kr ink 6% moms\n" + mTimeNow
				+ mMonth + mDay + gNumbers[2] + (gNumbers[0] - 3) + gNumbers[3]
				+ Utils.generateRandomString(7, true) + "\n"
				+ "http://mobil.sl.se \n\n"
				+ "1 februari ändras sms-biljetten \n\n"
				+ "Från den 1 februari köper du din sms-biljett på ett nytt nummer. \n"
				+ "Det nya numret är 076-7201010 \n\n"
				+ "Innan ditt första köp på det nya numret måste du registrera dig \n"
				+ "på sl.se/sms. Sedan beställer du precis som vanligt. Ändringen \n"
				+ "görs för att leva upp till nya regler och ett nytt system. \n\n"
				+ "Läs gärna mer på http://sl.se Tack!";
				
				
				
	}

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return (mReduced ? "R" : "H") + mZones;
	}

	@Override
	public String getNumberOut() {
		return "72150";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		mSender = generateSenderNumber();

		Calendar cal = Calendar.getInstance();
		Date now = new Date();

		mReduced = data.getBoolean("price_reduced");

		if (data.getBoolean("zone_a"))
			mZones += "A";
		if (data.getBoolean("zone_b"))
			mZones += "B";
		if (data.getBoolean("zone_c"))
			mZones += "C";
		if (data.getBoolean("zone_l"))
			mZones += "L";

		if (mZones.contentEquals("")) {
			throw new TicketException("Fyll i vilka zoner du vill åka i.");
		} else if (mZones.startsWith("AC")) {
			mZones = "ABC" + (data.getBoolean("zone_l") ? "L" : null);
		}

		if (mZones.equals("AL") || mZones.equals("BL") || mZones.equals("ABL")) {
			throw new TicketException(
					"Ogiltig kombination med Länsgränspassage.");
		}

		boolean twohours = mZones.startsWith("ABC") || mZones.equals("L");

		// Date and time
		mTimeNow = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());
		mMonth = Utils.gAlphabet[cal.get(Calendar.MONTH)];

		cal.add(Calendar.MINUTE, (twohours ? 120 : 75));
		mTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		if (mZones.equals("L")) {
			iPrice = gPrices[3];
		} else if (data.getBoolean("zone_l")) {
			iPrice = gPrices[mZones.length() - 2] + gPrices[3];
		} else {
			iPrice = gPrices[mZones.length() - 1];
		}

		if (mReduced) {
			sPriceType = "R";
			sPriceText = "RED PRIS";
			iPrice = (int) Math.ceil(iPrice * gPriceMod);
		} else {
			sPriceType = "H";
			sPriceText = "Helt pris";
		}
	}

	private String generateSenderNumber() {
		gNumbers[0] = (int) Math.round(Math.random() * 6) + 3;
		String number = getNumberOut() + gNumbers[0];

		for (int i = 1; i <= 3; i++) {
			gNumbers[i] = (int) Math.round(Math.random() * 9);
			number += gNumbers[i];
		}

		return number;
	}
}