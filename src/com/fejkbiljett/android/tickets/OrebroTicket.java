package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class OrebroTicket extends Ticket {
	protected String mSender, uCode, uCodeTail, uScanCode= "";;
	
	
	protected String mPriceType, mValidDate, mValidTime = "";
	protected int mPrice;

	protected boolean mReduced;

	
	
	@Override
	public String getMessage() {
		return  uCodeTail + " LTÖ\n\n"
				
				+ "Ö" + (mReduced ? "S" : "V") + " " + mPriceType + " "
				+ "Giltig till " + mValidTime + " " + mValidDate + "\n"
				+ "Örebro" + "\n\n"
				
				+ mPrice + " SEK (6% MOMS) " + uCode + "\n\n"
				
				+ uScanCode;
	}
	

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return "Ö" + (mReduced ? "S" : "V");
	}

	@Override
	public String getNumberOut() {
		return "0762778000";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		Calendar cal = Calendar.getInstance();
		
		mReduced = data.getBoolean("price_reduced");

		if (mReduced) {
			mPriceType = "SKOLUNGDOM";
			mPrice = 10;
		} else {
			mPriceType = "VUXEN";
			mPrice = 21;
		}

		cal.add(Calendar.MINUTE, 180);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		uCode = Utils.generateNumberSequence();
		uScanCode = Utils.aeoxScannerBlock(uCode);
		mSender = generateSenderNumber();
	}
	private String generateSenderNumber() {
		uCodeTail = uCode.substring(uCode.length()-3);
		return "LTO" + uCodeTail;
	}
}