package com.fejkbiljett.android.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.fejkbiljett.android.R;
import com.fejkbiljett.android.tickets.StockholmTicket;
import com.fejkbiljett.android.tickets.Ticket;

public class StockholmActivity extends TicketGeneratorActivity {
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ticket_stockholm);

		mCityName = "Stockholm";

		super.onCreate(savedInstanceState);
	}

	public Bundle getParams() {
		Bundle data = new Bundle();

		boolean bReduced;
		if (((RadioButton) findViewById(R.id.price_reduced)).isChecked()) {
			bReduced = true;
		} else {
			bReduced = false;
		}

		data.putBoolean("price_reduced", bReduced);

		return data;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		if (prefs.getBoolean("price_reduced", false)) {
			((RadioButton) findViewById(R.id.price_reduced)).setChecked(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		Bundle data = getParams();
		
		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));
	
		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new StockholmTicket();
	}
}
