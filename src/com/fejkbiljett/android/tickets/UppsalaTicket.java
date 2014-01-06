package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class UppsalaTicket extends Ticket {
	protected String mSender, uCode, uCodeTail, uScanCode= "";;
	
	
	protected String mPriceType, mValidDate, mValidTime = "";
	protected int mPrice;

	protected boolean mReduced;

	
	
	@Override
	public String getMessage() {
		return  uCodeTail + " UL\n\n"
				
				+ "U" + (mReduced ? "U" : "V") + " " + mPriceType + " "
				+ "Giltig till " + mValidTime + " " + mValidDate + "\n"
				+ "Stadsbuss" + "\n\n"
				
				+ mPrice + " SEK (6% MOMS) " + uCode + "\n\n"
				
				+ uScanCode;
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

		cal.add(Calendar.MINUTE, 90);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		uCode = Utils.generateNumberSequence();
		uScanCode = Utils.aeoxScannerBlock(uCode);
		mSender = generateSenderNumber();
	}
	private String generateSenderNumber() {
		uCodeTail = uCode.substring(uCode.length()-3);
		return "UL" + uCodeTail;
	}
}