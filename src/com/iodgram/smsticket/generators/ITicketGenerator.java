package com.iodgram.smsticket.generators;

import android.os.Bundle;

import com.iodgram.smsticket.tickets.Ticket;

public interface ITicketGenerator {
	public Bundle getParams();
	public Ticket getTicket();
}
