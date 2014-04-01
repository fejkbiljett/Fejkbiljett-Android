package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class VasterasTicket extends Ticket {
	protected String mSender, uCode, uCodeTail, uScanCode= "";;
	
	
	protected String mPriceType, mValidDate, mValidTime = "";
	protected int mPrice;

	protected boolean mReduced;

	
	
	@Override
	public String getMessage() {
		return  uCodeTail + " VL\n\n"
				
				+ "V" + (mReduced ? "S" : "V") + " " + mPriceType + " "
				+ "Giltig till " + mValidTime + " " + mValidDate + "\n"
				+ "Västerås" + "\n\n"
				
				+ mPrice + " SEK (6% MOMS)\n" + uCode + "\n\n"
				
				+ uScanCode;
	}
	

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		return "V" + (mReduced ? "S" : "V");
	}

	@Override
	public String getNumberOut() {
		return "0739304050";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		Calendar cal = Calendar.getInstance();
		
		mReduced = data.getBoolean("price_reduced");

		if (mReduced) {
			mPriceType = "SKOLUNGDOM";
			mPrice = 12;
		} else {
			mPriceType = "VUXEN";
			mPrice = 25;
		}

		cal.add(Calendar.MINUTE, 60);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		uCode = Utils.generateNumberSequence();
		uScanCode = Utils.aeoxScannerBlock(uCode);
		mSender = generateSenderNumber();
	}
	private String generateSenderNumber() {
		uCodeTail = uCode.substring(uCode.length()-3);
		return "VL" + uCodeTail;
	}
}