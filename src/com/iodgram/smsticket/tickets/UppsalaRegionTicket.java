package com.iodgram.smsticket.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.iodgram.smsticket.R;
import com.iodgram.smsticket.Utils;

public class UppsalaRegionTicket extends Ticket {
	private int[] gNumbers = new int[4];

	final char[] aChars = { '+', '-', '/', '*' };

	int mZone;
	boolean bReduced;

	String mSender;

	String mValidDate, mValidTime;
	String mTime, mMonth, mDay;
	String mCode = "";
	String mPriceType, mZoneOut, mZoneType = "UL";
	int mPrice;

	@Override
	public String getMessage() {
		return "" + gNumbers[1] + gNumbers[2]
				+ aChars[(int) Math.floor(Math.random() * 4)] + " "
				+ mPriceType + " " + mZoneType + ". " + "Giltig till "
				+ mValidDate + " kl. " + mValidTime + ". " + "Pris: " + mPrice
				+ " kr (inkl. 6% moms) " + mTime + mMonth + mDay + mCode;
	}

	@Override
	public String getSender() {
		return mSender;
	}

	@Override
	public String getMessageOut() {
		if (mZone == R.id.region_zone_ulsl) {
			return "ULSL" + (bReduced ? "U" : "V");
		} else if (mZone == R.id.region_zone_bike) {
			return "ULCY";
		} else {
			return "UL" + (bReduced ? "U" : "V") + mZoneOut;
		}
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

		bReduced = data.getBoolean("price_reduced");
		mZone = data.getInt("zone");

		if (mZone == R.id.region_zone_bike) {
			mPriceType = "CYKEL";
		} else if (bReduced) {
			mPriceType = "UNGDOM";
		} else {
			mPriceType = "VUXEN";
		}
		
		int iExpireTime = 90;

		switch (mZone) {
		case R.id.region_zone_urban:
			mZoneType = "Tätortstrafik";
			mPrice = bReduced ? 12 : 20;
			mZoneOut = "T";
			break;
		case R.id.region_zone_1:
			mZoneType = "UL resa i zon 1";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "1";
			break;
		case R.id.region_zone_2:
			mZoneType = "UL resa i zon 2";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "2";
			break;
		case R.id.region_zone_2p:
			mZoneType = "UL resa mellan zon 2 och PLUS";
			mPrice = bReduced ? 54 : 90;
			mZoneOut = "2P";
			break;
		case R.id.region_zone_12:
			mZoneType = "UL resa mellan zon 1 och 2";
			mPrice = bReduced ? 54 : 90;
			mZoneOut = "12";
			break;
		case R.id.region_zone_12p:
			mZoneType = "UL resa mellan zon 1, 2 och PLUS";
			mPrice = bReduced ? 81 : 135;
			mZoneOut = "12P";
			break;
		case R.id.region_zone_p:
			mZoneType = "UL resa i zon PLUS";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "P";
			break;
		case R.id.region_zone_ulsl:
			mZoneType = "UL/SL resa med hela UL och SL";
			mPrice = bReduced ? 60 : 100;
			iExpireTime = 120;
			break;
		case R.id.region_zone_bike:
			mZoneType = "UL";
			mPrice = 45;
			break;
		}

		// Date and time
		mTime = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());
		mMonth = Utils.gAlphabet[cal.get(Calendar.MONTH)];

		cal.add(Calendar.MINUTE, iExpireTime);
		mValidTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
		mValidDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		for (int i = 0; i < 2; i++) {
			mCode += Utils.gAlphabet[(int) Math.floor(Math.random() * 26)]
					.toLowerCase();
		}
		for (int i = 0; i < 3; i++) {
			mCode += Math.round(Math.random() * 9);
		}
		mCode += Integer.toString((int) (Math.random() * 9))
				+ (char) ((Math.random() * 26) + 65);
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