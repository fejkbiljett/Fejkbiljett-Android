package com.iodgram.smsticket.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import com.iodgram.smsticket.R;
import com.iodgram.smsticket.tickets.Ticket;
import com.iodgram.smsticket.tickets.UmeaTicket;

public class UmeaActivity extends TicketGeneratorActivity {

	public void onCreate(Bundle savedInstanceState) {
		mCityName = "Umeå";

		super.onCreate(savedInstanceState);

		setContentView(R.layout.ticket_umea);
	}

	@Override
	public Bundle getParams() {
		Bundle params = new Bundle();

		int ticketType = 0;
		if (((RadioButton) findViewById(R.id.price_child)).isChecked())
			ticketType = UmeaTicket.CHILD;
		else if (((RadioButton) findViewById(R.id.price_youth)).isChecked())
			ticketType = UmeaTicket.YOUTH;
		else if (((RadioButton) findViewById(R.id.price_adult)).isChecked())
			ticketType = UmeaTicket.ADULT;
		else if (((RadioButton) findViewById(R.id.price_flying_bus))
				.isChecked())
			ticketType = UmeaTicket.FLYING_BUS;

		params.putInt("ticket_type", ticketType);

		params.putString("phone_number",
				((EditText) findViewById(R.id.phonenumber)).getText()
						.toString());

		return params;
	}

	@Override
	public Ticket getTicket() {
		return new UmeaTicket();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);

		int radioBtnId = 0;
		int ticketType = prefs.getInt("ticket_type", 0);
		if (ticketType == UmeaTicket.CHILD)
			radioBtnId = R.id.price_child;
		else if (ticketType == UmeaTicket.YOUTH)
			radioBtnId = R.id.price_youth;
		else if (ticketType == UmeaTicket.ADULT)
			radioBtnId = R.id.price_adult;
		else if (ticketType == UmeaTicket.FLYING_BUS)
			radioBtnId = R.id.price_flying_bus;

		if (radioBtnId > 0) {
			((RadioButton) findViewById(radioBtnId)).setChecked(true);
		}

		((EditText) findViewById(R.id.phonenumber)).setText(prefs.getString(
				"phone_number", "731234567"));
	}

	@Override
	protected void onPause() {
		super.onPause();

		Editor editor = getPreferences(MODE_PRIVATE).edit();
		Bundle data = getParams();

		editor.putInt("ticket_type", data.getInt("ticket_type"));
		editor.putString("phone_number", data.getString("phone_number"));

		editor.commit();
	}

}
