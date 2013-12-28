package com.fejkbiljett.android.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioButton;

import com.fejkbiljett.android.R;
import com.fejkbiljett.android.tickets.JonkopingTicket;
import com.fejkbiljett.android.tickets.Ticket;

public class JonkopingActivity extends TicketGeneratorActivity {
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ticket_jonkoping);

		mCityName = "Jönköping";

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

		/*
		int iZones = 0;
		if (((RadioButton) findViewById(R.id.zone_1)).isChecked()) {
			iZones = 1;
		} else if (((RadioButton) findViewById(R.id.zone_2)).isChecked()) {
			iZones = 2;
		} else if (((RadioButton) findViewById(R.id.zone_3)).isChecked()) {
			iZones = 3;
		} else if (((RadioButton) findViewById(R.id.zone_4)).isChecked()) {
			iZones = 4;
		} else if (((RadioButton) findViewById(R.id.zone_5)).isChecked()) {
			iZones = 5;
		} else if (((RadioButton) findViewById(R.id.zone_6)).isChecked()) {
			iZones = 6;
		} else if (((RadioButton) findViewById(R.id.zone_7)).isChecked()) {
			iZones = 7;
		}
		*/
		
		data.putBoolean("price_reduced", bReduced);
		//data.putInt("zones", iZones);

		return data;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		if (prefs.getBoolean("price_reduced", false)) {
			((RadioButton) findViewById(R.id.price_reduced)).setChecked(true);
		}

		/*
		switch (prefs.getInt("zones", 0)) {
		case 1:
			((RadioButton) findViewById(R.id.zone_1)).setChecked(true);
			break;
		case 2:
			((RadioButton) findViewById(R.id.zone_2)).setChecked(true);
			break;
		case 3:
			((RadioButton) findViewById(R.id.zone_3)).setChecked(true);
			break;
		case 4:
			((RadioButton) findViewById(R.id.zone_4)).setChecked(true);
			break;
		case 5:
			((RadioButton) findViewById(R.id.zone_5)).setChecked(true);
			break;
		case 6:
			((RadioButton) findViewById(R.id.zone_6)).setChecked(true);
			break;
		case 7:
			((RadioButton) findViewById(R.id.zone_7)).setChecked(true);
			break;
		default:
			//ERROR
			break;
		}
		*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		Bundle data = getParams();
		
		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));
		//editor.putInt("zones", data.getInt("zone"));

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new JonkopingTicket();
	}
}
