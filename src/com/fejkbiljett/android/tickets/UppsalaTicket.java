package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class UppsalaTicket extends Ticket {
	private int[] gNumbers = new int[4];

	final char[] aChars = { '+', '-', '/', '*' };

	protected String mSender;

	protected String mPriceType, mValidDate, mValidTime, mTime, mDay, mCode = "";
	protected int mPrice;

	protected boolean mReduced;

	@Override
	public String getMessage() {
		return "" + gNumbers[1] + gNumbers[2]
				+ aChars[(int) Math.floor(Math.random() * 4)] + " "
				+ mPriceType + " UPPSALA Stadstrafik. " + "Giltig till "
				+ mValidDate + " kl. " + mValidTime + ". " + "Pris: " + mPrice
				+ " kr (inkl. 6% moms) " + mTime
				+ (Math.random() >= 0.5 ? "e" : "m") + mDay + mCode;
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
		return "72472";
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

		for (int i = 0; i < 2; i++) {
			mCode += Utils.gAlphabet[(int) Math.floor(Math.random() * 26)]
					.toLowerCase();
		}
		for (int i = 0; i < 4; i++) {
			mCode += Math.round(Math.random() * 9);
		}
		mCode += Utils.gAlphabet[(int) Math.floor(Math.random() * 26)];
		for (int i = 0; i < 6; i++) {
			mCode += Math.round(Math.random() * 9);
		}
	}

	private String generateSenderNumber() {
		String number = getNumberOut();

		for (int i = 0; i < 3; i++) {
			gNumbers[i] = (int) Math.round(Math.random() * 9);
			number += gNumbers[i];
		}

		return number;
	}
}