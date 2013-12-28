package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class JonkopingTicket extends Ticket {
	private int[] gNumbers = new int[3];

	protected String mSender, uCode = "";
	
	
	protected String mPriceType, mValidDate, mValidTime, mPrice = "";

	protected boolean mReduced;
	protected int mZones;
	
	/*
	static protected int [] mReducedPrices = { 16, 22, 31,  41, 50, 59, 68 };
	static protected int [] mPrices = { 26, 37, 52, 67, 83, 98, 114 };
	
	static protected double mDiscount = 0.1;
	*/
	
	@Override
	public String getMessage() {
		return "" + gNumbers[0] + gNumbers[1] + gNumbers[2] + " JLT\n\n"
				
				+ "Giltig till kl " 	+ mValidTime + ", " + mValidDate + "\n"
				+ mPriceType + " JÖNKÖPING Tätortstrafik\n" 
				+ mPrice + " SEK (6% MOMS)\n"
				+ uCode + "\n";
	}
	

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return "J" + (mReduced ? "S" : "V");
	}

	@Override
	public String getNumberOut() {
		return "72344";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		mSender = generateSenderNumber();

		Calendar cal = Calendar.getInstance();

		mReduced = data.getBoolean("price_reduced");
		mZones = data.getInt("zones");

		if (mReduced) {
			mPriceType = "SKOLUNGDOM";
			//mPrice = String.format("%.2f", mReducedPrices[mZones-1]*(1-mDiscount));
			mPrice = "16";
		} else {
			mPriceType = "VUXEN";
			//mPrice = String.format("%.2f", mPrices[mZones-1]*(1-mDiscount));
			mPrice = "26";
		}

		cal.add(Calendar.MINUTE, 90);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("dd MMM yy").format(cal.getTime()).toLowerCase();

		uCode = Utils.getRandChars("123456789ABCDEFGHJKLMNPQRSTUVWX", 9);
	}

	private String generateSenderNumber() {
		String number = "72344";

		for (int i = 0; i < 3; i++) {
			gNumbers[i] = (int) Math.round(Math.random() * 9);
			number += gNumbers[i];
		}
		
	return number;
	}
}