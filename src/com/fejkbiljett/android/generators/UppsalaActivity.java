package com.fejkbiljett.android.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.fejkbiljett.android.R;
import com.fejkbiljett.android.tickets.UppsalaTicket;
import com.fejkbiljett.android.tickets.Ticket;

public class UppsalaActivity extends TicketGeneratorActivity {
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ticket_uppsala);

		mCityName = "Uppsla";

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

		boolean bZone1 = ((CheckBox) findViewById(R.id.zone_1)).isChecked();
		boolean bZone2 = ((CheckBox) findViewById(R.id.zone_2)).isChecked();
		boolean bZone3 = ((CheckBox) findViewById(R.id.zone_3)).isChecked();
		boolean bZone4 = ((CheckBox) findViewById(R.id.zone_4)).isChecked();
		boolean bZone5 = ((CheckBox) findViewById(R.id.zone_5)).isChecked();
		
		boolean bZoneSLABC = ((CheckBox) findViewById(R.id.zone_slabc)).isChecked();
		boolean bZoneSLC = ((CheckBox) findViewById(R.id.zone_slc)).isChecked();

		data.putBoolean("price_reduced", bReduced);

		data.putBoolean("zone_1", bZone1);
		data.putBoolean("zone_2", bZone2);
		data.putBoolean("zone_3", bZone3);
		data.putBoolean("zone_4", bZone4);
		data.putBoolean("zone_5", bZone5);
		
		data.putBoolean("zone_slabc", bZoneSLABC);
		data.putBoolean("zone_slc", bZoneSLC);
		
		return data;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);

		((RadioButton) findViewById(R.id.price_reduced))
			.setChecked(prefs.getBoolean("price_reduced", false));

		((CheckBox) findViewById(R.id.zone_1)).setChecked(prefs.getBoolean("zone_1", false));
		((CheckBox) findViewById(R.id.zone_2)).setChecked(prefs.getBoolean("zone_2", false));
		((CheckBox) findViewById(R.id.zone_3)).setChecked(prefs.getBoolean("zone_3", false));
		((CheckBox) findViewById(R.id.zone_4)).setChecked(prefs.getBoolean("zone_4", false));
		((CheckBox) findViewById(R.id.zone_5)).setChecked(prefs.getBoolean("zone_5", false));

		((CheckBox) findViewById(R.id.zone_slabc)).setChecked(prefs.getBoolean("zone_slabc", false));
		((CheckBox) findViewById(R.id.zone_slc)).setChecked(prefs.getBoolean("zone_slc", false));

	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		Bundle data = getParams();
		
		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));
		
		editor.putBoolean("zone_1", data.getBoolean("zone_1"));
		editor.putBoolean("zone_2", data.getBoolean("zone_2"));
		editor.putBoolean("zone_3", data.getBoolean("zone_3"));
		editor.putBoolean("zone_4", data.getBoolean("zone_4"));
		editor.putBoolean("zone_5", data.getBoolean("zone_5"));

		editor.putBoolean("zone_slabc", data.getBoolean("zone_slabc"));
		editor.putBoolean("zone_slc", data.getBoolean("zone_slc"));

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new UppsalaTicket();
	}
}
