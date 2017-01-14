package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class StockholmTicket extends Ticket {
	private int[] gPrices = new int[] { 43, 29 };

	protected String mSender;

	protected boolean mReduced;

	protected String mZones = "", mTimeNow, mTime, mDay, mDate,
			sPriceText, sPriceType;
	protected String sCode, sCodeTail, sScanCode = "";

	protected int iPrice;

	@Override
	public String getMessage() {
		return
				sPriceType + mZones + " " + mTime + " " + sCodeTail + "\n\n"

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
		return (mReduced ? "rab" : "vux");
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

		boolean twohours = false;

		// Date and time
		mTimeNow = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());

		cal.add(Calendar.MINUTE, (twohours ? 120 : 75));
		mTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		iPrice = gPrices[0];
		
		if (mReduced) {
			sPriceType = "RAB";
			sPriceText = "Rabbaterad";
			iPrice = gPrices[1];
		} else {
			sPriceType = "VUX";
			sPriceText = "Vuxen";
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
