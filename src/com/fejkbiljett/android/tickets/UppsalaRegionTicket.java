package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.fejkbiljett.android.R;
import com.fejkbiljett.android.Utils;

public class UppsalaRegionTicket extends Ticket {
	private int[] gNumbers = new int[3];

	int mZone;
	boolean bReduced;

	String mSender;

	String mValidDate, mValidTime;
	String mTime, mDay;
	String mCode = "";
	String mPriceType, mZoneOut, mZoneType, mZon = "UL";
	String uCode, uAEOX = "";
	int mPrice;

			
	@Override
	public String getMessage() {
		return "" + gNumbers[0] + gNumbers[1] + gNumbers[2] + " UL\n\n"
				+ mZoneType + " " + mPriceType + " " + "Giltig till "
				+ mValidTime + " " + mValidDate + "\n" + mZon + "\n\n" + mPrice
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
		if (mZone == R.id.region_zone_ulsl) {
			return "ULSL" + (bReduced ? "U" : "V");
		} else if (mZone == R.id.region_zone_bike) {
			return "ULCY";
		} else if (mZone == R.id.region_zone_ul12psl) {
			return "UL12PSL" + (bReduced ? "U" : "V");
		} else if (mZone == R.id.region_zone_ulslc) {
			return "ULSLC" + (bReduced ? "U" : "V");
		} else if (mZone == R.id.region_zone_ul2slc) {
			return "UL2SLC" + (bReduced ? "U" : "V");
		} else if (mZone == R.id.region_zone_ul2sl) {
			return "UL2SL" + (bReduced ? "U" : "V");
		} else {
			return "UL" + (bReduced ? "U" : "V") + mZoneOut;
		}
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
			mZoneType = "UL" + (bReduced ? "U" : "V") + "T";
			mPrice = bReduced ? 12 : 20;
			mZoneOut = "T";
			mZon = "Tätort";
			break;
		case R.id.region_zone_1:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "1";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "1";
			mZon = "Zon 1";
			break;
		case R.id.region_zone_2:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "2";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "2";
			mZon = "Zon 2";
			break;
		case R.id.region_zone_2p:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "2P";
			mPrice = bReduced ? 54 : 90;
			mZoneOut = "2P";
			mZon = "Zon 2 PLUS";
			break;
		case R.id.region_zone_12:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "12";
			mPrice = bReduced ? 54 : 90;
			mZoneOut = "12";
			mZon = "Län";
			break;
		case R.id.region_zone_12p:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "12P";
			mPrice = bReduced ? 81 : 135;
			mZoneOut = "12P";
			mZon = "Län PLUS";
			break;
		case R.id.region_zone_p:
			mZoneType = "UL" + (bReduced ? "U" : "V") + "P";
			mPrice = bReduced ? 27 : 45;
			mZoneOut = "P";
			mZon = "PLUS";
			break;
		case R.id.region_zone_ulsl:
			mZoneType = "UL" + "SL" + (bReduced ? "U" : "V");
			mPrice = bReduced ? 65 : 110;
			iExpireTime = 120;
			mZon = "UL + SL Län";
			break;
		case R.id.region_zone_bike:
			mZoneType = "ULCY";
			mPrice = 45;
			mZon = "Cykel";
			break;
		case R.id.region_zone_ul12psl:
			mZoneType = "UL" + "12PSL" + (bReduced ? "U" : "V");
			mPrice = bReduced ? 92 : 155;
			iExpireTime = 150;
			mZon = "UL Län PLUS + SL Län";
			break;
		case R.id.region_zone_ulslc:
			mZoneType = "UL" + "SLC" + (bReduced ? "U" : "V");
			mPrice = bReduced ? 50 : 90;
			iExpireTime = 120;
			mZon = "UL Län + SL zon C Nord";
			break;
		case R.id.region_zone_ul2slc:
			mZoneType = "UL" + "2SLC" + (bReduced ? "U" : "V");
			mPrice = bReduced ? 35 : 55;
			iExpireTime = 120;
			mZon = "UL zon 2 + SL zon C Nord";
			break;
		case R.id.region_zone_ul2sl:
			mZoneType = "UL" + "2SL" + (bReduced ? "U" : "V");
			mPrice = bReduced ? 45 : 80;
			iExpireTime = 120;
			mZon = "UL zon 2 + SL Län";
			break;
			
		}
		// Date and time
		mTime = new SimpleDateFormat("HHmm").format(now.getTime());
		mDay = new SimpleDateFormat("dd").format(now.getTime());

		cal.add(Calendar.MINUTE, iExpireTime);
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