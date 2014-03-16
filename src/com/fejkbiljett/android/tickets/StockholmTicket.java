package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class StockholmTicket extends Ticket {
	private int[] gNumbers = new int[3];

	private int[] gPrices = new int[] { 36, 54, 72, 36 };

	private double gPriceMod = 0.55;

	protected String mSender;

	protected boolean mReduced;

	protected String mZones = "", mTimeNow, mTime, mDay, mDate, mMonth,
			sPriceText, sPriceType;
	protected String sCode, sAEOX = "";

	protected int iPrice;

	@Override
	public String getMessage() {
		return
				sPriceType + "-" + mZones + " " + mTime + " "
				+ gNumbers[0] + gNumbers[1] + gNumbers[2] + "\n\n"

				+ "E" + sAEOX.substring(0, 9) + "\n"
				+ "E" + sAEOX.substring(9, 18) + "\n"
				+ "E" + sAEOX.substring(18, 24) + Utils.getRandChars("AEOX", 3) + "\n"
				+ "EEEEEEEEEE\n\n"
				
				+ "SL biljett giltig till " + mTime + ", " + "\n" + mDate + "\n"
				+ sPriceText + " " + iPrice + " kr ink 6% moms\n"
				
				+ sCode + "\n"
				+ "m.sl.se";
	}

	protected String generateRandomAEOXString()
	{
		String string = "";
		String[] letters = {"A", "E", "O", "X"};

		for (int i = 0; i < 9; i++) {
			string += letters[(int) Math.floor(Math.random() * letters.length)];
		}

		return string;
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
		return "0767201010";
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
			sPriceText = "Red pris";
			iPrice = (int) Math.ceil(iPrice * gPriceMod);
		} else {
			sPriceType = "H";
			sPriceText = "Helt pris";
			
			sCode = Utils.generateRandomString(11, false)
					+ gNumbers[0] + gNumbers[1] + gNumbers[2];
				String hexCode = Utils.decToHex(sCode);
				sAEOX = Utils.hexToAEOX(hexCode);
			
		}
	}

	private String generateSenderNumber() {
		String number = "SL";

		for (int i = 0; i < 3; i++) {
			gNumbers[i] = (int) Math.round(Math.random() * 9);
			number += gNumbers[i];
		}

		return number;
	}
}
