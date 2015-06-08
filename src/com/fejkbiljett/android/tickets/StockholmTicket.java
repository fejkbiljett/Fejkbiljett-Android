package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class StockholmTicket extends Ticket {
	private int[] gPrices = new int[] { 36, 54, 72, 36 };

	private double gPriceMod = 0.55;

	protected String mSender;

	protected boolean mReduced;

	protected String mZones = "", mTimeNow, mTime, mDay, mDate,
			sPriceText, sPriceType;
	protected String sCode, sCodeTail, sScanCode = "";

	protected int iPrice;

	@Override
	public String getMessage() {
		return
				sPriceType + "-" + mZones + " " + mTime + " " + sCodeTail + "\n\n"

				+ sScanCode + "\n\n"
				
				+ "SL biljett giltig till " + mTime + " " + mDate + "\n"
				+ sPriceText + " " + iPrice + " kr ink 6% moms\n"				
				+ sCode.substring(0, 6) + " " + sCode.substring(6) + "\n"
				+ "sl.se";
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
			//Don't do more than the user asks.
			//mZones = "ABC" + (data.getBoolean("zone_l") ? "L" : "");
			throw new TicketException("Felaktig zonkombination.");
		}

		if (mZones.equals("AL") || mZones.equals("BL") || mZones.equals("ABL")) {
			throw new TicketException(
					"Ogiltig kombination med Länsgränspassage.");
		}

		boolean twohours = mZones.startsWith("ABC") || mZones.endsWith("L");

		// Date and time
		mTimeNow = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());

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
		}
		
		sCode = Utils.generateNumberSequence();
		sScanCode = Utils.aeoxScannerBlock(sCode);		
		mSender = generateSenderNumber();
		}

	private String generateSenderNumber() {
		sCodeTail = sCode.substring(sCode.length()-3);
		return "SL" + sCodeTail;
	}
}
