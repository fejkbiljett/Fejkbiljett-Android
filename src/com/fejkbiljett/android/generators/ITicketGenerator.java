package com.fejkbiljett.android.generators;

import android.os.Bundle;

import com.fejkbiljett.android.tickets.Ticket;

public interface ITicketGenerator {
	public Bundle getParams();
	public Ticket getTicket();
}
