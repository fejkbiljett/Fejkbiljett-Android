package com.iodgram.smsticket.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.iodgram.smsticket.R;
import com.iodgram.smsticket.tickets.StockholmTicket;
import com.iodgram.smsticket.tickets.Ticket;

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

		boolean bZoneA = ((CheckBox) findViewById(R.id.zone_a)).isChecked();
		boolean bZoneB = ((CheckBox) findViewById(R.id.zone_b)).isChecked();
		boolean bZoneC = ((CheckBox) findViewById(R.id.zone_c)).isChecked();
		boolean bZoneL = ((CheckBox) findViewById(R.id.zone_l)).isChecked();
		
		data.putBoolean("price_reduced", bReduced);

		data.putBoolean("zone_a", bZoneA);
		data.putBoolean("zone_b", bZoneB);
		data.putBoolean("zone_c", bZoneC);
		data.putBoolean("zone_l", bZoneL);

		return data;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		if (prefs.getBoolean("price_reduced", false)) {
			((RadioButton) findViewById(R.id.price_reduced)).setChecked(true);
		}

		((CheckBox) findViewById(R.id.zone_a)).setChecked(prefs.getBoolean("zone_a", false));
		
		if (prefs.getBoolean("zone_b", false)) {
			((CheckBox) findViewById(R.id.zone_b)).setChecked(true);
		}
		if (prefs.getBoolean("zone_c", false)) {
			((CheckBox) findViewById(R.id.zone_c)).setChecked(true);
		}
		if (prefs.getBoolean("zone_l", false)) {
			((CheckBox) findViewById(R.id.zone_l)).setChecked(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		Bundle data = getParams();
		
		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));
		
		editor.putBoolean("zone_a", data.getBoolean("zone_a"));
		editor.putBoolean("zone_b", data.getBoolean("zone_b"));
		editor.putBoolean("zone_c", data.getBoolean("zone_c"));
		editor.putBoolean("zone_l", data.getBoolean("zone_l"));

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new StockholmTicket();
	}
}
