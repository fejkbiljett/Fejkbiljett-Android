package com.iodgram.smsticket.tickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.os.Bundle;

public class UmeaTicket extends Ticket {

	public static int CHILD = 1;
	public static int YOUTH = 2;
	public static int ADULT = 3;
	public static int FLYING_BUS = 4;

	int iTicketType;
	String sTicketTypes[];
	int iPrices[];

	String sDateFrom, sDateTo;
	String sPhonenumber;
	String sCode;

	public UmeaTicket() {
		int numTicketTypes = 5;

		iPrices = new int[numTicketTypes];
		sTicketTypes = new String[numTicketTypes];

		iPrices[CHILD] = 13;
		iPrices[YOUTH] = 19;
		iPrices[ADULT] = 25;
		iPrices[FLYING_BUS] = 45;

		sTicketTypes[CHILD] = "Barn";
		sTicketTypes[YOUTH] = "Ungdom";
		sTicketTypes[ADULT] = "Vuxen";
		sTicketTypes[FLYING_BUS] = "Flygbuss";
	}

	@Override
	public void create(Bundle data) throws TicketException {
		Calendar cal = Calendar.getInstance();
		Random rand = new Random();

		iTicketType = data.getInt("ticket_type", ADULT);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sDateFrom = dateFormat.format(cal.getTime());
		cal.add(Calendar.MINUTE, 90);
		sDateTo = dateFormat.format(cal.getTime());

		String phone_number = data.getString("phone_number");
		if (phone_number == null) {
			phone_number = "731234567";
		}
		sPhonenumber = "46"+ phone_number;

		String sDay = new SimpleDateFormat("dd").format(cal.getTime());
		String sMinute = new SimpleDateFormat("mm").format(cal.getTime());

		sCode = "";
		sCode += Integer.parseInt(sDay.substring(0, 1))
				+ Integer.parseInt(sMinute.substring(0, 1));

		sCode += sPhonenumber.substring(sPhonenumber.length() - 1,
				sPhonenumber.length())
				+ sPhonenumber.substring(sPhonenumber.length() - 2,
						sPhonenumber.length() - 1);

		sCode += rand.nextInt(9);
		sCode += sDay;
		sCode += sMinute.substring(1, 2) + sMinute.substring(0, 1);
		sCode += rand.nextInt(9);
	}

	@Override
	public String getMessage() {
		return getSender() + "\n" + "Enkelbiljett\n" + "Umeå Tätort\n" + "Tid "
				+ sDateFrom + "\nGiltig till " + sDateTo + "\n"
				+ iPrices[iTicketType] + ",00 sek\n"
				+ sTicketTypes[iTicketType] + "\n" + "Till tel " + sPhonenumber
				+ "\nKod " + sCode;
	}

	@Override
	public String getSender() {
		return getNumberOut();
	}

	@Override
	public String getMessageOut() {
		return "VB v"+ sTicketTypes[iTicketType].substring(0, 1).toLowerCase();
	}

	@Override
	public String getNumberOut() {
		return "72322";
	}

}
