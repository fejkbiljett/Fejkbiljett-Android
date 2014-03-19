package com.fejkbiljett.android.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;

import com.fejkbiljett.android.Utils;

public class UppsalaTicket extends Ticket {
	protected String mSender, uCode, uCodeTail, uScanCode= "";;
	
	
	protected String mPriceType, mValidDate, mValidTime, mZones = "";
	protected int mPrice;

	protected boolean mReduced;
	
	protected String[] mAllowedZoneCombinations = { "1", "2", "3", "4", 
												    "12", "123", "1234", "12345", 
													"23", "234", "34", "125", "25", "1235", 
													"1235SL", "12345SL", "12SLC", "123SLC", 
													"125SL", "2SLC", "25SL", "3SLC", "3SL" };
	protected int mZonePrice = 25;
	protected int mRedZonePrice = 15;
	
	protected int[] mSLcPrice = { 20, 15, 10 };
	protected int[] mRedSLcPrice = { 10, 10, 5 };
	
	protected int[] mSLabcPrice = { 50, 40, 30, 30, 25 };
	protected int[] mRedSLabcPrice = { 25, 20, 20, 15, 15 };
	
	@Override
	public String getMessage() {
		return  uCodeTail + " UL\n\n"
				
				+ (mReduced ? "U" : "V") + mZones + " " + mPriceType + " "
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
		return (mReduced ? "U" : "V") + mZones;
	}

	@Override
	public String getNumberOut() {
		return "0704202222";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		Calendar cal = Calendar.getInstance();
		
		mReduced = data.getBoolean("price_reduced");
		
		if(data.getBoolean("zone_1")) {
			mZones += "1";
		}
		if(data.getBoolean("zone_2")) {
			mZones += "2";
		}
		if(data.getBoolean("zone_3")) {
			mZones += "3";
		}
		if(data.getBoolean("zone_4")) {
			mZones += "4";
		}
		if(data.getBoolean("zone_5")) {
			mZones += "5";
		}
		
		int ulZones = mZones.length();
		if(ulZones < 1) {
			throw new TicketException(
					"Måste välja minst en zon i UL!"); //TODO: Translate
		}
		
		boolean slabc = data.getBoolean("zone_slabc");
		boolean slc = data.getBoolean("zone_slc");
		
		if(slabc && slc) {
			throw new TicketException(
					"Kan inte både ha zon \"SL ABC\" och \"SL C\"!"); //TODO: Translate
		}
		
		if(slabc) {
			mZones += "SL";
		}
		if(slc) {
			mZones += "SLC";
		}
		
		for(int i=0; i<mAllowedZoneCombinations.length;) {
			if(mZones.compareTo(mAllowedZoneCombinations[i]) == 0) {
				break;
			}
			if(++i >= mAllowedZoneCombinations.length) {
				throw new TicketException(
						"Ogiltig zonkombination!"); //TODO: Translate
			}
				
		}
		
		if (mReduced) {
			mPriceType = "UNGDOM";
			mPrice = mRedZonePrice * ulZones;
			if(slabc) {
				mPrice += mRedSLabcPrice[ulZones-1];
			}
			if(slc) {
				mPrice += mRedSLcPrice[ulZones-1];
			}
		} else {
			mPriceType = "VUXEN";
			mPrice = mZonePrice * ulZones;
			if(slabc) {
				mPrice += mSLabcPrice[ulZones-1];
			}
			if(slc) {
				mPrice += mSLcPrice[ulZones-1];
			}
		}
		
		int validMins = 75;
		if(ulZones==2) {
			validMins = 90;
		}
		else if(ulZones>2) {
			validMins = 120;
		}
		
		if(slabc || slc) {
			if(slabc && ulZones>2) {
				validMins = 150;
			}
			validMins = 120;
		}
			
		

		cal.add(Calendar.MINUTE, validMins);
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