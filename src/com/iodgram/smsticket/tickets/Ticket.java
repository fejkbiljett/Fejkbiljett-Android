package com.iodgram.smsticket.tickets;

import android.os.Bundle;

public abstract class Ticket {
	public abstract void create(Bundle data) throws TicketException;

	public abstract String getMessage();

	public abstract String getSender();

	public abstract String getMessageOut();

	public abstract String getNumberOut();

	public class TicketException extends Exception {
		public TicketException(String string) {
			super(string);
		}

		private static final long serialVersionUID = -5833440917229755849L;
	}
}
