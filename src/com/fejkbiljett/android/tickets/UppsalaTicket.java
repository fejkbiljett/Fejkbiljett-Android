package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class UppsalaTicket extends Ticket {
	private int[] gNumbers = new int[3];

	protected String mSender, uCode, uAEOX = "";;
	
	
	protected String mPriceType, mValidDate, mValidTime, mTime, mDay, mCode = "";
	protected int mPrice;

	protected boolean mReduced;

	
	
	@Override
	public String getMessage() {
		return "" + gNumbers[0] + gNumbers[1] + gNumbers[2] + " UL\n\n"
				+ "U" + (mReduced ? "U" : "V") + " " + mPriceType + " " + "Giltig till "
				+ mValidTime + " " + mValidDate + "\n"	+ "Stadsbuss" + "\n\n" + mPrice
				+ " SEK (6% MOMS) "
				+ uCode + "\n\n"
				+ "E" + uAEOX.substring(0, 9) + "\n"
				+ "E" + uAEOX.substring(9, 18) + "\n"
				+ "E" + uAEOX.substring(18, 24) + Utils.getRandChars("AEOX", 3) + "\n"
				+ "EEEEEEEEEE";
	}
	

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return "U" + (mReduced ? "U" : "V");
	}

	@Override
	public String getNumberOut() {
		return "0704202222";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		mSender = generateSenderNumber();

		Calendar cal = Calendar.getInstance();
		Date now = new Date();

		mReduced = data.getBoolean("price_reduced");

		if (mReduced) {
			mPriceType = "UNGDOM";
			mPrice = 15;
		} else {
			mPriceType = "VUXEN";
			mPrice = 25;
		}

		// Date and time
		mTime = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());
		// String sMonth = Utils.gAlphabet[ cal.get(Calendar.MONTH) ];

		cal.add(Calendar.MINUTE, 90);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		uCode = Utils.generateRandomString(12, false)
			+ gNumbers[0] + gNumbers[1] + gNumbers[2];
			String hexCode = Utils.decToHex(uCode);
			uAEOX = Utils.hexToAEOX(hexCode);
	}

	private String generateSenderNumber() {
		String number = "UL";

		for (int i = 0; i < 3; i++) {
			gNumbers[i] = (int) Math.round(Math.random() * 9);
			number += gNumbers[i];
		}
		
	return number;
	}
}